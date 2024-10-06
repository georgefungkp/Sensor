package org.george_fung.com;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CentralMonitoringServiceTest {

    private final PrintStream standardOut = System.out;
    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errStreamCaptor));
    }


    @AfterEach
    public void tearDown() {
        System.setOut(standardOut); // Revert back to the standard output stream
        System.setErr((standardErr)); // Revert back to the standard error stream
    }


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

        // Verify the format error message is printed
        assertThrows(IllegalArgumentException.class, ()-> service.processMessage(testMessage), "The format of reading is not correct");

    }


}
