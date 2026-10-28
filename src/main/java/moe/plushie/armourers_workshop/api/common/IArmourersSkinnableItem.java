package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;

@SuppressWarnings("unused")
public interface IArmourersSkinnableItem {

	boolean isSkinValidForItem(ISkinType skinType);

}
