package reward;

import java.io.Serializable;
import java.math.BigDecimal;

@SuppressWarnings("serial")
public class Dining implements Serializable {

	private BigDecimal amount;

	private String creditCardNumber;

	private String merchantNumber;

	public Dining(String amount, String creditCardNumber, String merchantNumber) {
		this.amount = new BigDecimal(amount);
		this.creditCardNumber = creditCardNumber;
		this.merchantNumber = merchantNumber;
	}

	/**
	 * Returns the amount of this dining--the total amount of the bill that was
	 * charged to the credit card.
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * Returns the number of the credit card used to pay for this dining. For
	 * this dining to be eligible for reward, this credit card number should be
	 * associated with a valid account in the reward network.
	 */
	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	/**
	 * Returns the merchant number of the restaurant where this dining occurred.
	 * For this dining to be eligible for reward, this merchant number should be
	 * associated with a valid restaurant in the reward network.
	 */
	public String getMerchantNumber() {
		return merchantNumber;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Dining)) {
			return false;
		}
		Dining other = (Dining) o;
		// value objects are equal if their attributes are equal
		return amount.equals(other.amount) && creditCardNumber.equals(other.creditCardNumber)
				&& merchantNumber.equals(other.merchantNumber);
	}

	@Override
	public int hashCode() {
		return amount.hashCode() + creditCardNumber.hashCode() + merchantNumber.hashCode();
	}

	@Override
	public String toString() {
		return "Dining of " + amount + " charged to '" + creditCardNumber + "' by '" + merchantNumber + "'";
	}
}