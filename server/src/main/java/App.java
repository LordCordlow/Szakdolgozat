import controller.crud.PlayerControllerImpl;
import controller.rest.RestServer;
import controller.websocket.WebSocketServer;
import model.bean.Player;

public class App {
    public static void main(String[] args) {
        WebSocketServer webSocketServer = new WebSocketServer("0.0.0.0", 8025, "/websockets");
        RestServer restServer = new RestServer("0.0.0.0", 8031);

        restServer.runServer();
        webSocketServer.runServer();
    }
}
