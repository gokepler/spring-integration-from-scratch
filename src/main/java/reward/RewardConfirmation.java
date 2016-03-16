package reward;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RewardConfirmation implements Serializable {

	private String confirmationNumber;
	
	public RewardConfirmation(String confirmationNumber) {
		this.confirmationNumber = confirmationNumber;
	}

	/**
	 * Returns the confirmation number of the reward transaction. Can be used later to lookup the transaction record.
	 */
	public String getConfirmationNumber() {
		return confirmationNumber;
	}


	@Override
	public String toString() {
		return String.format("RewardConfirmation id is: %s", confirmationNumber);
	}

	@Override
	public int hashCode() {
		return confirmationNumber.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof RewardConfirmation)) {
			return false;
		}
		RewardConfirmation other = (RewardConfirmation) o;
		return confirmationNumber.equals(other.confirmationNumber);
	}
	
}
