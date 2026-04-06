package noppes.npcs.api.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.api.interfaces.ParamName;

import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public interface IRenderSystem {

    void enableBlend();
    void disableBlend();
    PoseStack pushPose(@ParamName("graphics") GuiGraphics graphics);
    PoseStack popPose(@ParamName("graphics") GuiGraphics graphics);

    void color(@ParamName("red") float red, @ParamName("green") float green, @ParamName("blue") float blue, @ParamName("alpha") float alpha);
    void translate(@ParamName("graphics") GuiGraphics graphics, @ParamName("x") float x, @ParamName("y") float y, @ParamName("z") float z);
    void scale(@ParamName("graphics") GuiGraphics graphics, @ParamName("x") float x, @ParamName("y") float y, @ParamName("z") float z);
    void rotate(@ParamName("graphics") GuiGraphics graphics, @ParamName("angle") float angle,
                @ParamName("axisX") float axisX, @ParamName("axisY") float axisY, @ParamName("axisZ") float axisZ);

    void drawString(@ParamName("graphics") GuiGraphics graphics, @ParamName("text") String text,
                    @ParamName("x") float x, @ParamName("y") float y, @ParamName("color") int color, @ParamName("dropShadow") boolean dropShadow);
    void drawTexture(@ParamName("graphics") GuiGraphics graphics, @ParamName("resourceLocation") String resourceLocation,
                     @ParamName("x") float x, @ParamName("y") float y, @ParamName("u") int u, @ParamName("v") int v,
                     @ParamName("width") float width, @ParamName("height") float height, @ParamName("revers") boolean revers);
    void draw(@ParamName("graphics") GuiGraphics graphics,
              @ParamName("left") int left, @ParamName("top") int top, @ParamName("width") int width, @ParamName("height") int height,
              @ParamName("color") int color);
    void draw(@ParamName("graphics") GuiGraphics graphics,
              @ParamName("left") int left, @ParamName("top") int top, @ParamName("width") int width, @ParamName("height") int height,
              @ParamName("red") float red, @ParamName("green") float green, @ParamName("blue") float blue,
              @ParamName("alpha") float alpha);
    void renderEntity(@ParamName("graphics") GuiGraphics graphics, @ParamName("entity") Entity entity,
                      @ParamName("x") int x, @ParamName("y") int y, @ParamName("scale") float scale,
                      @ParamName("yaw") int yaw, @ParamName("pitch") int pitch, @ParamName("followCursor") int followCursor);
    void drawOBJ(@ParamName("graphics") GuiGraphics graphics, @ParamName("resourceLocation") String resourceLocation,
                 @ParamName("visibleMeshes") List<String> visibleMeshes, @ParamName("materialTextures") Map<String, ResourceLocation> materialTextures,
                 @ParamName("lightMap") int lightMap, @ParamName("overlay") int overlay);

}
