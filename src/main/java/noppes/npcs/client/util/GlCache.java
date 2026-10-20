package noppes.npcs.client.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.renderer.GlStateManager;
import noppes.npcs.CustomNpcs;
import org.lwjgl.opengl.GL11;

public class GlCache {

    private static final Int2ObjectOpenHashMap<int[]> SIZES = new Int2ObjectOpenHashMap<>();

    public static int boundTexture() {
        if (!CustomNpcs.FastNpcRender) { return GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D); }
        return GlStateManager.textureState[GlStateManager.activeTextureUnit].textureName;
    }

    public static void bindTexture(int id) {
        if (CustomNpcs.FastNpcRender && CustomNpcs.FastNpcFrame && GlStateManager.textureState[GlStateManager.activeTextureUnit].textureName == id) { return; }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        if (CustomNpcs.FastNpcRender) { GlStateManager.textureState[GlStateManager.activeTextureUnit].textureName = id; }
    }

    public static int[] boundTextureSize() {
        if (!CustomNpcs.FastNpcRender) { return querySize(); }
        int id = boundTexture();
        int[] size = SIZES.get(id);
        if (size == null) {
            size = querySize();
            SIZES.put(id, size);
        }
        return size;
    }

    public static void forget(int id) { SIZES.remove(id); }

    private static int[] querySize() {
        return new int[] { GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH),
                GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT) };
    }

}
