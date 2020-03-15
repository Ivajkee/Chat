package chat.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class Logger {
    public static void saveMessage(String message) {
        try (FileWriter fileWriter = new FileWriter(LocalDate.now() + ".txt", StandardCharsets.UTF_8, true)) {
            fileWriter.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}