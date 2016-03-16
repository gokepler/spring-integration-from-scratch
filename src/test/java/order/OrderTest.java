package order;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import my.springframework.integration.core.MessagingTemplate;
import my.springframework.messaging.MessageChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath*:order2-config.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class OrderTest {

	@Autowired OrderService orderService;
	@Autowired MessageChannel pollableChannel;
	
	@Test
	public void testFlow() throws Exception {
		Order order = new Order("1484");

		Confirmation confirmation = orderService.submitOrder(order);
		assertNotNull(confirmation);
		
		MessagingTemplate template = new MessagingTemplate();
		Order actual = template.receiveAndConvert(pollableChannel, Order.class);
		assertNotNull(actual);
		
		System.out.println("Received confirmation " + confirmation.getConfirmationNumber() + " for order of product " + actual.getProductCode());
	}

}
