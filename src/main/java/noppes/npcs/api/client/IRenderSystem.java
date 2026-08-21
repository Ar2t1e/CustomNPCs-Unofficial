package noppes.npcs.api.client;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.api.interfaces.ParamName;

import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
@SideOnly(Side.CLIENT)
public interface IRenderSystem {

	void enableBlend();
	void disableBlend();
	void enableAlpha();
	void disableAlpha();
	void pushMatrix();
	void popMatrix();

	void color(@ParamName("red") float red, @ParamName("green") float green, @ParamName("blue") float blue, @ParamName("alpha") float alpha);
	void translate(@ParamName("x") float x, @ParamName("y") float y, @ParamName("z") float z);
	void scale(@ParamName("x") float x, @ParamName("y") float y, @ParamName("z") float z);
	void rotate(@ParamName("angle") float angle, @ParamName("x") float x, @ParamName("y") float y, @ParamName("z") float z);
	
	void drawString(@ParamName("text") String text,
					@ParamName("x") float x, @ParamName("y") float y,
					@ParamName("color") int color, @ParamName("dropShadow") boolean dropShadow);
	void drawTexture(@ParamName("resourceLocation") String resourceLocation, @ParamName("x") double x, @ParamName("y") double y, @ParamName("z") double z,
					 @ParamName("u") double u, @ParamName("v") double v, @ParamName("width") double width, @ParamName("height") double height,
					 @ParamName("revers") boolean revers);
	void draw(@ParamName("left") double left, @ParamName("top") double top, @ParamName("width") double width, @ParamName("height") double height,
			  @ParamName("color") int color, @ParamName("alpha") float alpha);
	void draw(@ParamName("left") double left, @ParamName("top") double top, @ParamName("width") double width, @ParamName("height") double height,
			  @ParamName("red") float red, @ParamName("green") float green, @ParamName("blue") float blue, @ParamName("alpha") float alpha);
	void renderEntity(@ParamName("entity") Entity entity,
					  @ParamName("x") int x, @ParamName("y") int y, @ParamName("zoomed") float scale,
					  @ParamName("yaw") int yaw, @ParamName("pitch") int pitch,
					  @ParamName("guiLeft") float guiLeft, @ParamName("guiTop") float guiTop,
					  @ParamName("followCursor") int followCursor);
	void drawOBJ(@ParamName("resourceLocation") String resourceLocation,
				 @ParamName("visibleMeshes") List<String> visibleMeshes, @ParamName("materialTextures") Map<String, String> materialTextures);
	
}
