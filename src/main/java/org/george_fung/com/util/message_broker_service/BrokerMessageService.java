package org.george_fung.com.util.message_broker_service;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.george_fung.com.util.MessageHandler;
import org.george_fung.com.util.Misc;

import javax.jms.*;
import java.util.Map;

public final class BrokerMessageService{
    private static Map<String, Object> configMap;
    private static BrokerMessageService service;
    private static ActiveMQConnectionFactory factory;
    private static Connection connection;

    /**
     * Singleton. Forbid any object creation from other classes
     */
    private BrokerMessageService() {
    }

    public static synchronized BrokerMessageService getService(String env) throws JMSException {
        if (service == null) {
            service = new BrokerMessageService();
            configMap = Misc.getSettings(env);
            int portNo = (Integer) configMap.get("active_mq.port");
            // Create a connection factory
            factory = new ActiveMQConnectionFactory("tcp://localhost:" + portNo);
            // Create a connection
            factory.setReconnectAttempts(10);
            factory.setRetryInterval(1000);
            factory.setConnectionTTL(100000);
            connection = factory.createConnection();
            connection.start();
            // Create a session
//            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // Create a queue
//            Queue queue = session.createQueue((String) configMap.get("active_mq.queue"));
            // Create a producer
//            producer = session.createProducer(queue);
            // Create a consumer
//            consumer = session.createConsumer(queue);
        }
        return service;
    }

    /**
     * Actions before program ends of Message Broker
     *
     * @throws JMSException
     */
    public static void stopService() throws JMSException {
        // Clean up
        connection.close();
        factory.close();
        service = null;
    }

//    public static void main(String[] args) throws JMSException, InterruptedException {
//        BrokerMessageService messageService = BrokerMessageService.getService("dev");
//        messageService.put("Hi");
//        Thread.sleep(1000);
//        System.out.println(messageService.get());
//    }

    /**
     * Send the message to message broker
     *
     * @param message Message to be sent
     * @throws JMSException
     */
    public void put(String message) throws JMSException {
        // Create a session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // Access a queue
        Queue queue = session.createQueue((String) configMap.get("active_mq.queue"));
        // Create a producer
        MessageProducer producer = session.createProducer(queue);
        producer.send(session.createTextMessage(message));
        producer.close();
        session.close();
    }

    /**
     * Get the message from message broker by Pull
     *
     * @return message received from message broker
     * @throws JMSException
     */
    public String get() throws JMSException {
        // Create a session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // Access a queue
        Queue queue = session.createQueue((String) configMap.get("active_mq.queue"));
        // Create a consumer

        MessageConsumer consumer = session.createConsumer(queue);
        TextMessage message = (TextMessage) consumer.receiveNoWait();
        consumer.close();
        session.close();
        if (message != null)
            return message.getText();
        else
            return "";
//        return ((TextMessage)consumer.receive()).getText();
    }

    /**
     * Get the message from message broker asynchronously (Push by message broker)
     *
     * @param messageHandler Object that processes message
     * @throws JMSException
     */
    public void getAsync(MessageHandler messageHandler) throws JMSException {
        // Create a session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        // Access a queue
        Queue queue = session.createQueue((String) configMap.get("active_mq.queue"));
        // Create a consumer
        MessageConsumer consumer = session.createConsumer(queue);
        consumer.setMessageListener(message -> {
            if (message instanceof TextMessage) {
                try {
                    TextMessage textMessage = (TextMessage) message;
                    System.out.println("Received message: " + textMessage.getText());
                    messageHandler.processMessage(textMessage.getText());
                } catch (JMSException e) {
                    System.err.println("System error. Close the service");
                    try {
                        stopService();
                    } catch (JMSException ex) {
                        throw new RuntimeException(ex);
                    }
                }catch (Exception e){
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        });

        // Keep the program running to listen for messages
        System.out.println("Listening the coming message...");
        while(true) {
            try {
                Thread.sleep(1000); // Keep the program running
            } catch (InterruptedException _) {
                break;
            }
        }

        consumer.close();
        session.close();
    }
}
