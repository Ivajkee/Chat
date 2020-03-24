package chat;

import chat.utils.User;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Connect extends Thread {
    private final Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private final Connectable connect;
    private final User user;
    private boolean isConnect;

    public Connect(Socket socket, Connectable connect) {
        this.socket = socket;
        this.connect = connect;
        user = new User();
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            disconnect();
            connect.systemOutMessage("Ошибка создания потоков записи/чтения");
            return;
        }
        isConnect = true;
        connect.connectReady();
        readMessage();
    }

    private void readMessage() {
        String msg;
        try {
            while ((msg = in.readLine()) != null) {
                connect.readMessage(this, msg);
            }
        } catch (Exception e) {
            System.out.println("Ошибка чтения сообщения");
        } finally {
            disconnect();
        }
    }

    public void sendMessage(String message) {
        if (isConnect) {
            try {
                out.write(message + "\n");
                out.flush();
            } catch (Exception e) {
                connect.systemOutMessage("Ошибка отправки сообщения");
                disconnect();
            }
        }
    }

    public void disconnect() {
        if (isConnect) {
            isConnect = false;
            try {
                socket.close();
            } catch (Exception e) {
                connect.systemOutMessage("Ошибка закрытия сокета");
            } finally {
                connect.deleteConnect(this);
            }
        }
    }

    public void setNickName(String nickName) {
        user.setNickName(nickName);
    }

    public String getNickName() {
        return user.getNickName();
    }

    public boolean isConnected() {
        return isConnect;
    }
}