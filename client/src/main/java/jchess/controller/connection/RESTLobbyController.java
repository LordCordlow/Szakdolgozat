package jchess.controller.connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jchess.model.Lobby;
import jchess.utils.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RESTLobbyController {
    private static final GsonBuilder gsonBuilder = new GsonBuilder();
    private static final Gson gson = gsonBuilder.create();

    public static int createLobby() throws IOException {
        String urlString = SystemUtils.getInstance().appProps.getProperty("rest_url") + "lobby";
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-type", "application/json");
        connection.setDoOutput(true);

        String message = gson.toJson(SystemUtils.getInstance().getUser());
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = message.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int statusCode = connection.getResponseCode();

        if (statusCode == 200) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8")
            )) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                System.out.println("lobbyId: " + response.toString());
                SystemUtils.getInstance().setCurrentLobby(response.toString());
                SystemUtils.getInstance().setWhite(true);
            }
        }
        return statusCode;
    }

    public static int joinLobby(Lobby lobby) throws IOException {
        String urlString = SystemUtils.getInstance().appProps.getProperty("rest_url") + "lobby?id=" + lobby.getId();
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-type", "application/json");
        connection.setDoOutput(true);

        String message = gson.toJson(SystemUtils.getInstance().getUser());
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = message.getBytes("utf-8");
            os.write(input, 0, input.length);
        }
        int statusCode = connection.getResponseCode();
        if (statusCode == 200) {
            SystemUtils.getInstance().setCurrentLobby(lobby.getId());
            SystemUtils.getInstance().setWhite(false);
        }
        return connection.getResponseCode();
    }

    public static int getLobbies() throws IOException {
        String urlString = SystemUtils.getInstance().appProps.getProperty("rest_url") + "lobby";
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);

        int statusCode = connection.getResponseCode();

        if (statusCode == 200) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8")
            )) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Type lobbiesType = new TypeToken<ArrayList<Lobby>>(){}.getType();
                ArrayList<Lobby> lobbies = gson.fromJson(response.toString(), lobbiesType);
                SystemUtils.getInstance().setLobbies(lobbies);
            }
        }
        return statusCode;
    }

    public static int refreshLobby(String lobbyId) throws IOException {
        String urlString = SystemUtils.getInstance().appProps.getProperty("rest_url") + "lobby?id=" + lobbyId;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-type", "application/json");
        connection.setDoOutput(true);

        int statusCode = connection.getResponseCode();

        if (statusCode == 200) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8")
            )) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Lobby updatedLobby = gson.fromJson(response.toString(), Lobby.class);
                if (updatedLobby != null) {
                    if (SystemUtils.getInstance().getUser().getId() == updatedLobby.getHost().getId()) {
                        SystemUtils.getInstance().setOpponent(updatedLobby.getGuest());
                    } else {
                        SystemUtils.getInstance().setOpponent(updatedLobby.getHost());
                    }
                }
            }
        }
        return statusCode;
    }

    public static int leaveLobby(String lobbyId) throws IOException {
        String urlString = SystemUtils.getInstance().appProps.getProperty("rest_url")+ "lobby?id=" + lobbyId;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-type", "application/json");
        connection.setDoOutput(true);

        String message = gson.toJson(SystemUtils.getInstance().getUser());
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = message.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int statusCode = connection.getResponseCode();

        return statusCode;
    }
}
