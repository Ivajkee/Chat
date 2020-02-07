package chat.server;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerConnect extends Thread {
    private Server server;
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String nickName;
    private boolean isConnect;

    public ServerConnect(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), StandardCharsets.UTF_8));
        out = new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8));
        isConnect = true;
    }

    @Override
    public void run() {
        while (isConnect) {
            try {
                server.readMessage(this, in.readLine());
            } catch (Exception e) {
                if (!socket.isClosed()) {
                    disconnect();
                    //server.disconnect();
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
                server.disconnect();
                server.systemOutMessage("Ошибка отправки сообщения");
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
                server.systemOutMessage("Ошибка закрытия сокета");
            }
            server.deleteClient(this);
        }
    }

    protected void setNickName(String nickName) {
        this.nickName = nickName;
    }

    protected String getNickName() {
        return nickName;
    }

//    protected boolean isConnected() {
//        return isConnect;
//    }
}