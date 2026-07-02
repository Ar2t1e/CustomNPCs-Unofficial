package noppes.npcs.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomParticleTypes;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.client.particles.CustomParticle;
import noppes.npcs.client.particles.CustomParticleType;
import noppes.npcs.client.util.ClientRecipeRegister;
import noppes.npcs.client.util.ShaderData;
import noppes.npcs.controllers.RecipeController;
import noppes.npcs.fluids.CustomFluid;
import noppes.npcs.mixin.client.IRecipeBookCategoriesMixin;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.CustomNPCsScheduler;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = CustomNpcs.MODID, value = Dist.CLIENT)
public class ClientRegisterEvents {

    private static final Map<ResourceLocation, ShaderData> SHADERS = new HashMap<>();

    /** HUD Bar Interface */
    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelow(
                VanillaGuiOverlay.CHAT_PANEL.id(),
                "cnpc_mail",
                ClientEventHandler::renderMailOverlay
        );
        event.registerBelow(
                VanillaGuiOverlay.CHAT_PANEL.id(),
                "cnpc_compass",
                ClientEventHandler::renderCompassOverlay
        );
        event.registerBelow(
                VanillaGuiOverlay.CHAT_PANEL.id(),
                "cnpc_nbtbook",
                ClientEventHandler::renderNbtBookOverlay
        );
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) {
        File dir = new File(CustomNpcs.Dir, "assets/" + CustomNpcs.MODID + "/shaders/core");
        for (File file : Util.instance.getFiles(dir, ".json")) {
            ShaderData shaderData = ShaderData.of(file);
            try {
                event.registerShader(
                        new ShaderInstance(event.getResourceProvider(), shaderData.id, shaderData.format),
                        shader -> {
                            shaderData.shader = shader;
                            SHADERS.put(shaderData.id, shaderData);
                        }
                );
                LogWriter.debug("Load shader \"" + shaderData.id + "\"");
            }
            catch (Exception e) { LogWriter.error("Error load shader", e); }
        }
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (ICustomElement fluid : CustomBlocks.customfluid.values()) {
                if (fluid instanceof CustomFluid liquid) {
                    ItemBlockRenderTypes.setRenderLayer(liquid.getSource(), RenderType.translucent());
                    ItemBlockRenderTypes.setRenderLayer(liquid.getFlowing(), RenderType.translucent());
                }
            }
        });
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
    @SuppressWarnings("ConstantConditions")
    public static void cnpcsRegisterRecipe(RegisterRecipeBookCategoriesEvent event) {
        event.registerBookCategories(RecipeController.CRAFTING_CUSTOM_GLOBAL, RecipeBookCategories.CRAFTING_CATEGORIES);
        event.registerBookCategories(RecipeController.CRAFTING_CUSTOM_ANVIL, ClientRecipeRegister.CRAFTING_CUSTOM_ANVIL_CATEGORIES);
        CustomNPCsScheduler.runTack(() -> {
            while (true) {
                if (CustomItems.wand != null && CustomItems.cloner != null && CustomBlocks.carpenty_item != null) {
                    try {
                        ((IRecipeBookCategoriesMixin) (Object) ClientRecipeRegister.CRAFTING_CUSTOM_GLOBAL_CATEGORY).setItemIcons(
                                ImmutableList.of(new ItemStack(CustomItems.wand), new ItemStack(CustomItems.cloner))
                        );
                        ((IRecipeBookCategoriesMixin) (Object) ClientRecipeRegister.CRAFTING_CUSTOM_ANVIL_CATEGORY).setItemIcons(
                                ImmutableList.of(new ItemStack(CustomBlocks.carpenty_item))
                        );
                    } catch (Exception e) {
                        LogWriter.error(e);
                    }
                    break;
                }
            }
        });
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

    public static ShaderData getShader(ResourceLocation id) { return SHADERS.get(id); }

}
