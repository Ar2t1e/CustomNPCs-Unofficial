package noppes.npcs.fluids;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;

import java.util.Objects;

public class CustomFluid extends Fluid implements ICustomElement {

	private int mapColor;

	public NBTTagCompound nbtData;

	public CustomFluid(NBTTagCompound nbtBlock) {
		super("custom_fluid_" + nbtBlock.getString("RegistryName"),
				new ResourceLocation(CustomNpcs.MODID,
						"fluids/custom_fluid_" + nbtBlock.getString("RegistryName") + "_still"),
				new ResourceLocation(CustomNpcs.MODID,
						"fluids/custom_fluid_" + nbtBlock.getString("RegistryName") + "_flow"),
				new ResourceLocation(CustomNpcs.MODID,
						"fluids/custom_fluid_" + nbtBlock.getString("RegistryName") + "_overlay"));
		nbtData = nbtBlock;
		mapColor = nbtBlock.hasKey("Color", 3) ? nbtBlock.getInteger("Color") : 0xFFFFFFFF;
		setDensity(nbtBlock.hasKey("Density", 3) ? nbtBlock.getInteger("Density") : 1100);
		setGaseous(nbtBlock.hasKey("IsGaseous", 1) && nbtBlock.getBoolean("IsGaseous"));
		setLuminosity(nbtBlock.hasKey("Luminosity", 3) ? nbtBlock.getInteger("Luminosity") : 5);
		setViscosity(nbtBlock.hasKey("Viscosity", 3) ? nbtBlock.getInteger("Viscosity") : 900);
		setTemperature(nbtBlock.hasKey("Temperature", 3) ? nbtBlock.getInteger("Temperature") : 300);
		setUnlocalizedName("custom_fluid_" + nbtBlock.getString("RegistryName"));
	}

	@Override
	public int getColor() { return mapColor; }

	@Override
	public Fluid setColor(int parColor) {
		mapColor = parColor;
		return this;
	}

	@Override
	public String getCustomName() { return nbtData.getString("RegistryName"); }

	@Override
	public INbt getCustomNbt() { return Objects.requireNonNull(NpcAPI.Instance()).getINbt(nbtData); }

	@Override
	public int getElementType() {
		if (nbtData.hasKey("BlockType", 1)) { return nbtData.getByte("BlockType"); }
		return 1;
	}

	@Override
	public boolean showInCreative() { return !nbtData.hasKey("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative"); }

}
