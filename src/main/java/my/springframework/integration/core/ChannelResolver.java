package my.springframework.integration.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;

import my.springframework.messaging.MessageChannel;
import my.springframework.messaging.MessagingException;

public class ChannelResolver {

	private BeanFactory beanFactory;

	public ChannelResolver(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}
	
	/**
	 * Resolve the given destination name.
	 * @param name the destination name to resolve
	 * @return the resolved destination (never {@code null})
	 * @throws DestinationResolutionException if the specified destination
	 * wasn't found or wasn't resolvable for any other reason
	 */
	public MessageChannel resolveDestination(String destinationName) throws MessagingException {
		Assert.state(this.beanFactory != null, "No BeanFactory configured");
		try {
			return this.beanFactory.getBean(destinationName, MessageChannel.class);
		}
		catch (BeansException ex) {
			throw new MessagingException(
					"Failed to find MessageChannel bean with name '" + destinationName + "'", ex);
		}
	}
}

