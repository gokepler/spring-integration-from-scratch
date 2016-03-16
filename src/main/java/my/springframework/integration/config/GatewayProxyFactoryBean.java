package my.springframework.integration.config;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.ClassUtils;

import my.springframework.integration.core.ChannelResolver;
import my.springframework.integration.core.MessagingTemplate;
import my.springframework.integration.support.MessageBuilder;
import my.springframework.messaging.Message;
import my.springframework.messaging.MessageChannel;
import my.springframework.messaging.MessagingException;

public class GatewayProxyFactoryBean implements FactoryBean<Object>, BeanFactoryAware, MethodInterceptor {

	private volatile Class<?> serviceInterface;
	private volatile MessageChannel defaultRequestChannel;
		
	private MessagingTemplate messagingTemplate;
	
	public GatewayProxyFactoryBean(Class<?> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	public void setDefaultRequestChannel(MessageChannel defaultRequestChannel) {
		this.defaultRequestChannel = defaultRequestChannel;
	}
	
	@Override
	public Class<?> getObjectType() {
		return (this.serviceInterface != null ? this.serviceInterface : null);
	}

	private volatile Object serviceProxy;

	@Override
	public Object getObject() throws Exception {
		Class<?> proxyInterface = this.serviceInterface;
		serviceProxy = new ProxyFactory(proxyInterface, this).getProxy(ClassUtils.getDefaultClassLoader());
		return serviceProxy;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.messagingTemplate = new MessagingTemplate();
		messagingTemplate.setChannelResolver(new ChannelResolver(beanFactory));
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method method = invocation.getMethod();
		if (AopUtils.isToStringMethod(method)) {
			return "gateway proxy for service interface [" + this.serviceInterface + "]";
		}
		try {
			Message<Object> request = MessageBuilder.withPayload(invocation.getArguments()[0]).build();
			Message<?> response = messagingTemplate.sendAndReceive(defaultRequestChannel, request);
			return response.getPayload();
		}
		catch (Throwable e) {//NOSONAR
			this.rethrowExceptionCauseIfPossible(e, invocation.getMethod());
			return null; // preceding call should always throw something
		}
	}

	private void rethrowExceptionCauseIfPossible(Throwable originalException, Method method) throws Throwable {
		Class<?>[] exceptionTypes = method.getExceptionTypes();
		Throwable t = originalException;
		while (t != null) {
			for (Class<?> exceptionType : exceptionTypes) {
				if (exceptionType.isAssignableFrom(t.getClass())) {
					throw t;
				}
			}
			if (t instanceof RuntimeException
					&& !(t instanceof MessagingException)
					&& !(t instanceof UndeclaredThrowableException)
					&& !(t instanceof IllegalStateException && ("Unexpected exception thrown").equals(t.getMessage()))) {
				throw t;
			}
			t = t.getCause();
		}
		throw originalException;
	}

}
