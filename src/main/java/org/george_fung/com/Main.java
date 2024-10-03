package org.george_fung.com;

import org.george_fung.com.util.LoadYamlIntoMap;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;


public class Main {

    public static void main(String[] args) throws URISyntaxException, IOException {
        URL url  = LoadYamlIntoMap.class.getClassLoader().getResource("dev_config.yaml");
        assert url != null;
        File file = new File(url.toURI());
        Map<String, Object> mapResult = LoadYamlIntoMap.getMap(file);

    }
}