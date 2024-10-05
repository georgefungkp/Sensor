package ignore;

import javax.jms.*;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class ArtemisExample {
    public static void main(String[] args) throws JMSException {
        // Create a connection factory
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        // Create a connection
        Connection connection = factory.createConnection();
        connection.start();

        // Create a session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create a queue
        Queue queue = session.createQueue("exampleQueue");

        // Create a producer
        MessageProducer producer = session.createProducer(queue);
        TextMessage message = session.createTextMessage("Hello, ActiveMQ Artemis!");
        producer.send(message);

        // Create a consumer
        MessageConsumer consumer = session.createConsumer(queue);
        TextMessage receivedMessage = (TextMessage) consumer.receive(5000);
        System.out.println("Received message: " + receivedMessage.getText());

        // Clean up
        session.close();
        connection.close();
    }
}
