package org.george_fung.com.util.message_broker_service;

import org.george_fung.com.util.MessageHandler;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.jms.JMSException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BrokerMessageServiceTest {

    private BrokerMessageService messageService;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
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
    void shouldStartService() throws JMSException {
        BrokerMessageService instance = BrokerMessageService.getService("test");
        assertNotNull(instance, "Check instance is not null");
    }

    @Test
    public void testPutAndGetSyn() throws JMSException {
        assertDoesNotThrow(() -> messageService.put("Hello, World!"), "Check if put does not throw an exception");

//        messageService.put("Hello, World!");
        String message = messageService.get();
        assertEquals("Hello, World!", message);
    }

    @Test
    public void testGet_NoMessage() {
//        String message;
        assertDoesNotThrow(() -> {
            String message = messageService.get();
            assertEquals("", message);
        }, "Check if put does not throw an exception");
    }

    /**
     * Given that the run method in the Runnable interface requires no parameters and returns void,
     * any lambda expression of the form () -> { statements } conforms to the Runnable interface
     * because it also takes no parameters and returns void.
     *
     * @throws Exception
     */
    @Test
    void testGetAsyncMethod() throws Exception {
        MessageHandler mockMessageHandler = mock(MessageHandler.class);

        doNothing().when(mockMessageHandler).processMessage(any());
        messageService.put("Async Message");
        Thread t = new Thread(() -> {
            try {
                messageService.getAsync(mockMessageHandler);
            } catch (JMSException _) {
            }
        });
        t.start();
        Thread.sleep(2000);
        t.interrupt();
        // Verify that the method was called
        Mockito.verify(mockMessageHandler, times(1)).processMessage(any(String.class));
    }
}
