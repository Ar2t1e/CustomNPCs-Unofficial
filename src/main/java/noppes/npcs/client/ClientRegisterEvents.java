package noppes.npcs.client;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomParticleTypes;
import noppes.npcs.client.particles.CustomParticle;
import noppes.npcs.client.particles.CustomParticleType;
import noppes.npcs.client.util.ClientRecipeRegister;
import noppes.npcs.controllers.RecipeController;

import javax.annotation.Nonnull;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = CustomNpcs.MODID, value = Dist.CLIENT)
public class ClientRegisterEvents {

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



}
