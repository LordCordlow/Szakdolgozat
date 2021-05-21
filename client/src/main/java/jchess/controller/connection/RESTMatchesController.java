package jchess.controller.connection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jchess.model.Match;
import jchess.model.VMatch;
import jchess.utils.SystemUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class RESTMatchesController {
    private static final GsonBuilder gsonBuilder = new GsonBuilder();
    private static final Gson gson = gsonBuilder.create();

    public static int getMatchesByPlayerId() throws IOException {
        String urlString = SystemUtils.getInstance().appProps.getProperty("rest_url")
                + "matches?id="
                + SystemUtils.getInstance().getUser().getId();
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
                Type matchesType = new TypeToken<ArrayList<VMatch>>(){}.getType();
                ArrayList<VMatch> VMatches = gson.fromJson(response.toString(), matchesType);
                SystemUtils.getInstance().setMatches(VMatches);
            }
        }

        return statusCode;
    }

    public static void addMatch(int winner) throws IOException {
        String urlString = SystemUtils.getInstance().appProps.getProperty("rest_url") + "matches";
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-type", "application/json");
        connection.setDoOutput(true);

        int whitePlayerId = SystemUtils.getInstance().isWhite() ?
                SystemUtils.getInstance().getUser().getId() :
                SystemUtils.getInstance().getOpponent().getId();
        int blackPlayerId = SystemUtils.getInstance().isWhite() ?
                SystemUtils.getInstance().getOpponent().getId() :
                SystemUtils.getInstance().getUser().getId();
        Match match = new Match(
                whitePlayerId,
                blackPlayerId,
                winner,
                SystemUtils.getInstance().getMatchStartTime(),
                SystemUtils.getInstance().getMatchEndTime()
        );

        String message = gson.toJson(match);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = message.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int statusCode = connection.getResponseCode();
        System.out.println(statusCode);
    }
}
