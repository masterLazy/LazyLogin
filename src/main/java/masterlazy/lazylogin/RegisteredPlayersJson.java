package masterlazy.lazylogin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
        return playerObject != null && playerObject.get("pwd_hash").getAsString().equalsIgnoreCase(sha256Hex(password));
    }

    private static JsonObject findPlayerObject(String username) {
        JsonObject playerObject = null;
        if (jsonArray.isEmpty()) {
            return null;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject playerObjectIndex = jsonArray.get(i).getAsJsonObject();
            if (playerObjectIndex.get("name").getAsString().equalsIgnoreCase(username)) {
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
                if (playerObjectIndex.get("name").getAsString().equalsIgnoreCase(username)) {
                    playerObject = new JsonObject();
                    playerObject.addProperty("name", username);
                    playerObject.addProperty("pwd_hash", sha256Hex(password));
                    jsonArray.set(i, playerObject);
                    break;
                }
            }
        } else {
            playerObject = new JsonObject();
            playerObject.addProperty("name", username);
            playerObject.addProperty("pwd_hash", sha256Hex(password));
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
            LazyLogin.LOGGER.info("[LazyLogin] registered-players.json not found, creating a new one.");
            return;
        }
        try {
            BufferedReader bufferedReader = Files.newReader(REGISTERED_PLAYERS, StandardCharsets.UTF_8);
            jsonArray = gson.fromJson(bufferedReader, JsonArray.class);
            LazyLogin.LOGGER.info("[LazyLogin] Loaded(reloaded) registered-players.json");
        } catch (Exception e) {
            LazyLogin.LOGGER.error("[LazyLogin] Exception occurred when loading registered-players.json. Did you modified it wrongly?");
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

    private static String sha256Hex(String str) {
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to get sha-256 instance");
        }
        byte[] digest = messageDigest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            String temp = Integer.toHexString(b & 0xFF);
            if (temp.length() == 1) {
                sb.append("0");
            }
            sb.append(temp);
        }
        return sb.toString();
    }
}

