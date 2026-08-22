package noppes.npcs.client;

import com.google.common.collect.Lists;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.*;
import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.client.network.INetworkPlayerInfoMixin;
import noppes.npcs.shared.client.util.ResourceDownloader;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.client.util.ImageDownloadAlt;
import noppes.npcs.controllers.PlayerSkinController;
import noppes.npcs.controllers.data.SkinData;
import org.apache.commons.compress.utils.IOUtils;
import org.lwjgl.opengl.GL11;

public class SkinUtil {

    private static final HashSet<ResourceLocation> createdSkins = new HashSet<>();

    public static void resetSkin(UUID uuid) {
        if (uuid == null) { return; }
        LogWriter.debug("Check uuid: " + uuid);
        Map<MinecraftProfileTexture.Type, SkinData> data = PlayerSkinController.getInstance().get(uuid);
        if (data == null) { return; }
        Minecraft minecraft = Minecraft.getMinecraft();
        NetHandlerPlayClient connection = minecraft.getConnection();
        if (connection == null) { return; }
        NetworkPlayerInfo playerInfo = connection.getPlayerInfo(uuid);
        Map<MinecraftProfileTexture.Type, ResourceLocation> map = ((INetworkPlayerInfoMixin) playerInfo).getPlayerTextures();
        map.clear();
        for (SkinData skinData : data.values()) {
            if (!skinData.isValid()) {
                LogWriter.debug("Check not valid skinData: " + skinData);
                continue;
            }
            createPlayerSkin(skinData);
            LogWriter.debug("Set: "+skinData.type()+" = \""+skinData.getLocation()+"\"");
            map.put(skinData.type(), skinData.getLocation());
        }
    }

    public static void createPlayerSkin(SkinData skinData) {
        TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
        IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
        ResourceLocation location = skinData.getLocation();
        if (skinData.isUrl()) {
            if (createdSkins.contains(location)) { return; }
            String locSkin = String.format("assets/%s/%s", location.getResourceDomain(), location.getResourcePath());
            File file = new File(CustomNpcs.Dir, locSkin);
            if (!file.exists()) {
                ResourceDownloader.load(new ImageDownloadAlt(file, skinData.getUrl(), location, DefaultPlayerSkin.getDefaultSkinLegacy(), true, () -> {})); }
            createdSkins.add(location);
            return;
        }
        if (skinData.isLocation()) {
            if (createdSkins.contains(location)) { return; }
            LogWriter.debug("Set location: "+location+"; "+skinData);
            texturemanager.loadTexture(location, new SimpleTexture(location));
            createdSkins.add(location);
            return;
        }
        if (skinData.getDefault() != null && location.equals(skinData.getDefault())) { return; }
        // combine
        skinData.calculateResLoc();
        LogWriter.debug("Create Composite: "+skinData.getLocation()+"; "+skinData);
        if (createdSkins.contains(location)) { return; }
        String locSkin = String.format("assets/%s/%s", skinData.getLocation().getResourceDomain(), skinData.getLocation().getResourcePath());
        File file = new File(CustomNpcs.Dir, locSkin);
        if (!file.getParentFile().exists() && file.getParentFile().mkdirs()) { return; }
        if (file.exists() && file.isFile()) { return; }

        List<BufferedImage> listBuffers = Lists.newArrayList();
        BufferedImage bodyImage = readBufferedImage(resourceManager, skinData.getPartResLocByNumber(resourceManager, "torsos", skinData.getBodyType()));
        bodyImage = colorTexture(bodyImage, new Color(skinData.getBodyColor()));
        BufferedImage hairImage = readBufferedImage(resourceManager, skinData.getPartResLocByNumber(resourceManager, "hairs", skinData.getHairType()));
        hairImage = colorTexture(hairImage, new Color(skinData.getHairColor()));
        BufferedImage faceImage = readBufferedImage(resourceManager, skinData.getPartResLocByNumber(resourceManager, "faces", skinData.getFaceType()));
        faceImage = colorTexture(faceImage, new Color(skinData.getEyesColor()));
        BufferedImage legsImage = readBufferedImage(resourceManager, skinData.getPartResLocByNumber(resourceManager, "legs", skinData.getPantsType()));
        BufferedImage jacketsImage = readBufferedImage(resourceManager, skinData.getPartResLocByNumber(resourceManager, "jackets", skinData.getJacketType()));
        BufferedImage shoesImage = readBufferedImage(resourceManager, skinData.getPartResLocByNumber(resourceManager, "shoes", skinData.getShoesType()));
        for (int pec : skinData.getPeculiarities()) { listBuffers.add(readBufferedImage(resourceManager, skinData.getPartResLocByNumber(resourceManager, "peculiarities", pec))); }
        BufferedImage skinImage = combineTextures(bodyImage, readBufferedImage(resourceManager, skinData.getPartResLocByNumber(resourceManager, "torsos", -1)));
        skinImage = combineTextures(skinImage, faceImage);
        skinImage = combineTextures(skinImage, legsImage);
        skinImage = combineTextures(skinImage, shoesImage);
        skinImage = combineTextures(skinImage, jacketsImage);
        skinImage = combineTextures(skinImage, faceImage);
        skinImage = combineTextures(skinImage, hairImage);
        BufferedImage buffer;
        if (!listBuffers.isEmpty()) {
            for(Iterator<BufferedImage> var17 = listBuffers.iterator(); var17.hasNext(); skinImage = combineTextures(skinImage, buffer)) { buffer = var17.next(); }
        }
        try {
            ImageIO.write(skinImage, "PNG", file);
            LogWriter.debug("Create new skin: " + file.getAbsolutePath());
        }
        catch (Exception ignored) { }
        SimpleTexture texture = new SimpleTexture(location);
        TextureUtil.allocateTexture(texture.getGlTextureId(), skinImage.getWidth(), skinImage.getHeight());
        uploadBufferedImageContents(skinImage, texture.getGlTextureId());
        texturemanager.loadTexture(location, texture);
        createdSkins.add(location);
    }

