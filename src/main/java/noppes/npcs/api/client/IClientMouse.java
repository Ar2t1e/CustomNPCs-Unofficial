package noppes.npcs.api.client;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.api.interfaces.ParamName;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public interface IClientMouse {

    int getX();

    int getY();

    boolean isLeftPressed();

    boolean isRightPressed();

    boolean isPressed(@ParamName("buttonId") int buttonId);

}
