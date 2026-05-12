package noppes.npcs.client.gui.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import noppes.npcs.shared.client.util.GuiNpcPngAnimation;
import noppes.npcs.shared.common.util.LogWriter;
import org.lwjgl.opengl.GL11;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class GuiNpcUtil {

    private static final Map<ResourceLocation, GuiNpcPngAnimation> itemsMap = new HashMap<>(); // Items or Blocks [texture, settings]
    private static final Map<ResourceLocation, GuiNpcPngAnimation> entitysMap = new HashMap<>(); // [texture, [frame ID, settings]]
    private static final List<ResourceLocation> notAnimated = new ArrayList<>();

    public static void drawTexturedModalRect(GuiGraphics graphics, ResourceLocation textureLocation, int textureU, int textureV, int textureWidth, int textureHeight, float scaleSize) {
        Minecraft.getInstance().getTextureManager().getTexture(textureLocation);
        int addV = 0;
        int drawHeight = textureHeight;
        if (!notAnimated.contains(textureLocation)) {
            if (!itemsMap.containsKey(textureLocation)) {
                load(textureLocation, true);
            }
            if (itemsMap.containsKey(textureLocation)) {
                GuiNpcPngAnimation pngAnimation = itemsMap.get(textureLocation);
                int frame = pngAnimation.getFrameId();
                float scale = (float) pngAnimation.height / (float) pngAnimation.width;
                drawHeight = (int) (scaleSize / scale);
                addV = (int) (frame * (float) drawHeight);
                graphics.pose().scale(1.0f, scale, 1.0f);
            }
        }
        graphics.blit(textureLocation, 0, 0, textureU, textureV + addV, textureWidth, drawHeight);
    }

    public static void load(ResourceLocation textureLocation, boolean isItem) {
        Minecraft mc = Minecraft.getInstance();
        try {
            Optional<Resource> res = mc.getResourceManager().getResource(new ResourceLocation(textureLocation.getNamespace(), textureLocation.getPath() + ".mcmeta"));
            if (res.isEmpty()) { return; }
            try (InputStreamReader reader = new InputStreamReader(res.get().open(), StandardCharsets.UTF_8)) {
                JsonElement json = JsonParser.parseReader(reader);
                if (json != null && json.getAsJsonObject().getAsJsonObject("animation") != null) {
                    JsonObject animation = json.getAsJsonObject().getAsJsonObject("animation");
                    mc.getTextureManager().getTexture(textureLocation);
                    Resource resource = mc.getResourceManager().getResourceOrThrow(textureLocation);
                    NativeImage nativeimage;
                    try (InputStream inputstream = resource.open()) { nativeimage = NativeImage.read(inputstream); }
                    GuiNpcPngAnimation pngAnimation = new GuiNpcPngAnimation(nativeimage.getWidth(), nativeimage.getHeight(),
                            GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D),
                            animation);
                    if (isItem) { itemsMap.put(textureLocation, pngAnimation); }
                    else {
                        pngAnimation.createEntityIDs();
                        entitysMap.put(textureLocation, pngAnimation);
                    }
                    return;
                }
            }
        }
        catch (Exception e) { LogWriter.error(e); }
        if (!notAnimated.contains(textureLocation)) { notAnimated.add(textureLocation); }
    }

}
