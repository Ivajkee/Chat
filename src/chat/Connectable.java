package chat;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public interface Connectable {
    void readMessage(Connect connect, String message);

    void sendMessage(String message);

    void connect();

    void connectReady();

    void disconnect();

    void deleteConnect(Connect connect);

    void systemOutMessage(String message);

    default String getTime() {
        LocalTime ldt = LocalTime.now();
        return "[" + ldt.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "]";
    }
}