package order;

import javax.jms.Message;
import javax.jms.MessageListener;

public class OrderListener implements MessageListener {

    public void onMessage(Message message) {
        System.out.println(message);
    }

}