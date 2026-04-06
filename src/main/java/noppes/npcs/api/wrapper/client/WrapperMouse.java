package noppes.npcs.api.wrapper.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.api.client.IClientMouse;
import noppes.npcs.mixin.client.IMouseHandlerMixin;

@OnlyIn(Dist.CLIENT)
public class WrapperMouse implements IClientMouse {

    private final Minecraft minecraft;

    public WrapperMouse(Minecraft mc) { minecraft = mc; }

    @Override
    public int getX() { return (int) Math.floor(minecraft.mouseHandler.xpos()); }

    @Override
    public int getY() { return (int) Math.floor(minecraft.mouseHandler.ypos()); }

    @Override
    public boolean isLeftPressed() { return minecraft.mouseHandler.isLeftPressed(); }

    @Override
    public boolean isRightPressed() { return minecraft.mouseHandler.isRightPressed(); }

    @Override
    public boolean isPressed(int buttonId) { return ((IMouseHandlerMixin) Minecraft.getInstance().mouseHandler).getActiveButton() == buttonId; }

}
