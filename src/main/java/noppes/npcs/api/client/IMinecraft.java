package noppes.npcs.api.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.entity.IPlayer;

@OnlyIn(Dist.CLIENT)
public interface IMinecraft {

    Minecraft getMc();

    Screen getCurrentScreen();

    int getDisplayWidth();

    int getDisplayHeight();

    double getWidth();

    double getHeight();

    float getRenderPartialTicks();

    ResourceManager getResourceManager();

    TextureManager getTextureManager();

    SoundManager getSoundHandler();

    ItemRenderer getItemRenderer();

    Options getGameSettings();

    IRenderSystem getRenderSystem();

    IClientMouse getMouseHandler();

    void playSound(@ParamName("category") String category, @ParamName("sound") String sound,
                   @ParamName("x") float x, @ParamName("y") float y, @ParamName("z") float z,
                   @ParamName("volume") float volume, @ParamName("pitch") float pitch, @ParamName("isLooping") boolean isLooping);

    void stopSound(@ParamName("category") String category, @ParamName("sound") String sound);

    float getSoundVolume(@ParamName("category") String category);

    void setSoundVolume(@ParamName("category") String category, @ParamName("volume") double volume);

    IPlayer<?> getPlayer();

}
