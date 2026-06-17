package noppes.npcs.client.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class ShaderData {

    public final ResourceLocation id;
    public final VertexFormat format;
    public ShaderInstance shader;

    private ShaderData(ResourceLocation idIn, VertexFormat formatIn) {
        id = idIn;
        format = formatIn;
    }

    public static ShaderData of(File file) {
        return new ShaderData(new ResourceLocation(CustomNpcs.MODID,
                NoppesUtilServer.validPath(file.getName().toLowerCase().replace(".json", ""))),
                parseVertexFormat(file));
    }

    private static VertexFormat parseVertexFormat(File jsonFile) {
        String formatName = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new java.io.FileInputStream(jsonFile)))) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            if (json.has("vertexFormat")) { formatName = json.get("vertexFormat").getAsString(); }
        }
        catch (Exception ignored) {}
        return getFormatByName(formatName);
    }

    private static VertexFormat getFormatByName(String name) {
        return switch (name) {
            case "POSITION_COLOR" -> DefaultVertexFormat.POSITION_COLOR;
            case "POSITION_COLOR_LIGHTMAP" -> DefaultVertexFormat.POSITION_COLOR_LIGHTMAP;
            case "POSITION_COLOR_NORMAL" -> DefaultVertexFormat.POSITION_COLOR_NORMAL;
            case "POSITION_COLOR_TEX" -> DefaultVertexFormat.POSITION_COLOR_TEX;
            case "POSITION_COLOR_TEX_LIGHTMAP" -> DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP;
            case "POSITION_TEX" -> DefaultVertexFormat.POSITION_TEX;
            case "POSITION_TEX_COLOR" -> DefaultVertexFormat.POSITION_TEX_COLOR;
            case "POSITION_TEX_COLOR_NORMAL" -> DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL;
            case "POSITION_TEX_LIGHTMAP_COLOR" -> DefaultVertexFormat.POSITION_TEX_LIGHTMAP_COLOR;
            case "BLOCK" -> DefaultVertexFormat.BLOCK;
            case "NEW_ENTITY" -> DefaultVertexFormat.NEW_ENTITY;
            case "PARTICLE" -> DefaultVertexFormat.PARTICLE;
            case "BLIT_SCREEN" -> DefaultVertexFormat.BLIT_SCREEN;
            default -> DefaultVertexFormat.POSITION;
        };
    }

}
