package moe.plushie.armourers_workshop.api.common.capability;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import net.minecraft.inventory.EntityEquipmentSlot;

@SuppressWarnings("unused")
public interface IPlayerWardrobeCap extends IWardrobeCap {

	boolean getArmourOverride(EntityEquipmentSlot equipmentSlot);

	int getMaxSlotsForSkinType(ISkinType skinType);

	int getUnlockedSlotsForSkinType(ISkinType skinType);

	void setArmourOverride(EntityEquipmentSlot equipmentSlot, boolean override);

	void setUnlockedSlotsForSkinType(ISkinType skinType, int count);

}
