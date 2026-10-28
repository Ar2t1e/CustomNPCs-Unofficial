package moe.plushie.armourers_workshop.api.common.painting;

import moe.plushie.armourers_workshop.api.common.IExtraColours.ExtraColourType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("unused")
public interface IPaintType {

	int getChannelIndex();

	ExtraColourType getColourType();

	int getId();

	@SideOnly(Side.CLIENT)
	String getLocalizedName();

	int getMarkerIndex();

	String getName();

	float getU();

	String getUnlocalizedName();

	float getV();

	boolean hasAverageColourChannel();

	void setColourChannelIndex(int channelIndex);

}
