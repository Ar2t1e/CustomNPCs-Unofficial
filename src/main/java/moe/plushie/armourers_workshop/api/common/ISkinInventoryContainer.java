package moe.plushie.armourers_workshop.api.common;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public interface ISkinInventoryContainer {

	void clear();

	void dropItems(World world, Vec3d pos);

	void readFromNBT(NBTTagCompound compound);

	void writeToNBT(NBTTagCompound compound);

}
