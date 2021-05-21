package jchess.controller.connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jchess.model.Lobby;
import jchess.model.TLobby;
import jchess.utils.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RESTTandemController {
    private static final GsonBuilder gsonBuilder = new GsonBuilder();
    private static final Gson gson = gsonBuilder.create();

    public static int createTLobby() throws IOException {
        String urlString = SystemUtils.getInstance().appProps.getProperty("rest_url") + "tandem_lobby";
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Content-type", "application/json");
        connection.setRequestMethod("POST");
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
                TLobby tandem = new TLobby(response.toString(), SystemUtils.getInstance().getUser(), SystemUtils.getInstance().getSocketId());
                SystemUtils.getInstance().setCurrentTandem(tandem);
                SystemUtils.getInstance().setWhite(true);
            }
        }
        return statusCode;
    }

    public static int joinTandem(TLobby tandem) throws IOException {
        String urlString = SystemUtils.getInstance().appProps.getProperty("rest_url") + "tandem_lobby?id=" + tandem.getId();
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
                TLobby tandemLobby = gson.fromJson(response.toString(), TLobby.class);
                SystemUtils.getInstance().setCurrentTandem(tandemLobby);
                if (tandemLobby.getHostTeam().size() > 1 &&
                        tandemLobby.getHostTeam().get(1).getId() == SystemUtils.getInstance().getUser().getId()
                ) {
                    SystemUtils.getInstance().setWhite(false);
                }

                if (tandemLobby.getOppTeam().size() > 0 &&
                        tandemLobby.getOppTeam().get(0).getId() == SystemUtils.getInstance().getUser().getId()
                ) {
                    SystemUtils.getInstance().setWhite(false);
                }

                if (tandemLobby.getOppTeam().size() > 1 &&
                        tandemLobby.getOppTeam().get(1).getId() == SystemUtils.getInstance().getUser().getId()
                ) {
                    SystemUtils.getInstance().setWhite(true);
                }
            }
        }
        return statusCode;
    }

    public static int getTandems() throws IOException {
        String urlString = SystemUtils.getInstance().appProps.getProperty("rest_url") + "tandem_lobby";
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
                Type tandemsType = new TypeToken<ArrayList<TLobby>>(){}.getType();
                ArrayList<TLobby> tandems = gson.fromJson(response.toString(), tandemsType);
                SystemUtils.getInstance().setTandems(tandems);
            }
        }
        return statusCode;
    }

    public static int refreshTandem(String id) throws IOException {
        String urlString = SystemUtils.getInstance().appProps.getProperty("rest_url") + "tandem_lobby?id=" + id;
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
                TLobby updatedLobby = gson.fromJson(response.toString(), TLobby.class);
                if (updatedLobby != null) {
                    SystemUtils.getInstance().setCurrentTandem(updatedLobby);
                }
            }
        }
        return statusCode;
    }

    public static int leaveTandem(String id) throws IOException {
        String urlString = SystemUtils.getInstance().appProps.getProperty("rest_url") + "tandem_lobby?id=" + id;
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
