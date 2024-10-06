package org.george_fung.com;

import org.george_fung.com.util.MessageHandler;
import org.george_fung.com.util.Misc;
import org.george_fung.com.util.message_broker_service.BrokerMessageService;

import javax.jms.JMSException;
import java.util.Map;
import java.util.Optional;

public class CentralMonitoringService implements Service, MessageHandler  {
    private final Map<String, Object> configMap;
    private final BrokerMessageService service;
    private final int temperatureThreshold;
    private final int humidityThreshold;

    public CentralMonitoringService(String env) throws JMSException {
        this.configMap = Misc.getSettings(env);
        this.temperatureThreshold = retrieveThreshold("sensors.temperature.threshold");
        this.humidityThreshold = retrieveThreshold("sensors.humidity.threshold");
        this.service = BrokerMessageService.getService(env);
    }

    private int retrieveThreshold(String thresholdName) {
        return (Integer) Optional
                .ofNullable(configMap.get(thresholdName))
                .orElseThrow(() ->
                        new IllegalArgumentException("Threshold \"" + thresholdName + "\" not configured."));
    }
    public static void main(String[] args) throws JMSException {
        CentralMonitoringService service = new CentralMonitoringService(args[0]);
        try {
            service.startService();
        } catch (Throwable e) {
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
            return;
        }
        if (sensorType == SensorType.TEMPERATURE) {
            threshold = this.temperatureThreshold;
        } else {
            threshold = this.humidityThreshold;
        }

        int value = parseInt(parts[1]);
        if (value > threshold) {
            String errMsg = String.format("ALARM: %s Sensor - %s threshold exceeded! Value: %d", sensorType, sensorName, value);
            System.err.println(errMsg);
        }
    }

    private int parseInt(String part) {
        try {
            return Integer.parseInt(part.split("=")[1]);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("The format of reading is not correct", nfe);
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
