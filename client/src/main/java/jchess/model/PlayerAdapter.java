package jchess.model;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class PlayerAdapter extends TypeAdapter<Player> {
    @Override
    public void write(JsonWriter jsonWriter, Player player) throws IOException {
        jsonWriter.beginObject();

        jsonWriter.name("id").value(player.getId() + "");
        jsonWriter.name("username").value(player.getUsername());
        jsonWriter.name("password").value(player.getPassword());

        jsonWriter.endObject();
    }

    @Override
    public Player read(JsonReader jsonReader) throws IOException {
        Player player = new Player();
        jsonReader.beginObject();
        String fieldName = null;

        while (jsonReader.hasNext()) {
            JsonToken token = jsonReader.peek();

            if (token.equals(JsonToken.NAME)) {
                fieldName = jsonReader.nextName();
            }

            if ("id".equals(fieldName)) {
                token = jsonReader.peek();
                player.setId(jsonReader.nextInt());
            }

            if ("username".equals(fieldName)) {
                token = jsonReader.peek();
                player.setUsername(jsonReader.nextString());
            }

            if ("password".equals(fieldName)) {
                token = jsonReader.peek();
                player.setPassword(jsonReader.nextString());
            }
        }
        jsonReader.endObject();
        return player;
    }
}
