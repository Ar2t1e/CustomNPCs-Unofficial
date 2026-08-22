package noppes.npcs.api.wrapper.client;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.api.client.IClientMouse;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class WrapperMouse implements IClientMouse {

    @Override
    public int getX() { return Mouse.getX(); }

    @Override
    public int getY() { return Mouse.getY(); }

    @Override
    public boolean isLeftPressed() { return Mouse.isButtonDown(0); }

    @Override
    public boolean isRightPressed() { return Mouse.isButtonDown(1); }

    @Override
    public boolean isPressed(int buttonId) { return Mouse.isButtonDown(buttonId); }

}
