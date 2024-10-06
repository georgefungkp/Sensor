package org.george_fung.com;

import org.george_fung.com.util.message_broker_service.BrokerMessageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.jms.JMSException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CentralMonitoringServiceTest {

    private final PrintStream standardOut = System.out;
    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() throws JMSException {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errStreamCaptor));
    }


    @AfterEach
    public void tearDown() {
        System.setOut(standardOut); // Revert back to the standard output stream
        System.setErr((standardErr)); // Revert back to the standard error stream
    }

//    @Test
//    void testStartService() throws Exception {
//        // Ensure that starting the service doesn't throw an exception
//        CentralMonitoringService mockCentralMonitor = Mockito.mock(CentralMonitoringService.class);
//        assertDoesNotThrow(mockCentralMonitor::startService);
//
//        // Verify that the async message retrieval is triggered
//        verify(mockCentralMonitor, times(1)).getAsync(any(CentralMonitoringService.class));
//    }


    @Test
    public void testProcessMessageWithValidTemperatureSensorData() {
        String testMessage = "type=t1; reading=0"; // example message

        CentralMonitoringService service = Mockito.mock(CentralMonitoringService.class);
        doCallRealMethod().when(service).processMessage(testMessage);

        service.processMessage(testMessage);

        verify(service, times(1)).processMessage(testMessage);
        assertFalse(errStreamCaptor.toString().trim().contains("ALARM: TEMPERATURE Sensor"));
    }


    @Test
    public void testProcessMessageWithInvalidTemperatureSensorData() {
        String testMessage = "type=t1; reading=50"; // example message

        CentralMonitoringService service = Mockito.mock(CentralMonitoringService.class);
        doCallRealMethod().when(service).processMessage(testMessage);

        service.processMessage(testMessage);

        verify(service, times(1)).processMessage(testMessage);

        assertTrue(errStreamCaptor.toString().trim().contains("ALARM: TEMPERATURE Sensor - t1 threshold exceeded! Value: 50"));
        assertFalse(errStreamCaptor.toString().trim().contains("ALARM: TEMPERATURE Sensor - t1 threshold exceeded! Value: 51"));
    }


    @Test
    void testProcessMessageWithValidHumiditySensorData() {
        // Simulate receiving a humidity sensor message
        String testMessage = "sensor=h1; value=0";

        CentralMonitoringService service = Mockito.mock(CentralMonitoringService.class);
        doCallRealMethod().when(service).processMessage(testMessage);

        service.processMessage(testMessage);

        verify(service, times(1)).processMessage(testMessage);
        assertFalse(errStreamCaptor.toString().trim().contains("ALARM: TEMPERATURE Sensor"));
    }


    @Test
    public void testProcessMessageWithInvalidHumiditySensorData() {
        String testMessage = "type=h1; reading=60"; // example message

        CentralMonitoringService service = Mockito.mock(CentralMonitoringService.class);
        doCallRealMethod().when(service).processMessage(testMessage);

        service.processMessage(testMessage);

        verify(service, times(1)).processMessage(testMessage);

        assertTrue(errStreamCaptor.toString().trim().contains("ALARM: HUMIDITY Sensor - h1 threshold exceeded! Value: 60"));
        assertFalse(errStreamCaptor.toString().trim().contains("ALARM: HUMIDITY Sensor - h1 threshold exceeded! Value: 61"));
    }


    @Test
    void testProcessMessageWithInvalidSensorType() {
        // Simulate receiving a message with an unknown sensor type
        String testMessage = "sensor=Z1; value=25";  // Z is not a valid sensor type

        CentralMonitoringService service = Mockito.mock(CentralMonitoringService.class);
        doCallRealMethod().when(service).processMessage(testMessage);

        service.processMessage(testMessage);

        // Verify the error message is printed
        assertTrue(errStreamCaptor.toString().trim().contains("The sensor type is wrong. value:Z"));
    }

    @Test
    void testProcessMessageWithInvalidValue() {
        // Simulate receiving a message with an invalid value
        String testMessage = "sensor=t1; value=abc";  // Value is not a number

        CentralMonitoringService service = Mockito.mock(CentralMonitoringService.class);
        doCallRealMethod().when(service).processMessage(testMessage);

        service.processMessage(testMessage);

        // Verify the format error message is printed
        assertTrue(errStreamCaptor.toString().trim().contains("The format of reading abc is not correct"));
    }

//    @Test
//    void testStopService() {
//        // Test stopping the service
//        assertDoesNotThrow(() -> {
//            centralMonitoringService.stopService();
//        });
//
//        // Check if stop service message is printed
//        verify(System.out, times(1)).println("stopping...");
//    }

//    @Test
//    void testMainMethod() {
//        // Test the main method to ensure it doesn't throw any exceptions
//        assertDoesNotThrow(() -> {
//            String[] args = {"test"};
//            CentralMonitoringService.main(args);
//        });
//    }


}
