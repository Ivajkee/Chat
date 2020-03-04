package chat.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Logger {
    private Map<String, List<String>> logs;

    public Logger() {
        logs = new HashMap<>();
    }

    protected void saveMessage(String nickName, String message) {
        logs.get(nickName).add(message);
    }
}
