package org.george_fung.com;

import org.george_fung.com.WarehouseService;
import org.george_fung.com.util.Misc;
import org.george_fung.com.util.message_broker_service.BrokerMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.jms.JMSException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class WarehouseServiceTest {

    private WarehouseService warehouseService;
    private BrokerMessageService mockBrokerService;
    private DatagramSocket mockSocket;
    private Map<String, Object> configMap;

    @BeforeEach
    void setUp() throws JMSException {
        // Mock the settings and BrokerMessageService
        mockBrokerService = mock(BrokerMessageService.class);
        mockSocket = mock(DatagramSocket.class);
        configMap = new HashMap<>();
        configMap.put("sensors.temperature.udp_port", 1234);
        configMap.put("sensors.humidity.udp_port", 5678);

        // Mock Misc.getSettings to return the configMap
        Mockito.mockStatic(Misc.class);
        when(Misc.getSettings("testEnv")).thenReturn(configMap);

        // Mock BrokerMessageService.getService to return mockBrokerService
        Mockito.mockStatic(BrokerMessageService.class);
        when(BrokerMessageService.getService("testEnv")).thenReturn(mockBrokerService);

        // Create WarehouseService instance
        warehouseService = new WarehouseService("testEnv", "Test Warehouse");
    }

    @Test
    void testStartService() {
        // Ensure that starting the service doesn't throw an exception
        assertDoesNotThrow(() -> {
            warehouseService.startService();
        });
    }

    @Test
    void testListenForSensorData() throws IOException, JMSException {
        // Mock the data packet
        byte[] mockData = "Temperature: 25".getBytes();
        DatagramPacket mockPacket = new DatagramPacket(mockData, mockData.length);

        // Stub the receive method to simulate receiving data
        doAnswer(invocation -> {
            // Simulate receiving the mock packet
            DatagramSocket socket = invocation.getArgument(0);
            socket.receive(mockPacket);
            return null;
        }).when(mockSocket).receive(any(DatagramPacket.class));

        // Invoke listenForSensorData method via reflection
        assertDoesNotThrow(() -> {
            // Normally private, so we need to use reflection or invoke it indirectly via startService.
            warehouseService.startService();
        });

        // Verify that the message is sent to the message broker
        verify(mockBrokerService, times(1)).put("Temperature: 25");
    }

    @Test
    void testStopService() {
        // Test stopping the service
        assertDoesNotThrow(() -> {
            warehouseService.stopService();
        });
    }

    @Test
    void testMainMethod() {
        // Test the main method to ensure it doesn't throw any exceptions
        assertDoesNotThrow(() -> {
            String[] args = {"testEnv"};
            WarehouseService.main(args);
        });
    }
}
