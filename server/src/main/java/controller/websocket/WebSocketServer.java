package controller.websocket;

import org.glassfish.tyrus.server.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WebSocketServer {
    private String hostname;
    private int port;
    private String rootPath;

    public WebSocketServer(String hostname, int port, String rootPath) {
        this.hostname = hostname;
        this.port = port;
        this.rootPath = rootPath;
    }

    public void runServer() {
        Server server = new Server(this.hostname, this.port, this.rootPath, ServerEndpoint.class);

        try {
            server.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Press any key to stop the server");
            reader.readLine();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            server.stop();
        }
    }
}
