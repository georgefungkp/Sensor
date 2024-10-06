package org.george_fung.com;

import org.george_fung.com.util.message_broker_service.BrokerMessageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.jms.JMSException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class WarehouseServiceTest {

    @Mock
    DatagramSocket mockSocket;

    @Mock
    DatagramPacket mockPacket;

    @Mock
    BrokerMessageService brokerService;

    //    @InjectMocks
    WarehouseService warehouseService;

    @BeforeEach
    public void setup() throws JMSException {
        MockitoAnnotations.openMocks(this);
        warehouseService = new WarehouseService("test", "WarehouseOne");
    }

    @AfterEach
    public void tearDown() {
        warehouseService = null;
        brokerService = null;
    }

    @Test
    public void testListenForSensorDataSuccess() throws JMSException, IOException {
        warehouseService = mock(WarehouseService.class);
        doNothing().when(mockSocket).receive(any(DatagramPacket.class));
        when(mockPacket.getData()).thenReturn("Test Data from Sensors".getBytes());
        doNothing().when(brokerService).put(any(String.class));

        warehouseService.listenForSensorData(8080);
    }

    // And so on...
}