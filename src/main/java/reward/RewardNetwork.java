package reward;

import java.util.UUID;

public class RewardNetwork {

	public RewardConfirmation rewardAccountFor(Dining dining) {
		return new RewardConfirmation(calculateConfirmationNumber(dining));
	}

	private String calculateConfirmationNumber(Dining dining) {
		return UUID.randomUUID().toString();
	}
}
