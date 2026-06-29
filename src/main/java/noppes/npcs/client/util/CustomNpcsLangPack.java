package noppes.npcs.client.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLPaths;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.Util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class CustomNpcsLangPack {

    private static String currentLanguage = "en_us";
    private static final Map<String, String> enProperties = Maps.newHashMap();
    private static final Map<String, String> properties = Maps.newHashMap();
    private static boolean init = false;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void load() {
        if (!init) { registerReloadListener(); }
        try { reload(); } catch (Exception e) { LogWriter.error(e); }
    }

    @SuppressWarnings("ConstantConditions")
    private static void registerReloadListener() {
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager())
                .registerReloadListener((stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor) -> stage.wait(null)
                                .thenRunAsync(() -> {
                    try {
                        reload();
                        check();
                    } catch (Exception e) { LogWriter.error(e); }
                }, gameExecutor));
        init = true;
    }

    @SuppressWarnings("ConstantConditions")
    private static void reload() throws IOException {
        clear();
        Path options = FMLPaths.GAMEDIR.get().resolve("options.txt");
        if (Files.exists(options)) {
            try (BufferedReader reader = Files.newBufferedReader(options, StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("lang:")) {
                        currentLanguage = line.substring("lang:".length());
                        break;
                    }
                }
            } catch (Exception ignored) {}
        }
        Path langDir = FMLPaths.GAMEDIR.get()
                .resolve("assets/" + CustomNpcs.MODID + "/lang");
        if (Files.exists(langDir) || Files.createDirectories(langDir) != null) {
            Path enLang = langDir.resolve("en_us.json");
            if (Files.exists(enLang)) {
                loadLocaleData(true, Files.newInputStream(enLang));
            }
            Path curLang = langDir.resolve(currentLanguage + ".json");
            if (Files.exists(curLang)) {
                loadLocaleData(false, Files.newInputStream(curLang));
            }
        }
        save();
    }

    public static void save() {
        Path langDir = FMLPaths.GAMEDIR.get()
                .resolve("assets/" + CustomNpcs.MODID + "/lang");
        try {
            if (Files.exists(langDir) || Files.createDirectories(langDir) != null) {
                saveLocaleData(true, langDir.resolve("en_us.json"));
                if (!currentLanguage.equals("en_us")) {
                    saveLocaleData(false, langDir.resolve(currentLanguage + ".json"));
                }
            }
        } catch (IOException e) {
            LogWriter.error(e);
        }
    }

    private static void loadLocaleData(boolean isEn, InputStream inputStream) throws IOException {
        if (inputStream == null) return;

        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            JsonObject json = GSON.fromJson(reader, JsonObject.class);
            if (json == null) return;

            for (Map.Entry<String, com.google.gson.JsonElement> entry : json.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue().getAsString();

                value = value.replaceAll("%(\\d+\\$)?s", "%$1s");

                if (isEn) {
                    enProperties.put(key, value);
                } else {
                    properties.put(key, value);
                }
            }
        }
    }

    private static void saveLocaleData(boolean isEn, Path langPath) {
        JsonObject json = new JsonObject();
        Map<String, String> targetMap = isEn ? enProperties : properties;

        targetMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> json.addProperty(entry.getKey(), entry.getValue()));

        try (BufferedWriter writer = Files.newBufferedWriter(langPath, StandardCharsets.UTF_8)) {
            GSON.toJson(json, writer);
        } catch (Exception e) {
            LogWriter.error(e);
        }
    }

    private static void clear() {
        enProperties.clear();
        properties.clear();
    }

    /**
     * Add custom localization
     * @param key - localization key
     * @param value - value for the key, in "en_us" language
     */
    public static void added(String key, String value) {
        if (key == null || key.isEmpty() || value == null) { return; }
        String translateValue = value;
        if (!currentLanguage.equals("en_us")) {
            String language = currentLanguage;
            if (currentLanguage.contains("_")) {
                if (currentLanguage.equals("zh_cn")) { language = "zh_CN"; }
                else if (currentLanguage.equals("zh_tw")) { language = "zh_TW"; }
                else { language = currentLanguage.substring(0, currentLanguage.indexOf("_")); }
            }
            String temp = Util.instance.translateGoogle("en", language, value);
            if (!temp.equals(value)) { translateValue = temp; }
        }

        boolean needSave = !enProperties.containsKey(key) || !properties.containsKey(key);
        if (!needSave && (!enProperties.get(key).equals(value) || !properties.get(key).equals(translateValue))) { needSave = true; }

        enProperties.put(key, value);
        properties.put(key, translateValue);

        if (needSave) { save(); }
    }

    public static void check() {
        if (!properties.isEmpty()) { clear(); }
    }

}