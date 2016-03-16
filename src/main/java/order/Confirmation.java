package order;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Confirmation implements Serializable {

	private String confirmationNumber;
	
	public Confirmation(String confirmationNumber) {
		this.confirmationNumber = confirmationNumber;
	}

	public String getConfirmationNumber() {
		return confirmationNumber;
	}

	@Override
	public String toString() {
		return String.format("Confirmation id is: %s", confirmationNumber);
	}

	@Override
	public int hashCode() {
		return confirmationNumber.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Confirmation)) {
			return false;
		}
		Confirmation other = (Confirmation) o;
		return confirmationNumber.equals(other.confirmationNumber);
	}
	
}
