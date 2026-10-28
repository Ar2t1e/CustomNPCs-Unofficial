package moe.plushie.armourers_workshop.api.common.skin.data;

import java.util.ArrayList;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;

@SuppressWarnings("unused")
public interface ISkin {

	ISkinType getSkinType();

	ArrayList<ISkinPart> getSubParts();

}
