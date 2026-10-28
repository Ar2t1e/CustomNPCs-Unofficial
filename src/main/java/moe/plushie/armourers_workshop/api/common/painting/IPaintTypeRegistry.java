package moe.plushie.armourers_workshop.api.common.painting;

import java.util.ArrayList;

@SuppressWarnings("unused")
public interface IPaintTypeRegistry {

	int getExtraChannels();

	IPaintType getPaintTypeFormByte(byte index);

	IPaintType getPaintTypeFormName(String name);

	IPaintType getPaintTypeFromColour(int trgb);

	IPaintType getPaintTypeFromIndex(int index);

	ArrayList<IPaintType> getRegisteredTypes();

	boolean registerPaintType(IPaintType paintType);

	int setPaintTypeOnColour(IPaintType paintType, int colour);

}
