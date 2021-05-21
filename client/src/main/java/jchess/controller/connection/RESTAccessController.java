package jchess.controller.connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jchess.model.Player;
import jchess.utils.SystemUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RESTAccessController {
    public static int login(String username, String password, boolean isLogin) throws IOException {
        Player player = new Player(username, password);
        String urlEnd = isLogin ? "login" : "register";
        String urlString = SystemUtils.getInstance().appProps.getProperty("rest_url") + urlEnd;
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-type", "application/json");
        connection.setDoOutput(true);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        String jsonString = gson.toJson(player);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonString.getBytes("utf-8");
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
                SystemUtils.getInstance().setUser(gson.fromJson(response.toString(), Player.class));
            }
        }
        return statusCode;
    }

    public static void logout() throws IOException {
        if (SystemUtils.getInstance().getUser() == null) {
            return;
        }

        String urlString = SystemUtils.getInstance().appProps.getProperty("rest_url") + "logout?id=" + SystemUtils.getInstance().getUser().getId();
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-type", "application/json");
        connection.setDoOutput(true);

        int statusCode = connection.getResponseCode();
        if (statusCode == 200) {
            System.out.println("Successful logout!");
        } else if (statusCode == 202) {
            System.out.println("Successful logout and disconnect from lobby!");
        } else {
            System.out.println("Something went wrong!");
        }
    }
}