    private static BufferedImage readBufferedImage(IResourceManager resourceManager, ResourceLocation location) {
        if (resourceManager == null || location == null) { return null; }
        InputStream imageStream = null;
        try {
            imageStream = resourceManager.getResource(location).getInputStream();
            return ImageIO.read(imageStream);
        }
        catch (IOException ignored) { }
        finally {
            if (imageStream != null) { IOUtils.closeQuietly(imageStream); }
        }
        return null;
    }

    private static void uploadBufferedImageContents(BufferedImage bufferedimage, int id) {
        int width = bufferedimage.getWidth();
        int height = bufferedimage.getHeight();
        int[] lvt_8_1_ = new int[width * height];
        bufferedimage.getRGB(0, 0, width, height, lvt_8_1_, 0, width);
        IntBuffer intbuffer = ByteBuffer.allocateDirect(4 * width * height).order(ByteOrder.nativeOrder()).asIntBuffer();
        intbuffer.put(lvt_8_1_);
        intbuffer.flip();
        //RenderSystem.activeTexture(33984);
        GlStateManager.bindTexture(id);
        initTexture(intbuffer, width, height);
    }

    public static void initTexture(IntBuffer intBuffer, int width, int height) {
        GL11.glPixelStorei(GL11.GL_UNPACK_SWAP_BYTES, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_LSB_FIRST, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, 32993, 33639, intBuffer);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
    }

    private static BufferedImage colorTexture(BufferedImage buffer, Color color) {
        if (buffer != null && color != null) {
            for(int v = 0; v < buffer.getHeight(); ++v) {
                for(int u = 0; u < buffer.getWidth(); ++u) {
                    int c = buffer.getRGB(u, v);
                    int al = c >> 24 & 255;
                    if (al != 0) {
                        int r0 = c >> 16 & 255;
                        int g0 = c >> 8 & 255;
                        int b0 = c & 255;
                        String a = Integer.toHexString(Math.min(al + color.getAlpha(), 255));
                        if (a.length() == 1) { a = "0" + a; }
                        String r = Integer.toHexString((r0 + color.getRed()) / 2);
                        if (r.length() == 1) { r = "0" + r; }
                        String g = Integer.toHexString((g0 + color.getGreen()) / 2);
                        if (g.length() == 1) { g = "0" + g; }
                        String b = Integer.toHexString((b0 + color.getBlue()) / 2);
                        if (b.length() == 1) { b = "0" + b; }
                        buffer.setRGB(u, v, (int)Long.parseLong(a + r + g + b, 16));
                    }
                }
            }
        }
        return buffer;
    }

    private static BufferedImage combineTextures(BufferedImage buffer_0, BufferedImage buffer_1) {
        if (buffer_0 == null) { return buffer_1; }
        if (buffer_1 == null) { return buffer_0; }
        int w0 = buffer_0.getWidth();
        int w1 = buffer_1.getWidth();
        int h0 = buffer_0.getHeight();
        int h1 = buffer_1.getHeight();
        int w = Math.max(w0, w1);
        int h = Math.max(h0, h1);
        float sw0 = (float)w0 / (float)w;
        float sh0 = (float)h0 / (float)h;
        float sw1 = (float)w1 / (float)w;
        float sh1 = (float)h1 / (float)h;
        BufferedImage total = new BufferedImage(w, h, 6);
        for(int v = 0; v < h; ++v) {
            for(int u = 0; u < w; ++u) {
                int c0 = buffer_0.getRGB((int)((float)u * sw0), (int)((float)v * sh0));
                int a0 = c0 >> 24 & 255;
                if (a0 != 0) { total.setRGB(u, v, c0); }
                int c1 = buffer_1.getRGB((int)((float)u * sw1), (int)((float)v * sh1));
                int a1 = c1 >> 24 & 255;
                if (a1 != 0) {
                    if (a1 == 255) { total.setRGB(u, v, c1); }
                    else {
                        int r0 = c0 >> 16 & 255;
                        int g0 = c0 >> 8 & 255;
                        int b0 = c0 & 255;
                        int r1 = c1 >> 16 & 255;
                        int g1 = c1 >> 8 & 255;
                        int b1 = c1 & 255;
                        String a = Integer.toHexString(Math.min(a0 + a1, 255));
                        if (a.length() == 1) { a = "0" + a; }
                        String r = Integer.toHexString((r0 + r1) / 2);
                        if (r.length() == 1) {r = "0" + r; }
                        String g = Integer.toHexString((g0 + g1) / 2);
                        if (g.length() == 1) { g = "0" + g; }
                        String b = Integer.toHexString((b0 + b1) / 2);
                        if (b.length() == 1) { b = "0" + b; }
                        total.setRGB(u, v, (int)Long.parseLong(a + r + g + b, 16));
                    }
                }
            }
        }
        return total;
    }

    // New from Unofficial (BetaZavr)
    public static void checkTexture(@Nonnull EntityNPCInterface npc) {
        if (npc.display.skinType == 0) {
            ResourceLocation skin = new ResourceLocation(npc.display.getSkinTexture());
            if (skin.getResourceDomain().equals(CustomNpcs.MODID) &&
                    (skin.getResourcePath().toLowerCase().contains("textures/entity/custom/female_") ||
                            skin.getResourcePath().toLowerCase().contains("textures/entity/custom/male_"))) {
                createPlayerSkin(SkinData.create(MinecraftProfileTexture.Type.SKIN, skin));
            }
        }
    }

}
