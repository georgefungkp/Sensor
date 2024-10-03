package org.george_fung.com;

import org.george_fung.com.util.Misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Map;

/**
 * @Todo 1. add comment of each class
 * @Todo 2. log4j
 * Client side should use the following tip to communicate with Ware house service
 * <a href="https://help.ubidots.com/en/articles/937233-sending-tcp-udp-packets-using-netcat#test-your-netcat-understanding-as-a-client-server">...</a>
 */
public class WarehouseService {
    private final Map<String, Object> configMap;
    String name;
    Socket socket;
    BufferedReader in;
    PrintWriter out;


    public WarehouseService(String env, String name) throws IOException {
        this.configMap = Misc.getSettings(env);
        this.name = name;
        int portNo = (Integer) configMap.get("central_service.tcp_port");
        try {
            this.socket = new Socket("localhost", portNo);
        }catch (ConnectException _){
            System.err.println("Central Monitoring Service is down");
            return;
        }
        // Setup output stream to send data to the server
        this.out = new PrintWriter(socket.getOutputStream(), true);
        // Setup input stream to receive data from the server
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        System.out.println("Warehouse Service starts");
    }

    /**
     * @param port
     * @param threshold
     * @param sensorType
     * @throws SocketException
     */
    private void listenForSensorData(int port, int threshold, SensorType sensorType) throws SocketException {
        DatagramSocket socket = new DatagramSocket(port);

        try (socket) {
            byte[] buffer = new byte[256];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received: " + received);
                this.sendToCentralMonitoringService(received);

            }
        } catch (Throwable e) {
            System.err.println(e.fillInStackTrace().toString());
        } // Auto close socket
    }

    /**
     *
     * @param message
     * @throws IOException
     */
    private void sendToCentralMonitoringService(String message) throws IOException {
        // Send message to the server
        out.println(message);

        // Receive response from the server
        String response = in.readLine();
        System.out.println("Server says: " + response);
    }

    public static void main(String[] args) throws IOException {
        WarehouseService service = new WarehouseService(args[0], "Warehouse One");
        try {
            service.start();
        } catch (Exception ex) {
            service.stop();
        }
    }


    /**
     * Close the socket, reader and writer
     *
     * @throws IOException
     */
    public void stop() throws IOException {
        this.out.close();
        this.in.close();
        this.socket.close();
        System.out.println("Warehouse Service is closed");
    }


    public void start() {
        try {
            Thread tempThread = new Thread(() -> {
                try {
                    listenForSensorData((Integer) configMap.get("sensors.temperature.udp_port"),
                            (Integer) configMap.get("sensors.temperature.threshold"), SensorType.TEMPERATURE);
                } catch (SocketException e) {
                    throw new RuntimeException(e);
                }
            });
            Thread humidityThread = new Thread(() -> {
                try {
                    listenForSensorData((Integer) configMap.get("sensors.humidity.udp_port"),
                            (Integer) configMap.get("sensors.humidity.threshold"), SensorType.HUMIDITY);
                } catch (SocketException e) {
                    throw new RuntimeException(e);
                }
            });

            tempThread.start();
            humidityThread.start();
        } catch (RuntimeException ex) {
            System.err.println("System error:" + ex);
        }
    }

//        try {
//            DatagramSocket socket = new DatagramSocket();
//            String[] sensors = {
//                "sensor_id=t1; value=30",
//                "sensor_id=h1; value=40"
//            };
//
//            for (String sensorData : sensors) {
//                byte[] buffer = sensorData.getBytes();
//                InetAddress address = InetAddress.getByName("localhost");
//                int port = sensorData.contains("t1") ? 3344 : 3355;
//                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
//                socket.send(packet);
//                System.out.println("Sent: " + sensorData);
//            }
//            socket.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
}
