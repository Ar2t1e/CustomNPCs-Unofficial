package moe.plushie.armourers_workshop.api.common.painting;

import moe.plushie.armourers_workshop.api.common.skin.cubes.ICubeColour;

@SuppressWarnings("unused")
public interface IPantable {

	ICubeColour getColour();

	int getColour(int side);

	IPaintType getPaintType(int side);

	void setColour(byte[] rgb, int side);

	void setColour(ICubeColour colour);

	/** @deprecated Replaced by {@link #setColour(byte[] rgb, int side)} */
	@Deprecated
	void setColour(int colour);

	/** @deprecated Replaced by {@link #setColour(byte[] rgb, int side)} */
	@Deprecated
	void setColour(int colour, int side);

	void setPaintType(IPaintType paintType, int side);

}
