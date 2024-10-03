package org.george_fung.com;

import org.george_fung.com.util.Misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class CentralMonitoringService {
    private final Map<String, Object> configMap;
    private int temperatureThreshold = Integer.MAX_VALUE;
    private int humidityThreshold = Integer.MAX_VALUE;

    public CentralMonitoringService(String env) {
        this.configMap = Misc.getSettings(env);
        this.temperatureThreshold = (Integer) configMap.get("sensors.temperature.threshold");
        this.humidityThreshold = (Integer) configMap.get("sensors.humidity.threshold");
    }

    public static void main(String[] args) {
        try {
            CentralMonitoringService service = new CentralMonitoringService(args[0]);
            service.start();
        } catch (Throwable e) {
            System.err.println(e.fillInStackTrace().toString());
        }
    }

    /**
     *
     * @param message statistical information
     * @return return message to sensor
     */
    private String processSensorInfo(String message) {
        String[] parts = message.split("; ");
        String sensorName = parts[0].split("=")[1];
        SensorType sensorType;
        int threshold;
        try {
            sensorType = SensorType.getSensorTypeByShortName(sensorName.substring(0, 1));
        }catch (SensorType.SensorTypeNotFoundException ex){
            System.err.println(ex);
            return ex.getMessage();
        }
        if (sensorType == SensorType.TEMPERATURE) {
            threshold = this.temperatureThreshold;
        }else{
            threshold = this.humidityThreshold;
        }


        int value = 0;
        String valuePart = parts[1].split("=")[1];
        try {
            value = Integer.parseInt(valuePart.replaceAll("[^A-Za-z0-9]", ""));
        } catch (NumberFormatException nfe) {
            System.err.printf("The format of value %s if not correct%n", valuePart);
            return "Message Format Error";
        }

        if (value > threshold) {
            String errMsg = String.format("ALARM: %s %s threshold exceeded! Value: %d", sensorType, sensorName, value);
            System.err.println(errMsg);
            return errMsg;
        }else{
            return String.format("Monitoring statistics of %s is well-received", sensorName);
        }
    }

    public void start() throws IOException {
        int portNo = (Integer) configMap.get("central_service.tcp_port");
        ServerSocket serverSocket = new ServerSocket(portNo);
        System.out.printf("Central Monitoring Services is running on port %d and waiting for client connection... %n", portNo);
        // Accept incoming client connection
        Socket clientSocket = serverSocket.accept();
        System.out.println("Client connected!");

        // Setup input and output streams for communication with the client
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        // Read message from client
        String message;
        while ((message = in.readLine()) != null) {
            System.out.println(message);
            // Send response to the Warehouse
            out.println(this.processSensorInfo(message));
        }

        // Close the client socket
        clientSocket.close();
    }
}
