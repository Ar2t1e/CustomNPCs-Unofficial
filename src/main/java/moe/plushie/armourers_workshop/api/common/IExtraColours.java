package moe.plushie.armourers_workshop.api.common;

@SuppressWarnings("unused")
public interface IExtraColours {

	enum ExtraColourType { SKIN, HAIR, EYE, MISC_1, MISC_2, MISC_3, MISC_4 }

	int getColour(ExtraColourType type);

	byte[] getColourBytes(ExtraColourType type);

	void setColour(ExtraColourType type, int trgb);

	void setColourBytes(ExtraColourType type, byte[] rgbt);

}
