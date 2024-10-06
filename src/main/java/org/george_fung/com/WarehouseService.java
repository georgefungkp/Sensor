package org.george_fung.com;

import org.george_fung.com.util.Misc;
import org.george_fung.com.util.message_broker_service.BrokerMessageService;

import javax.jms.JMSException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Map;

/**
 * Client side should use the following tip to communicate with Ware house service
 * <a href="https://help.ubidots.com/en/articles/937233-sending-tcp-udp-packets-using-netcat#test-your-netcat-understanding-as-a-client-server">...</a>
 */
public class WarehouseService implements Service {
    private final Map<String, Object> configMap;
    String name;
    BrokerMessageService service;

    public WarehouseService(String env, String name) throws JMSException {
        this.configMap = Misc.getSettings(env);
        this.name = name;
        this.service = BrokerMessageService.getService(env);
        System.out.println("Warehouse Service starts and is waiting monitoring statistics from sensors...");
    }

    /**
     * Listen data from sensors
     * @param port listening port number
     * @throws SocketException
     */
    void listenForSensorData(int port) throws IOException {
        DatagramSocket socket = new DatagramSocket(port);

        try (socket) {
            byte[] buffer = new byte[256];
            do {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                received = received.replaceAll("[\\n\\r\\t]", "");
                System.out.println("Received: " + received);

                this.sendToCentralMonitoringService(received);
            } while (true);
        } catch (SocketException _) {
            System.err.println("Central Monitoring Service is stopped unexpected");
            this.stopService();
        } catch (Throwable e) {
            System.err.println(e.fillInStackTrace().toString());

        } // Auto close socket
    }

    /**
     * Send the message to message broker
     * @param message message to be sent
     * @throws JMSException
     */
    private void sendToCentralMonitoringService(String message) throws JMSException {
        // Send message to the server
        service.put(message);
    }


    /**
     * For action of stoppage of service before program ends
     */
    @Override
    public void stopService() {
        System.out.println("Warehouse Service is closed");
    }

    /**
     * Create threads to wait data from UDP port
     * One thread for one type of sensor.
     */
    @Override
    public void startService() {
        Thread tempThread = new Thread(() -> {
            try {
                listenForSensorData((Integer) configMap.get("sensors.temperature.udp_port"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Thread humidityThread = new Thread(() -> {
            try {
                listenForSensorData((Integer) configMap.get("sensors.humidity.udp_port"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        tempThread.start();
        humidityThread.start();
    }

    public static void main(String[] args) throws JMSException {
        WarehouseService service = new WarehouseService(args[0], "Warehouse One");
        try {
            service.startService();
        } catch (Throwable e) {
            System.err.println("System Error. Closing Warehouse Service");
            service.stopService();
        }
    }


}
