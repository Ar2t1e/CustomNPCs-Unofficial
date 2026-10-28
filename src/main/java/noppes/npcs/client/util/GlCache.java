package noppes.npcs.client.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;

public class GlCache {

    private static final Int2ObjectOpenHashMap<int[]> CASH_TEX_SIZES = new Int2ObjectOpenHashMap<>();
    // No mixins, only reflection!
    protected static Field textureState;
    protected static Field textureName;
    protected static Field activeTextureUnit;

    public static int boundTexture() {
        checkTextureName();
        checkTextureState();
        checkActiveTextureUnit();
        try {
            textureState.setAccessible(true);
            activeTextureUnit.setAccessible(true);
            int id = (int) activeTextureUnit.get(null);
            // GlStateManager.textureState[GlStateManager.activeTextureUnit].textureName
            return (int) textureName.get(((Object[]) textureState.get(null))[id]);
        }
        catch (Exception ignored1) { }
        return -1;
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    private static void checkTextureName() {
        if (textureName == null) {
            try {
                for (Class<?> subClass : GlStateManager.class.getDeclaredClasses()) {
                    if (subClass.getSimpleName().equals("TextureState")) {
                        try { textureName = subClass.getDeclaredField("field_179059_b"); }
                        catch (Exception ignored1) {
                            try { textureName = GlStateManager.class.getDeclaredField("textureName"); }
                            catch (Exception ignored2) { }
                        }
                        break;
                    }
                }
            }
            catch (Exception ignored) {}
        }
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    private static void checkActiveTextureUnit() {
        if (activeTextureUnit == null) {
            try { activeTextureUnit = GlStateManager.class.getDeclaredField("field_179162_o"); }
            catch (Exception ignored) {
                try { activeTextureUnit = GlStateManager.class.getDeclaredField("activeTextureUnit"); }
                catch (Exception ignored1) { }
            }
        }
    }

    @SuppressWarnings("JavaReflectionMemberAccess")
    private static void checkTextureState() {
        if (textureState == null) {
            try { textureState = GlStateManager.class.getDeclaredField("field_179174_p"); }
            catch (Exception ignored) {
                try { textureState = GlStateManager.class.getDeclaredField("textureState"); }
                catch (Exception ignored1) { }
            }
        }
    }

    public static void bindTexture(int id) {
        int parentId = boundTexture();
        if (parentId == id || parentId == -1) { return; }
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        try {
            textureState.setAccessible(true);
            activeTextureUnit.setAccessible(true);
            int idIn = (int) activeTextureUnit.get(null);
            // GlStateManager.textureState[GlStateManager.activeTextureUnit].textureName = id
            textureName.set(((Object[]) textureState.get(null))[idIn], id);
        }
        catch (Exception ignored1) { }
    }

    public static int[] boundTextureSize() {
        int id = boundTexture();
        int[] size = CASH_TEX_SIZES.get(id);
        if (size == null) { CASH_TEX_SIZES.put(id, size = querySize()); }
        return size;
    }

    public static void forget(int id) { CASH_TEX_SIZES.remove(id); }

    private static int[] querySize() {
        return new int[] { GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH),
                GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT) };
    }

}
