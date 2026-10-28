package moe.plushie.armourers_workshop.api.common;

import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

@SuppressWarnings("unused")
public interface ISkinNBTUtils {

	ISkinDescriptor getSkinDescriptor(ItemStack itemStack);

	ISkinDescriptor getSkinDescriptor(NBTTagCompound compound);

	boolean hasSkinDescriptor(ItemStack itemStack);

	boolean hasSkinDescriptor(NBTTagCompound compound);

	void removeSkinDescriptor(ItemStack itemStack);

	void removeSkinDescriptor(NBTTagCompound compound);

	// Item stack.
	void setSkinDescriptor(ItemStack itemStack, ISkinDescriptor skinDescriptor);

	// Tag compound.
	void setSkinDescriptor(NBTTagCompound compound, ISkinDescriptor skinDescriptor);

}
