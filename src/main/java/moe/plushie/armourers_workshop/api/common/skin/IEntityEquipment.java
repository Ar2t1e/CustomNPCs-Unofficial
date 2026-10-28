package moe.plushie.armourers_workshop.api.common.skin;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;

@SuppressWarnings("unused")
public interface IEntityEquipment {

	void addEquipment(ISkinType skinType, int slotIndex, ISkinDescriptor skinPointer);

	@Deprecated
    int getEquipmentId(ISkinType skinType, int slotIndex);

	@Deprecated
    int getNumberOfSlots();

	@Deprecated
    ISkinDye getSkinDye(ISkinType skinType, int slotIndex);

	ISkinDescriptor getSkinPointer(ISkinType skinType, int slotIndex);

	boolean haveEquipment(ISkinType skinType, int slotIndex);

	void removeEquipment(ISkinType skinType, int slotIndex);

}
