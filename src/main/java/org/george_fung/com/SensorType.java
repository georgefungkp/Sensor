package org.george_fung.com;

public enum SensorType{
    TEMPERATURE("t"),
    HUMIDITY("h");

    private final String shortName;

    SensorType(String s) {
        this.shortName = s;
    }


    public static SensorType getSensorTypeByShortName(String shortName) throws SensorTypeNotFoundException {
        if (TEMPERATURE.shortName.equalsIgnoreCase(shortName)){
            return SensorType.TEMPERATURE;
        }else if (HUMIDITY.shortName.equalsIgnoreCase(shortName)) {
            return SensorType.HUMIDITY;
        }
        throw new SensorTypeNotFoundException(String.format("The sensor type is wrong. value:%s", shortName));
    }

    public static class SensorTypeNotFoundException extends Exception{
        public SensorTypeNotFoundException(String messsage){
            super(messsage);
        }
    }
}
