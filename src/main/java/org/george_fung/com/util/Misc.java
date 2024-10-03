package org.george_fung.com.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Misc {
    public static Map<String, Object> getSettings(String env) {
        Map<String, Object> settings = new HashMap<>();
        try {
            URL url = LoadYamlIntoMap.class.getClassLoader().getResource(env + "_config.yaml");
            assert url != null;
            File file = new File(url.toURI());
            settings = LoadYamlIntoMap.getMap(file);
        }catch (URISyntaxException | IOException ex){
            System.err.printf("The configuration file %s has syntax error. Please check.%n", env + "_confilg.yaml");
        }
        return settings;
    }
}
