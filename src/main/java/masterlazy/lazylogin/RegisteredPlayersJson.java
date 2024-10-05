package masterlazy.lazylogin;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class RegisteredPlayersJson {
    private static final File REGISTERED_PLAYERS = new File("registered-players.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static JsonArray jsonArray = new JsonArray();

    public static boolean isPlayerRegistered(String username) {
        return findPlayerObject(username) != null;
    }

    public static boolean isCorrectPassword(String username, String password) {
        JsonObject playerObject = findPlayerObject(username);
        return playerObject != null && playerObject.get("pwd_hash").getAsString().equals(DigestUtils.sha256Hex(password));
    }

    private static JsonObject findPlayerObject(String username) {
        JsonObject playerObject = null;
        if (jsonArray.size() == 0) {
            return null;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject playerObjectIndex = jsonArray.get(i).getAsJsonObject();
            if (playerObjectIndex.get("name").getAsString().equals(username)) {
                playerObject = playerObjectIndex;
                break;
            }
        }
        return playerObject;
    }

    public static void save(String username, String password) {
        JsonObject playerObject = findPlayerObject(username);
        if (playerObject != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject playerObjectIndex = jsonArray.get(i).getAsJsonObject();
                if (playerObjectIndex.get("name").getAsString().equals(username)) {
                    playerObject = new JsonObject();
                    playerObject.addProperty("name", username);
                    playerObject.addProperty("pwd_hash", DigestUtils.sha256Hex(password));
                    jsonArray.set(i, playerObject);
                    break;
                }
            }
        } else {
            playerObject = new JsonObject();
            playerObject.addProperty("name", username);
            playerObject.addProperty("pwd_hash", DigestUtils.sha256Hex(password));
            jsonArray.add(playerObject);
        }
        try {
            BufferedWriter bufferedWriter = Files.newWriter(REGISTERED_PLAYERS, StandardCharsets.UTF_8);
            bufferedWriter.write(gson.toJson(jsonArray));
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void read() {
        if (! REGISTERED_PLAYERS.exists()) {
            return;
        }
        try {
            BufferedReader bufferedReader = Files.newReader(REGISTERED_PLAYERS, StandardCharsets.UTF_8);
            jsonArray = gson.fromJson(bufferedReader, JsonArray.class);
            LazyLogin.LOGGER.info("(lazylogin) Loaded registered-players.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getPlayers() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            list.add(jsonArray.get(i).getAsJsonObject().get("name").getAsString());
        }
        return list;
    }
}

