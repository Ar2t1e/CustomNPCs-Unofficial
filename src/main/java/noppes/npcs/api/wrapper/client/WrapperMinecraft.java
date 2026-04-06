package noppes.npcs.api.wrapper.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.client.IClientMouse;
import noppes.npcs.api.client.IMinecraft;
import noppes.npcs.api.client.IRenderSystem;
import noppes.npcs.api.entity.IPlayer;

import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class WrapperMinecraft
implements IMinecraft {

    private final Minecraft minecraft;
    private final IRenderSystem glRenderSystem;
    private final IClientMouse iMouse;

    public WrapperMinecraft(Minecraft mc) {
        minecraft = mc;
        glRenderSystem = new WrapperRenderSystem(mc);
        iMouse = new WrapperMouse(mc);
    }

    @Override
    public Minecraft getMc() { return minecraft; }

    @Override
    public Screen getCurrentScreen() { return minecraft.screen; }

    @Override
    public IClientMouse getMouseHandler() { return iMouse; }

    @Override
    public int getDisplayWidth() { return minecraft.getWindow().getScreenWidth(); }

    @Override
    public int getDisplayHeight() { return minecraft.getWindow().getScreenHeight(); }

    @Override
    public float getRenderPartialTicks() { return minecraft.getPartialTick(); }

    @Override
    public ResourceManager getResourceManager() { return minecraft.getResourceManager(); }

    @Override
    public TextureManager getTextureManager() { return minecraft.getTextureManager(); }

    @Override
    public SoundManager getSoundHandler() { return minecraft.getSoundManager(); }

    @Override
    public ItemRenderer getItemRenderer() { return minecraft.getItemRenderer(); }

    @Override
    public double getWidth() { return minecraft.getWindow().getWidth(); }

    @Override
    public double getHeight() { return minecraft.getWindow().getHeight(); }

    @Override
    public IRenderSystem getRenderSystem() { return glRenderSystem; }

    @Override
    public void playSound(String category, String sound, float x, float y, float z, float volume, float pitch, boolean isLooping) {
        if (sound == null || sound.isEmpty()) { return; }
        if (y < 0 && minecraft.player != null) {
            x = (float) minecraft.player.getX();
            y = (float) minecraft.player.getY() + 0.9f;
            z = (float) minecraft.player.getZ();
        }
        SoundInstance.Attenuation aType = SoundInstance.Attenuation.LINEAR;
        SoundSource cat = getCategory(category);
        if (cat == SoundSource.MUSIC || minecraft.level == null) {
            if (cat == SoundSource.MUSIC) { getSoundHandler().stop(ResourceLocation.tryParse(sound), SoundSource.MUSIC); }
            aType = SoundInstance.Attenuation.NONE;
            x = minecraft.player != null ? (float) minecraft.player.getX() : 0.0f;
            y = minecraft.player != null ? (float) minecraft.player.getY() + 0.5f : 0.0f;
            z = minecraft.player != null ? (float) minecraft.player.getZ() : 0.0f;
        }
        getSoundHandler().play(new SimpleSoundInstance(new ResourceLocation(sound), SoundSource.RECORDS, volume, pitch, SoundInstance.createUnseededRandom(), isLooping, 0, aType, x, y, z, false));
    }

    @Override
    public void stopSound(String category, String sound) {
        if (sound == null) { sound = ""; }
        getSoundHandler().stop(ResourceLocation.tryParse(sound), getCategory(category));
    }

    @Override
    public Options getGameSettings() { return minecraft.options; }

    @Override
    public float getSoundVolume(String category) { return minecraft.options.getSoundSourceVolume(getCategory(category)); }

    @Override
    public void setSoundVolume(String category, double volume) { minecraft.options.getSoundSourceOptionInstance(getCategory(category)).set(volume); }

    @Override
    public IPlayer<?> getPlayer() {
        return (IPlayer<?>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(minecraft.player);
    }

    private SoundSource getCategory(String category) {
        if (category != null && !category.isEmpty()) {
            for (SoundSource c : SoundSource.values()) {
                if (c.getName().equalsIgnoreCase(category)) {
                    return c;
                }
            }
        }
        return SoundSource.AMBIENT;
    }

}
