package utils;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ScenarioContext {

    private Map<String, Object> data;

    public ScenarioContext() {
        data = new HashMap<>();
    }

    public void saveData(String key, Object object) {
        data.put(key, object);
    }

    public Object getData(String key) {
        return data.get(key);
    }

    public void resetData() {
        data.clear();
    }
}
