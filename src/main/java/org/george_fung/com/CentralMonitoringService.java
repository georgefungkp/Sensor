package org.george_fung.com;

import org.george_fung.com.util.MessageHandler;
import org.george_fung.com.util.Misc;
import org.george_fung.com.util.message_broker_service.BrokerMessageService;

import javax.jms.JMSException;
import java.util.Map;

public class CentralMonitoringService implements Service, MessageHandler  {
    private final Map<String, Object> configMap;
    private BrokerMessageService service;
    private final int temperatureThreshold;
    private final int humidityThreshold;

    public CentralMonitoringService(String env) throws JMSException {
        this.configMap = Misc.getSettings(env);
        this.temperatureThreshold = (Integer) configMap.get("sensors.temperature.threshold");
        this.humidityThreshold = (Integer) configMap.get("sensors.humidity.threshold");
        this.service = BrokerMessageService.getService(env);
    }

    public static void main(String[] args) throws JMSException {
        CentralMonitoringService service = new CentralMonitoringService(args[0]);
        try {
            service.startService();
        } catch (Throwable e) {
            e.printStackTrace();
            System.err.println(e.fillInStackTrace().toString());
            System.err.println("System Error. Closing Central Monitoring Service");
            service.stopService();
        }
    }


    /**
     * Process message from message broker/event listener
     * @param message message content
     */
    @Override
    public void processMessage(String message) {
        this.processSensorInfo(message);
    }

    /**
     * @param message statistical information
     */
    private void processSensorInfo(String message) {
        message = message.replaceAll("[\\n\\r\\t]", "");
        String[] parts = message.split("; ");
        String sensorName = parts[0].split("=")[1];
        SensorType sensorType;
        int threshold;
        try {
            sensorType = SensorType.getSensorTypeByShortName(sensorName.substring(0, 1));
        } catch (SensorType.SensorTypeNotFoundException ex) {
            System.err.println(ex.getMessage());
            ex.getMessage();
            return;
        }
        if (sensorType == SensorType.TEMPERATURE) {
            threshold = this.temperatureThreshold;
        } else {
            threshold = this.humidityThreshold;
        }


        int value;
        String valuePart = parts[1].split("=")[1];
        try {
            value = Integer.parseInt(valuePart);
        } catch (NumberFormatException nfe) {
            System.err.printf("The format of reading %s is not correct%n", valuePart);
            return;
        }

        if (value > threshold) {
            String errMsg = String.format("ALARM: %s Sensor - %s threshold exceeded! Value: %d", sensorType, sensorName, value);
            System.err.println(errMsg);
        }
    }

    @Override
    public void startService() throws Exception {
        System.out.printf("Central Monitoring Services is running and checking message from message broker... %n");

        // Synchronized call
//        while (true) {
//            String message = service.get();
//            if ( message != null && !message.isEmpty()) {
//                this.processMessage(message);
//            }
//            Thread.sleep(1000);
//        }

        // Async call
        service.getAsync(this);
    }

    /**
     * For action of stoppage of service before program ends
     */
    @Override
    public void stopService() {
        System.out.println("stopping...");
        // For action of stoppage of service before program ends
    }
}
