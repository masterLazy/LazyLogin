package masterlazy.lazylogin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import net.minecraft.text.LiteralText;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LangManager {
    private static Map<String, String> lang = new HashMap<>();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final ClassLoader classLoader = LangManager.class.getClassLoader();

    public static void loadLang() {
        try (InputStream inputStream = classLoader.getResourceAsStream("assets/lazylogin/lang.json")) {
            if (inputStream == null) {
                LoginMod.LOGGER.error("(lazylogin) Failed to load lang.json");
                return;
            }
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            Type typeOfMap = new TypeToken<Map<String, String>>() {
            }.getType();
            lang = gson.fromJson(reader, typeOfMap);
            LoginMod.LOGGER.info("(lazylogin) Loaded lang.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        if (lang.containsKey(key)) {
            return lang.get(key);
        } else {
            LoginMod.LOGGER.error("(lazylogin) Failed to load text " + key);
            return "Error";
        }
    }

    public static LiteralText getText(String key) {
        return new LiteralText(get(key));
    }
}

