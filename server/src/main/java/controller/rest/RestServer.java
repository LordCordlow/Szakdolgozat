package controller.rest;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class RestServer {
    private HttpServer httpServer;
    private String hostname;
    private int port;

    public RestServer(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.createServer();
    }

    public void createServer() {
        try {
            this.httpServer = HttpServer.create(
                    new InetSocketAddress(this.hostname, this.port),
                    0
            );
            System.out.println("HTTP server successfully created");

            httpServer.createContext("/login", new LoginHandler());
            httpServer.createContext("/register", new RegisterHandler());
            httpServer.createContext("/logout", new LogoutHandler());
            httpServer.createContext("/players", new PlayersHandler());
            httpServer.createContext("/matches", new MatchesHandler());
            httpServer.createContext("/lobby", new LobbyHandler());
            httpServer.createContext("/tandem_lobby", new TLobbyHandler());
            httpServer.createContext("/tandem_matches", new TMatchesHandler());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runServer() {
        try {
            this.httpServer.start();
            System.out.println("HTTP started at: " + this.hostname + ":" + this.port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getIdFromParams(String query) {
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                if (pair[0].equals("id")) {
                    return pair[1];
                }
            }
        }
        return null;
    }

    /*
    * curl -i -X POST -H 'Content-Type: application/json' -d '{"name": "New item", "year": "2009"}' http://rest-api.io/items
    * curl -i -X PUT -H 'Content-Type: application/json' -d '{"name": "Updated item", "year": "2010"}' http://rest-api.io/items/5069b47aa892630aae059584
    * curl -i -X DELETE http://rest-api.io/items/5069b47aa892630aae059584
    * */
}
