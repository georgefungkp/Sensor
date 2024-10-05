package org.george_fung.com;

import org.george_fung.com.util.Misc;
import org.george_fung.com.util.message_broker_service.BrokerMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.jms.JMSException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CentralMonitoringServiceTest {

    private CentralMonitoringService centralMonitoringService;
    private BrokerMessageService mockBrokerService;
    private Map<String, Object> configMap;

    @BeforeEach
    void setUp() throws JMSException {
        // Mock the settings and BrokerMessageService
        mockBrokerService = mock(BrokerMessageService.class);
        configMap = new HashMap<>();
        configMap.put("sensors.temperature.threshold", 30);  // Set thresholds for test
        configMap.put("sensors.humidity.threshold", 50);

        // Mock Misc.getSettings to return the configMap
        Mockito.mockStatic(Misc.class);
        when(Misc.getSettings("testEnv")).thenReturn(configMap);

        // Mock BrokerMessageService.getService to return mockBrokerService
        Mockito.mockStatic(BrokerMessageService.class);
        when(BrokerMessageService.getService("testEnv")).thenReturn(mockBrokerService);

        // Create CentralMonitoringService instance
        centralMonitoringService = new CentralMonitoringService("testEnv");
    }

    @Test
    void testStartService() throws Exception {
        // Ensure that starting the service doesn't throw an exception
        assertDoesNotThrow(() -> {
            centralMonitoringService.startService();
        });

        // Verify that the async message retrieval is triggered
        verify(mockBrokerService, times(1)).getAsync(any(CentralMonitoringService.class));
    }

    @Test
    void testProcessMessageWithValidTemperatureSensorData() {
        // Simulate receiving a temperature sensor message
        String message = "sensor=T1; value=35";  // 35 exceeds the threshold of 30

        // Capture system output
        ArgumentCaptor<String> systemOutCaptor = ArgumentCaptor.forClass(String.class);
        System.setErr(mockPrintStream(systemOutCaptor));

        centralMonitoringService.processMessage(message);

        // Verify the ALARM message is printed
        assertTrue(systemOutCaptor.getValue().contains("ALARM: TEMPERATURE Sensor - T1 threshold exceeded! Value: 35"));
    }

    @Test
    void testProcessMessageWithValidHumiditySensorData() {
        // Simulate receiving a humidity sensor message
        String message = "sensor=H1; value=60";  // 60 exceeds the threshold of 50

        // Capture system output
        ArgumentCaptor<String> systemOutCaptor = ArgumentCaptor.forClass(String.class);
        System.setErr(mockPrintStream(systemOutCaptor));

        centralMonitoringService.processMessage(message);

        // Verify the ALARM message is printed
        assertTrue(systemOutCaptor.getValue().contains("ALARM: HUMIDITY Sensor - H1 threshold exceeded! Value: 60"));
    }

    @Test
    void testProcessMessageWithInvalidSensorType() {
        // Simulate receiving a message with an unknown sensor type
        String message = "sensor=Z1; value=25";  // Z is not a valid sensor type

        // Capture system output
        ArgumentCaptor<String> systemOutCaptor = ArgumentCaptor.forClass(String.class);
        System.setErr(mockPrintStream(systemOutCaptor));

        centralMonitoringService.processMessage(message);

        // Verify the error message is printed
        assertTrue(systemOutCaptor.getValue().contains("Sensor type Z not found"));
    }

    @Test
    void testProcessMessageWithInvalidValue() {
        // Simulate receiving a message with an invalid value
        String message = "sensor=T1; value=abc";  // Value is not a number

        // Capture system output
        ArgumentCaptor<String> systemOutCaptor = ArgumentCaptor.forClass(String.class);
        System.setErr(mockPrintStream(systemOutCaptor));

        centralMonitoringService.processMessage(message);

        // Verify the format error message is printed
        assertTrue(systemOutCaptor.getValue().contains("The format of reading abc is not correct"));
    }

    @Test
    void testStopService() {
        // Test stopping the service
        assertDoesNotThrow(() -> {
            centralMonitoringService.stopService();
        });

        // Check if stop service message is printed
        verify(System.out, times(1)).println("stopping...");
    }

    @Test
    void testMainMethod() {
        // Test the main method to ensure it doesn't throw any exceptions
        assertDoesNotThrow(() -> {
            String[] args = {"testEnv"};
            CentralMonitoringService.main(args);
        });
    }

    // Helper method to mock system output streams for testing print messages
    private PrintStream mockPrintStream(ArgumentCaptor<String> captor) {
        PrintStream mockPrintStream = mock(PrintStream.class);
        doAnswer(invocation -> {
            Object message = invocation.getArgument(0);
            captor.capture();
            System.err.println(message);  // Print normally to avoid confusion in the test output
            return null;
        }).when(mockPrintStream).println(anyString());
        return mockPrintStream;
    }
}
