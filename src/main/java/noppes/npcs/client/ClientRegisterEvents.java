package noppes.npcs.client;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomParticleTypes;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.client.particles.CustomParticle;
import noppes.npcs.client.particles.CustomParticleType;
import noppes.npcs.client.util.ClientRecipeRegister;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = CustomNpcs.MODID, value = Dist.CLIENT)
public class ClientRegisterEvents {

    private static final Map<ResourceLocation, ShaderInstance> SHADERS = new HashMap<>();

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {
        File dir = new File(CustomNpcs.Dir, "assets/customnpcs/shaders/core");
        for (File file : Util.instance.getFiles(dir, ".json")) {
            String name = NoppesUtilServer.validPath(file.getName().toLowerCase().replace(".json", ""));
            String parent = file.getParentFile().getName().toLowerCase();
            VertexFormat format = switch (parent) {
                case "position_tex" -> DefaultVertexFormat.POSITION_TEX;
                case "position_color" -> DefaultVertexFormat.POSITION_COLOR;
                case "position_color_lightmap" -> DefaultVertexFormat.POSITION_COLOR_LIGHTMAP;
                case "position_color_normal" -> DefaultVertexFormat.POSITION_COLOR_NORMAL;
                case "position_color_tex" -> DefaultVertexFormat.POSITION_COLOR_TEX;
                case "position_color_tex_lightmap" -> DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP;
                case "position_tex_color_normal" -> DefaultVertexFormat.POSITION_TEX_COLOR_NORMAL;
                default -> DefaultVertexFormat.POSITION;
            };
            if (format == DefaultVertexFormat.POSITION) { parent = ""; } else { parent += "/"; }
            ResourceLocation id = new ResourceLocation(CustomNpcs.MODID, parent + name);
            try {
                event.registerShader(
                        new ShaderInstance(event.getResourceProvider(), id, format),
                        shader -> SHADERS.put(id, shader)
                );
                LogWriter.debug("Load shader \"" + id + "\"; format: "+format);
            }
            catch (Exception e) { LogWriter.error("Error load shader", e); }
        }

    }

    @SubscribeEvent
    public static void cnpcsRegisterParticle(RegisterParticleProvidersEvent event) {
        MutableSpriteSet spriteSet = new MutableSpriteSet();
        ParticleEngine.SpriteParticleRegistration<CustomParticleType> provider = CustomParticle.Provider::new;
        for (ParticleType<CustomParticleType> particleType: CustomParticleTypes.CUSTOMS.values()) {
            event.registerSpecial(particleType, provider.create(spriteSet));
        }
    }

    @SubscribeEvent
    public static void cnpcsRegisterRecipe(RegisterRecipeBookCategoriesEvent event) {
        event.registerBookCategories(RecipeController.CRAFTING_CUSTOM_GLOBAL, RecipeBookCategories.CRAFTING_CATEGORIES);
        event.registerBookCategories(RecipeController.CRAFTING_CUSTOM_ANVIL, ClientRecipeRegister.CRAFTING_CUSTOM_ANVIL_CATEGORIES);
    }

    @OnlyIn(Dist.CLIENT)
    static class MutableSpriteSet implements SpriteSet {
        private List<TextureAtlasSprite> sprites;

        @Override
        public @Nonnull TextureAtlasSprite get(int u, int v) {
            return sprites.get(u * (sprites.size() - 1) / v);
        }

        @Override
        public @Nonnull TextureAtlasSprite get(RandomSource rnd) {
            return sprites.get(rnd.nextInt(sprites.size()));
        }

        public void rebind(List<TextureAtlasSprite> atlas) { sprites = ImmutableList.copyOf(atlas); }

    }

    public static ShaderInstance getShader(ResourceLocation id) { return SHADERS.get(id); }

    public static boolean hasShader(ResourceLocation id) { return SHADERS.containsKey(id); }

}
