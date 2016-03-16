package order;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath*:order-config.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class OrderTest {

	@Autowired OrderService orderService;

	@Test
	public void runTheFlow() throws Exception {
		Order order = new Order("1484");

		Confirmation confirmation = orderService.submitOrder(order);
		assertNotNull(confirmation);
	}

}
