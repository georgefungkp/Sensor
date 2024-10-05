package org.george_fung.com.util.message_broker_service;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.george_fung.com.util.MessageHandler;
import org.junit.jupiter.api.*;

import javax.jms.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BrokerMessageServiceTest {

    private BrokerMessageService messageService;

    @BeforeEach
    public void setUp() throws Exception {
        messageService = BrokerMessageService.getService("test");

        Assertions.assertDoesNotThrow(() -> {
            messageService = BrokerMessageService.getService("test");
        });
    }

    @AfterEach
    public void tearDown() throws JMSException {
        // Clean up
        BrokerMessageService.stopService();
    }

    @Test
    public void testPutAndGetSyn() throws JMSException {
        messageService.put("Hello, World!");
        String message = messageService.get();
        assertEquals("Hello, World!", message);
    }

    @Test
    public void testGet_NoMessage() throws JMSException {
        String message = messageService.get();
        assertEquals("", message);
    }

    @Disabled
    @Test
    public void testGetAsync() throws JMSException {
        messageService.put("Async Message");

        MessageHandler mockHandler = mock(MessageHandler.class);
        // Run async method
        messageService.getAsync(mockHandler);

//        doAnswer(invocation -> {
//            // Simulate receiving a message
//            mockHandler.processMessage("Async message");
//            return null;
//        }).when(mockConsumer).setMessageListener(any(MessageListener.class));

    }
}
