package chat.client;

import chat.utils.Msg;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientConnect extends Thread {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private Client client;
    private String nickName;
    private String ip;
    private int port;
    private boolean isConnect;

    public ClientConnect(Client client, String nickName, String ip, int port) {
        this.client = client;
        this.nickName = nickName;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
            isConnect = true;
            sendMessage(Msg.ADD_ONE + nickName);
        } catch (IOException e) {
            client.disconnect();
            client.systemOutMessage("Время ожидания подключения вышло");
        }

        while (isConnect) {
            try {
                client.readMessage(in.readLine());
            } catch (Exception e) {
                client.systemOutMessage("Соединение прервано");
                if (!socket.isClosed()) {
                    disconnect();
                    //client.disconnect();
                }
            }
        }
    }

    protected void sendMessage(String message) {
        if (isConnect) {
            try {
                out.write(message + "\n");
                out.flush();
            } catch (IOException e) {
                disconnect();
                //client.disconnect();
                client.systemOutMessage("Ошибка отправки сообщения");
            }
        }
    }

    protected void disconnect() {
        if (isConnect) {
            isConnect = false;
            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                client.systemOutMessage("Ошибка закрытия сокета");
            }
        }
    }

//    protected void setNickName(String nickName) {
//        this.nickName = nickName;
//    }
//
//    protected String getNickName() {
//        return nickName;
//    }

    protected boolean isConnected() {
        return isConnect;
    }
}