package moe.plushie.armourers_workshop.api.common.skin.data;

import moe.plushie.armourers_workshop.api.common.skin.Point3D;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import net.minecraft.util.EnumFacing;

@SuppressWarnings("unused")
public interface ISkinPart {

	Point3D getMarker(int index);

	int getMarkerCount();

	EnumFacing getMarkerSide(int index);

	ISkinPartType getPartType();

}
