package reward;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath*:reward-config.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class RewardTest {

	@Autowired JmsTemplate jmsTemplate;

	@Test
	public void runTheFlow() throws Exception {
		Dining dining = new Dining("80.93", "1234123412341234", "1234567890");

		jmsTemplate.convertAndSend("rewards.queue.dining", dining);
		RewardConfirmation confirmation = (RewardConfirmation) jmsTemplate.receiveAndConvert("rewards.queue.confirmation");

		assertNotNull(confirmation);
	}

}
