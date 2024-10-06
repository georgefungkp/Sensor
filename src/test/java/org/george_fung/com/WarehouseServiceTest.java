package org.george_fung.com;

import org.george_fung.com.util.message_broker_service.BrokerMessageService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.jms.JMSException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class WarehouseServiceTest {

    private final PrintStream standardOut = System.out;
    private final PrintStream standardErr = System.err;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final ByteArrayOutputStream errStreamCaptor = new ByteArrayOutputStream();

    //    @InjectMocks
    private WarehouseService warehouseService;

    @BeforeEach
    public void setup() throws JMSException {
        warehouseService = new WarehouseService("test", "Warehouse One");
        System.setOut(new PrintStream(outputStreamCaptor));
        System.setErr(new PrintStream(errStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        warehouseService = null;
        System.setOut(standardOut); // Revert back to the standard output stream
        System.setErr((standardErr)); // Revert back to the standard error stream
    }

    @Test
    void testStartService() {
        assertDoesNotThrow(() -> warehouseService.startService());
        // Here we only check does not throw any exceptions while executing startService method
    }

    @Test
    void testStopService() {
        assertDoesNotThrow(() -> warehouseService.stopService());
        // Here we only check does not throw any exceptions while executing stopService method
    }


    @Test
    public void testListenForSensorData() throws InterruptedException, JMSException, IllegalAccessException, NoSuchFieldException, IOException {
        WarehouseService warehouseService = new WarehouseService("test", "Warehouse One");
        BrokerMessageService mockBrokerService = mock(BrokerMessageService.class);
        int port = 5555;
        String testMessage = "sensor=h1; value=50";
        Field field = WarehouseService.class.getDeclaredField("service");
        field.setAccessible(true);
        field.set(warehouseService, mockBrokerService);
        doNothing().when(mockBrokerService).put(anyString());

        // Create a thread to execute listenForSensorData
        Thread thread = new Thread(() -> {
            try {
                warehouseService.listenForSensorData(port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Start the thread and send a UDP packet to listenForSensorData
        thread.start();
        sendUDPMessage(testMessage, port);

        // Let the thread execute for a short time to receive the UDP packet
        Thread.sleep(2000);

        // Verify that sendToCentralMonitoringService was called with the expected argument
        verify(mockBrokerService, times(1)).put(testMessage);

        assertTrue(outputStreamCaptor.toString().trim().contains(testMessage));


        // Close the created threads
        thread.interrupt();
    }

    @Test
    void testMainMethod() {
        // Test the main method to ensure it doesn't throw any exceptions
        assertDoesNotThrow(() -> {
            String[] args = {"test", "1"};
            WarehouseService.main(args);
        });
    }

    private void sendUDPMessage(String message, int port) throws IOException {
        byte[] buffer = message.getBytes();
        InetAddress address = InetAddress.getLocalHost();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        DatagramSocket datagramSocket = new DatagramSocket();
        datagramSocket.send(packet);
        datagramSocket.close();
    }
}