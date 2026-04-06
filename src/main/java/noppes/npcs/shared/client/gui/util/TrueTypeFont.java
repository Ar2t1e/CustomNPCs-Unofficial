package noppes.npcs.shared.client.gui.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.RandomSource;
import noppes.npcs.shared.common.util.LRUHashMap;
import noppes.npcs.shared.common.util.LogWriter;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class TrueTypeFont {

    private static final int TEXTURE_SIZE = 512;
    private static final List<Font> allFonts = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts());
    private static final RandomSource random = RandomSource.create();

    private final List<Font> usedFonts = new ArrayList<>();
    private final LinkedHashMap<String, TrueTypeFont.GlyphCache> textCache = new LRUHashMap<>(100);
    private final Map<Character, TrueTypeFont.Glyph> glyphCache = new HashMap<>();
    private final List<TrueTypeFont.TextureCache> textures = new ArrayList<>();
    private final Graphics2D globalG = (Graphics2D) (new BufferedImage(1, 1, 2)).getGraphics();
    public float scale = 1.0F;
    private char specialChar = (char) 167;

    private Font font;
    private int lineHeight = 1;

    public TrueTypeFont(Font fontIn, float scaleIn) {
        font = fontIn;
        scale = scaleIn;
        globalG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        lineHeight = globalG.getFontMetrics(font).getHeight();
    }

    public TrueTypeFont(ResourceLocation resource, int fontSize, float scaleIn) {
        Minecraft minecraft = Minecraft.getInstance();
        Resource resourceTTF = minecraft.getResourceManager().getResource(resource).orElse(null);
        if (resourceTTF != null) {
            try {
                InputStream stream = resourceTTF.open();
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                Font fontIn = Font.createFont(0, stream);
                ge.registerFont(fontIn);
                font = fontIn.deriveFont(Font.PLAIN, (float) fontSize);
                scale = scaleIn;
                globalG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                lineHeight = globalG.getFontMetrics(font).getHeight();
                LogWriter.info("Loaded font \""+font.getFontName()+"\"");
            }
            catch (Exception e) { LogWriter.error("Error load font \"" + resource + "\"", e); }
        }
    }

    public void setSpecial(char c) { specialChar = c; }

    public void draw(PoseStack posestack, String text, float x, float y, int color) {
        TrueTypeFont.GlyphCache cache = getOrCreateCache(text);
        int a = color >> 24 & 255;
        int r = color >> 16 & 255;
        int g = color >> 8 & 255;
        int b = color & 255;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);

        posestack.pushPose();
        posestack.translate(x, y, 0.0F);
        posestack.scale(scale, scale, scale);

        int rr = r;
        int gg = g;
        int  bb = b;
        boolean bold = false;
        boolean italic = false;
        boolean underline = false;
        boolean strikethrough = false;
        boolean obfuscated = false;

        float currentX = 0.0F;
        float maxLineHeight = 0.0F;

        // Для obfuscated - запоминаем оригинальные глифы и их позиции
        List<Glyph> glyphs = cache.glyphs;
        for (Glyph gl : glyphs) {
            switch (gl.type) {
                case RESET -> {
                    rr = r;
                    gg = g;
                    bb = b;
                    bold = italic = underline = strikethrough = obfuscated = false;
                }
                case COLOR -> {
                    rr = gl.color >> 16 & 255;
                    gg = gl.color >> 8 & 255;
                    bb = gl.color & 255;
                }
                case BOLD -> bold = true;
                case ITALIC -> italic = true;
                case UNDERLINE -> underline = true;
                case STRIKETHROUGH -> strikethrough = true;
                case RANDOM -> obfuscated = true;
                case NORMAL -> {
                    float glWidth = (float) gl.width * textureScale();
                    float glHeight = (float) gl.height * textureScale();
                    maxLineHeight = Math.max(maxLineHeight, glHeight);
                    // Obfuscated: Replace with a random character of the same width
                    Glyph renderGlyph = obfuscated ? getObfuscatedGlyph(gl.originalChar, gl) : gl;
                    // ITALIC: Move the top of the character to the right
                    float italicOffset = italic ? glHeight * -0.3f : 0f;
                    posestack.pushPose();
                    // Apply italic transformation to ONE character
                    if (italic) {
                        Matrix4f matrix = posestack.last().pose();
                        matrix.m10(matrix.m11() * -0.3f);
                    }
                    RenderSystem.setShaderTexture(0, renderGlyph.texture);

                    fillGradient(posestack.last().pose(),
                            currentX + (italic ? -italicOffset : 0), 0.0F,
                            (float) renderGlyph.x * textureScale(),
                            (float) renderGlyph.y * textureScale(),
                            glWidth, glHeight, rr, gg, bb);

                    posestack.popPose();
                    // BOLD: draw a second time with an offset of 0.5pxl
                    if (bold) {
                        posestack.pushPose();
                        if (italic) {
                            Matrix4f matrix = posestack.last().pose();
                            matrix.m01(matrix.m00() * 0.3f);
                        }
                        fillGradient(posestack.last().pose(),
                                currentX + 0.5F + (italic ? -italicOffset : 0), 0.0F,
                                (float) renderGlyph.x * textureScale(),
                                (float) renderGlyph.y * textureScale(),
                                glWidth, glHeight, rr, gg, bb);
                        posestack.popPose();
                        currentX += 0.5F;
                    }
                    if (strikethrough) {
                        float lineY = glHeight * 0.5f;
                        fillColor(posestack, currentX, lineY, glWidth, 0.5F, rr, gg, bb);
                    }
                    if (underline) {
                        float lineY = glHeight - 1.0f;
                        fillColor(posestack, currentX, lineY, glWidth, 0.5F, rr, gg, bb);
                    }
                    currentX += glWidth;
                }
            }
        }
        posestack.popPose();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void fillGradient(Matrix4f m, float x, float y, float textureX, float textureY, float width, float height, int r, int g, int b) {
        float f = 0.00390625F;
        float zLevel = 0.0f;
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        BufferBuilder tessellator = Tesselator.getInstance().getBuilder();
        tessellator.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        tessellator.vertex(m, x, y + height, zLevel)
                .uv(textureX * f, (textureY + height) * f)
                .color(r, g, b, 255).endVertex();
        tessellator.vertex(m, x + width, y + height, zLevel)
                .uv((textureX + width) * f, (textureY + height) * f)
                .color(r, g, b, 255).endVertex();
        tessellator.vertex(m, x + width, y, zLevel)
                .uv((textureX + width) * f, textureY * f)
                .color(r, g, b, 255).endVertex();
        tessellator.vertex(m, x, y, zLevel)
                .uv(textureX * f, textureY * f)
                .color(r, g, b, 255).endVertex();
        BufferUploader.drawWithShader(tessellator.end());
    }

    private void fillColor(PoseStack posestack, float x, float y, float w, float h, int r, int g, int b) {
        Matrix4f m = posestack.last().pose();
        float zLevel = 0.0f;

        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        BufferBuilder builder = Tesselator.getInstance().getBuilder();
        builder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        builder.vertex(m, x, y + h, zLevel).color(r, g, b, 255).endVertex();
        builder.vertex(m, x + w, y + h, zLevel).color(r, g, b, 255).endVertex();
        builder.vertex(m, x + w, y, zLevel).color(r, g, b, 255).endVertex();
        builder.vertex(m, x, y, zLevel).color(r, g, b, 255).endVertex();

        BufferUploader.drawWithShader(builder.end());
    }

    private Glyph getObfuscatedGlyph(char originalChar, Glyph original) {
        char[] randomChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
        char randomChar = randomChars[random.nextInt(randomChars.length)];
        while (randomChar == originalChar) { randomChar = randomChars[random.nextInt(randomChars.length)]; }
        GlyphCache cache = getOrCreateCache(String.valueOf(randomChar));
        if (!cache.glyphs.isEmpty()) { return cache.glyphs.get(0); }
        return original;
    }

    private TrueTypeFont.GlyphCache getOrCreateCache(String text) {
        TrueTypeFont.GlyphCache cache = textCache.get(text);
        if (cache == null) {
            cache = new GlyphCache();
            for (int i = 0; i < text.length(); ++i) {
                char c = text.charAt(i);
                if (c == specialChar && i + 1 < text.length()) {
                    char next = text.toLowerCase(Locale.ENGLISH).charAt(i + 1);
                    int index = "0123456789abcdefklmnor".indexOf(next);
                    if (index >= 0) {
                        Glyph g = new Glyph(specialChar);
                        if (index < 16) {
                            g.type = GlyphType.COLOR;
                            ChatFormatting cf = ChatFormatting.getByCode(next);
                            if (cf != null && cf.getColor() != null) { g.color = cf.getColor(); }
                        }
                        else if (index == 16) {
                            g.type = GlyphType.RANDOM;
                        } else if (index == 17) {
                            g.type = GlyphType.BOLD;
                        } else if (index == 18) {
                            g.type = GlyphType.STRIKETHROUGH;
                        } else if (index == 19) {
                            g.type = GlyphType.UNDERLINE;
                        } else if (index == 20) {
                            g.type = GlyphType.ITALIC;
                        } else {
                            g.type = GlyphType.RESET;
                        }
                        cache.glyphs.add(g);
                        ++i;
                        continue;
                    } // has color code
                }
                Glyph g = getOrCreateGlyph(c);
                cache.glyphs.add(g);
                cache.width += g.width;
                cache.height = Math.max(cache.height, g.height);
            }
            textCache.put(text, cache);
        }
        return cache;
    }

    private TrueTypeFont.Glyph getOrCreateGlyph(char c) {
        TrueTypeFont.Glyph g = glyphCache.get(c);
        if (g == null) {
            TextureCache cache = getCurrentTexture();
            Font font = getFontForChar(c);
            FontMetrics metrics = globalG.getFontMetrics(font);
            g = new Glyph(c);
            g.width = Math.max(metrics.charWidth(c), 1);
            g.height = Math.max(metrics.getHeight(), 1);
            if (cache.x + g.width >= TEXTURE_SIZE) {
                cache.x = 0;
                cache.y += lineHeight + 1;
                if (cache.y >= TEXTURE_SIZE) {
                    cache.full = true;
                    cache = getCurrentTexture();
                }
            }
            g.x = cache.x;
            g.y = cache.y;
            cache.x += g.width + 3;
            lineHeight = Math.max(lineHeight, g.height);
            cache.g.setFont(font);
            cache.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            cache.g.drawString("" + c, g.x, g.y + metrics.getAscent());
            g.texture = cache.textureId;
            /**/
            // load texture
            int[] pixels = new int[TEXTURE_SIZE * TEXTURE_SIZE];
            cache.bufferedImage.getRGB(0, 0, TEXTURE_SIZE, TEXTURE_SIZE, pixels, 0, TEXTURE_SIZE);
            IntBuffer intbuffer = BufferUtils.createIntBuffer(pixels.length).put(pixels).flip();
            RenderSystem.bindTextureForSetup(cache.textureId);
            GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_ROWS, 0);
            GL11.glPixelStorei(GL11.GL_UNPACK_SKIP_PIXELS, 0);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);
            GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, TEXTURE_SIZE, TEXTURE_SIZE, 0, 32993, 33639, intbuffer);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);  // LINEAR — better anti-aliasing
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);  // and for minification also LINEAR
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 33071);  // GL_CLAMP_TO_EDGE
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 33071);  // GL_CLAMP_TO_EDGE
            /**/
            glyphCache.put(c, g);
        }
        return g;
    }

    private TrueTypeFont.TextureCache getCurrentTexture() {
        TrueTypeFont.TextureCache cache = null;
        for (TextureCache t : textures) {
            if (!t.full) {
                cache = t;
                break;
            }
        }
        if (cache == null) {
            textures.add(cache = new TextureCache());
        }
        return cache;
    }

    private Font getFontForChar(char c) {
        if (font.canDisplay(c)) { return font; }
        Iterator<Font> var2 = usedFonts.iterator();
        Font f;
        do {
            if (!var2.hasNext()) {
                Font fa = new Font("Arial Unicode MS", Font.PLAIN, font.getSize());
                if (fa.canDisplay(c)) { return fa; }
                Iterator<Font> var6 = allFonts.iterator();
                do {
                    if (!var6.hasNext()) {
                        return font;
                    }
                    f = var6.next();
                } while(!f.canDisplay(c));
                usedFonts.add(f = f.deriveFont(Font.PLAIN, (float) font.getSize()));
                return f;
            }
            f = var2.next();
        } while(!f.canDisplay(c));
        return f;
    }

    public int width(String text) {
        TrueTypeFont.GlyphCache cache = getOrCreateCache(text);
        return (int)((float) cache.width * scale * textureScale());
    }

    public int height(String text) {
        if (text != null && !text.trim().isEmpty()) {
            TrueTypeFont.GlyphCache cache = getOrCreateCache(text);
            return Math.max(1, (int)((float) cache.height * scale * textureScale()));
        } else {
            return (int)((float) lineHeight * scale * textureScale());
        }
    }

    private float textureScale() { return 0.5F; }

    public void dispose() {
        for (TextureCache cache : textures) { RenderSystem.deleteTexture(cache.textureId); }
        textCache.clear();
    }

    public String getFontName() { return font.getFontName(); }

    public boolean hasFont() { return font != null; }

    static class GlyphCache {
        public int width;
        public int height;
        List<TrueTypeFont.Glyph> glyphs = new ArrayList<>();
    }

    static class Glyph {
        TrueTypeFont.GlyphType type;
        int color;
        int x;
        int y;
        int height;
        int width;
        int texture;
        char originalChar;

        Glyph(char originalCharIn) {
            type = TrueTypeFont.GlyphType.NORMAL;
            originalChar = originalCharIn;
            color = -1;
        }

    }

    enum GlyphType {
        NORMAL,
        COLOR,
        RANDOM,
        BOLD,
        STRIKETHROUGH,
        UNDERLINE,
        ITALIC,
        RESET,
        OTHER
    }

    static class TextureCache {
        int x;
        int y;
        int textureId = GL11.glGenTextures();
        BufferedImage bufferedImage = new BufferedImage(TEXTURE_SIZE, TEXTURE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g;
        boolean full;

        TextureCache() {
            g = (Graphics2D) bufferedImage.getGraphics();
            // Clearing the entire texture to transparent black
            g.setComposite(AlphaComposite.Clear);
            g.fillRect(0, 0, TEXTURE_SIZE, TEXTURE_SIZE);
            g.setComposite(AlphaComposite.SrcOver);
        }

    }

}
