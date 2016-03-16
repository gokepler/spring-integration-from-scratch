package order;

import java.util.UUID;

public class OrderProcessor {

	public Confirmation processOrder(Order order) {
		return new Confirmation(calculateConfirmationNumber(order));
	}

	private String calculateConfirmationNumber(Order order) {
		return UUID.randomUUID().toString();
	}
}
