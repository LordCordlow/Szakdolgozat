package jchess.controller.connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jchess.model.Player;
import jchess.utils.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RESTPlayersController {
    public static void getOnlinePlayers() throws IOException {
        String urlString = SystemUtils.getInstance().appProps.getProperty("rest_url") + "players";
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
                System.out.println(response);
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();

                Type playersType = new TypeToken<ArrayList<Player>>(){}.getType();
                ArrayList<Player> players = gson.fromJson(response.toString(), playersType);
                SystemUtils.getInstance().setOnlineUsers(players);
            }
        }
    }
}
