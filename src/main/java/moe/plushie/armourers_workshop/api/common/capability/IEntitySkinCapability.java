package moe.plushie.armourers_workshop.api.common.capability;

import moe.plushie.armourers_workshop.api.common.ISkinInventoryContainer;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

@SuppressWarnings("unused")
public interface IEntitySkinCapability {

	/**
	 * Checks if the entity can hold this skin type.
	 */
    boolean canHoldSkinType(ISkinType skinType);

	void clear();

	void clearSkin(ISkinType skinType, int slotIndex);

	/**
	 * Get the skin descriptor for the ISkinType and slot index.
	 */
    ISkinDescriptor getSkinDescriptor(ISkinType skinType, int slotIndex);

    ISkinInventoryContainer getSkinInventoryContainer();

	ItemStack getSkinStack(ISkinType skinType, int slotIndex);

	/**
	 * Gets the number of this skin that the entity can hold.
	 */
    int getSlotCountForSkinType(ISkinType skinType);

	ISkinType[] getValidSkinTypes();

	/**
	 * Sets the skin descriptor for the ISkinType and slot.
	 *
	 * @return ISkinDescriptor that was in the slot. Returns input ISkinDescriptor
	 *         on fail.
	 */
    ISkinDescriptor setSkinDescriptor(ISkinType skinType, int slotIndex, ISkinDescriptor skinDescriptor);

	ItemStack setSkinStack(ISkinType skinType, int slotIndex, ItemStack skinStack);

	boolean setStackInNextFreeSlot(ItemStack stack);

	/**
	 * Syncs capability data to all players tracking the entity.
	 */
    void syncToAllTracking();

	/**
	 * Syncs capability data to a player.
	 * 
	 * @param entityPlayer
	 *            Player to sync to.
	 */
    void syncToPlayer(EntityPlayerMP entityPlayer);

}
