package org.george_fung.com;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SensorTypeTest {

    @Test
    public void testGetSensorTypeByShortName_Temperature() throws SensorType.SensorTypeNotFoundException {
        SensorType type = SensorType.getSensorTypeByShortName("t");
        assertEquals(SensorType.TEMPERATURE, type);
    }

    @Test
    public void testGetSensorTypeByShortName_Humidity() throws SensorType.SensorTypeNotFoundException {
        SensorType type = SensorType.getSensorTypeByShortName("h");
        assertEquals(SensorType.HUMIDITY, type);
    }

    @Test
    public void testGetSensorTypeByShortName_Invalid() {
        assertThrows(SensorType.SensorTypeNotFoundException.class, () -> {
            SensorType.getSensorTypeByShortName("invalid");
        });
    }
}
