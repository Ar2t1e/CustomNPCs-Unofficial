package noppes.npcs.api.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.api.entity.IPlayer;
import noppes.npcs.api.interfaces.ParamName;

@SuppressWarnings("all")
@SideOnly(Side.CLIENT)
public interface IMinecraft {
	
	Minecraft getMc();
	
	GuiScreen getCurrentScreen();

	IClientMouse getMouseHandler();

	int getDisplayWidth();
	
	int getDisplayHeight();
	
	double getWidth();
	
	double getHeight();
	
	float getRenderPartialTicks();
	
	IResourceManager getResourceManager();
	
	TextureManager getTextureManager();
	
	SoundHandler getSoundHandler();
	
	ItemRenderer getItemRenderer();
	
	GameSettings getGameSettings();

	IRenderSystem getRenderSystem();
	
	void playSound(@ParamName("category") String category, @ParamName("sound") String sound,
                   @ParamName("x") float x, @ParamName("y") float y, @ParamName("z") float z,
                   @ParamName("volume") float volume, @ParamName("pitch") float pitch);
	
	void stopSound(@ParamName("category") String category, @ParamName("sound") String sound);
	
	float getSoundVolume(@ParamName("category") String category);
	
	void setSoundVolume(@ParamName("category") String category, @ParamName("volume") float volume);

    IPlayer<?> getPlayer();
}
