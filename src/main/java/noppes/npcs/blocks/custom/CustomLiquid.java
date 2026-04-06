package noppes.npcs.blocks.custom;

import net.minecraft.block.material.Material;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CustomLiquid extends BlockFluidClassic implements ICustomElement {

	protected final @Nonnull NBTTagCompound nbtData;

	public CustomLiquid(@Nonnull Fluid fluid, @Nonnull Material material, @Nonnull NBTTagCompound nbtBlock) {
		super(fluid, material);
		nbtData = nbtBlock;
		String name = "custom_fluid_" + nbtBlock.getString("RegistryName");
		setRegistryName(CustomNpcs.MODID, name.toLowerCase());
		setUnlocalizedName(name.toLowerCase());
	}

	@Override
	public boolean isReplaceable(@Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) { return true; }

	@Override
	public String getCustomName() { return nbtData.getString("RegistryName"); }

	@Override
	public INbt getCustomNbt() { return Objects.requireNonNull(NpcAPI.Instance()).getINbt(nbtData); }

	@Override
	public int getElementType() {
		if (nbtData != null && nbtData.hasKey("BlockType", 1)) { return nbtData.getByte("BlockType"); }
		return 1;
	}

	@Override
	public boolean showInCreative() {
		return !nbtData.hasKey("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative");
	}

}
