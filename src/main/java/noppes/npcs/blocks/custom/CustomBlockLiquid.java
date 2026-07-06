package noppes.npcs.blocks.custom;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.mixin.block.material.IMapColorMixin;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CustomBlockLiquid extends BlockFluidClassic implements ICustomElement {

	protected final @Nonnull NBTTagCompound nbtData;
	private final MapColor customColor;

	public CustomBlockLiquid(@Nonnull Fluid fluid, @Nonnull Material material, @Nonnull NBTTagCompound nbtBlock) {
		super(fluid, material);
		nbtData = nbtBlock;
		String name = "custom_" + nbtBlock.getString("RegistryName");
		setRegistryName(CustomNpcs.MODID, name.toLowerCase());
		setUnlocalizedName(name.toLowerCase());

		NBTTagCompound fluidType = nbtBlock.getCompoundTag("FluidType");
		if (fluidType.hasKey("lightLevel", 3)) {
			setLightLevel(fluidType.getInteger("lightLevel") / 15.0f);
		}
		if (nbtBlock.hasKey("mapColor", 3)) { customColor =  IMapColorMixin.create(0, nbtBlock.getInteger("mapColor")); }
		else { customColor = MapColor.WATER; }
	}

	@Override
	@SuppressWarnings("deprecation")
	public @Nonnull MapColor getMapColor(@Nonnull IBlockState state, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) { return customColor; }

	@Override
	public boolean isReplaceable(@Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) { return true; }

	@Override
	public String getCustomName() { return nbtData.getString("RegistryName"); }

	@Override
	public INbt getCustomNbt() { return Objects.requireNonNull(NpcAPI.Instance()).getINbt(nbtData); }

	@Override
	public int getElementType() { return 1; }

	@Override
	public boolean showInCreative() { return false; }

}