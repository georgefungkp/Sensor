package org.george_fung.com.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadYamlIntoMap {

    static ObjectMapper mapper = new ObjectMapper(new YAMLFactory());


    static void flattenedYaml(Map<String, Object> inputMap, Map<String, Object> resultMap, String parentKey){
        //recursion exit conditon
        if (inputMap == null || inputMap.isEmpty()){
           return;
        }

        inputMap.keySet().forEach( x -> {
            String keyBeingProcessed;

            if(parentKey !=null && resultMap.containsKey(parentKey)){
                String currentKey = parentKey + "." + x;
                resultMap.put(currentKey, null);
                keyBeingProcessed = currentKey;
            }
            else {
                resultMap.put(x, null);
                keyBeingProcessed = x;
            }

            Object o = inputMap.get(x);
            resultMap.put(keyBeingProcessed,o);

            if(o instanceof Map) {
                flattenedYaml((Map)inputMap.get(x), resultMap, keyBeingProcessed);
            } else if (o instanceof List list)  {
                int counter = 0;
                for (Object obj: list){
                    if (obj instanceof  Map) {
                        String currentKey = keyBeingProcessed + ".[" + counter + "]";
                        resultMap.put(currentKey, null);
                        flattenedYaml((Map) obj, resultMap, currentKey);
                    }
                }
            }
        });
    }

    public static Map<String, Object> getMap(File file) throws IOException {
        Map<String, Object> mapResult = new HashMap<>(20);
        flattenedYaml(mapper.readValue(file, Map.class), mapResult, null);
        return mapResult;

    }

    public static void main(String[] args) throws IOException, URISyntaxException {
       // URL resource = new LoadYamlIntoMap().getClass().getResource("myapp.yaml");
//        File file= new File("C:\\Users\\George\\IdeaProjects\\WareHouse\\src\\main\\resources\\dev_config.yaml");
        URL url  = LoadYamlIntoMap.class.getClassLoader().getResource("dev_config.yaml");
        assert url != null;
        File file = new File(url.toURI());
        Map<String, Object> mapResult = LoadYamlIntoMap.getMap(file);
        mapResult.forEach((k, v) -> System.out.println(k + ":" + v));
    }

}

