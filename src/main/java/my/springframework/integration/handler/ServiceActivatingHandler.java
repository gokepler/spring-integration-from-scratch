package my.springframework.integration.handler;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import my.springframework.integration.support.MessageBuilder;
import my.springframework.messaging.Message;
import my.springframework.messaging.MessagingException;

public class ServiceActivatingHandler extends AbstractMessageProducingHandler {

	private Object ref;

	public void setRef(Object ref) {
		this.ref = ref;
	}
	
	@Override
	protected void handleMessageInternal(Message<?> message) throws Exception {
		
		// Simplification, ignore method on superclasses
		// Do not support methods accepting or returning Message<?>, requiring header values, ...
		for (Method eachMethod : ref.getClass().getDeclaredMethods()) {
			if (eachMethod.getModifiers() == Modifier.PUBLIC
					&& eachMethod.getParameterTypes().length == 1 
					&& eachMethod.getParameterTypes()[0].isAssignableFrom(message.getPayload().getClass())) {
				Object response = eachMethod.invoke(ref, message.getPayload());
				Message<?> outputMessage = MessageBuilder.withPayload(response).copyHeaders(message.getHeaders()).build();
				getReplyChannel(message).send(outputMessage);
				return;
			}
		}

		throw new MessagingException(message, "Unable to find method on Service Acticator");
	}


}
