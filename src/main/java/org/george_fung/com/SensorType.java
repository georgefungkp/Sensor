package org.george_fung.com;

import java.util.FormatFlagsConversionMismatchException;
import java.util.Optional;

public enum SensorType{
    TEMPERATURE("t"),
    HUMIDITY("h");

    private final String shortName;

    SensorType(String s) {
        this.shortName = s;
    }

    public String getShortName(){
        return this.shortName;
    }

    public static SensorType getSensorTypeByShortName(String shortName) throws SensorTypeNotFoundException {
        if (TEMPERATURE.shortName.equalsIgnoreCase(shortName)){
            return SensorType.TEMPERATURE;
        }else if (HUMIDITY.shortName.equalsIgnoreCase(shortName)) {
            return SensorType.HUMIDITY;
        }
        throw new SensorTypeNotFoundException(String.format("Short Name %s is wrong", shortName));
    }

    public static class SensorTypeNotFoundException extends Exception{
        public SensorTypeNotFoundException(String messsage){
            super(messsage);
        }
    }
}
