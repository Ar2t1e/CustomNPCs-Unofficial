package noppes.npcs.api.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.api.interfaces.ParamName;

@OnlyIn(Dist.CLIENT)
public interface IClientMouse {

    int getX();

    int getY();

    boolean isLeftPressed();

    boolean isRightPressed();

    boolean isPressed(@ParamName("buttonId") int buttonId);

}

