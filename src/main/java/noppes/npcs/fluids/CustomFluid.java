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

	private int tintColor;
	private final int fogColor;
	private final int mapColor;
	private final int lightLevel;
	private final int tickRate;
	private final int slopeFindDistance;
	private final int levelDecreasePerBlock;
	private final float explosionResistance;

	public NBTTagCompound nbtData;

	public CustomFluid(NBTTagCompound nbtBlock) {
		super("custom_fluid_" + nbtBlock.getString("RegistryName"),
				new ResourceLocation(CustomNpcs.MODID,
						"block/" + nbtBlock.getString("RegistryName") + "_still"),
				new ResourceLocation(CustomNpcs.MODID,
						"block/" + nbtBlock.getString("RegistryName") + "_flow"),
				new ResourceLocation(CustomNpcs.MODID,
						"block/" + nbtBlock.getString("RegistryName") + "_overlay"));
		nbtData = nbtBlock;

		mapColor = nbtBlock.hasKey("mapColor", 3) ? nbtBlock.getInteger("mapColor") : 0xFFFFFFFF;

		NBTTagCompound fluidType = nbtBlock.getCompoundTag("FluidType");
		tintColor = fluidType.hasKey("tintColor", 3) ? fluidType.getInteger("tintColor") : 0xFFFFFFFF;
		fogColor = fluidType.hasKey("fogColor", 3) ? fluidType.getInteger("fogColor") : 0x3C6EDC;
		lightLevel = fluidType.hasKey("lightLevel", 3) ? fluidType.getInteger("lightLevel") : 0;
		tickRate = fluidType.hasKey("tickRate", 3) ? fluidType.getInteger("tickRate") : 5;
		slopeFindDistance = fluidType.hasKey("slopeFindDistance", 3) ? fluidType.getInteger("slopeFindDistance") : 4;
		levelDecreasePerBlock = fluidType.hasKey("levelDecreasePerBlock", 3) ? fluidType.getInteger("levelDecreasePerBlock") : 1;

		setDensity(fluidType.hasKey("density", 3) ? fluidType.getInteger("density") : 1100);
		setLuminosity(fluidType.hasKey("lightLevel", 3) ? fluidType.getInteger("lightLevel") : 5);
		setViscosity(fluidType.hasKey("viscosity", 3) ? fluidType.getInteger("viscosity") : 900);
		setTemperature(fluidType.hasKey("temperature", 3) ? fluidType.getInteger("temperature") : 300);
		setUnlocalizedName("custom_fluid_" + nbtBlock.getString("RegistryName"));

		NBTTagCompound properties = nbtBlock.getCompoundTag("Properties");
		setGaseous(properties.hasKey("liquid", 1) && !nbtBlock.getBoolean("liquid"));
		explosionResistance = properties.hasKey("explosionResistance", 5) ? properties.getFloat("explosionResistance") : 100.0f;
	}

	@Override
	public int getColor() { return tintColor; }

	@Override
	public Fluid setColor(int parColor) {
		tintColor = parColor;
		return this;
	}

	@SuppressWarnings("unused")
	public int getTintColor() { return tintColor; }
	public int getFogColor() { return fogColor; }
	public int getMapColor() { return mapColor; }

	public int getLightLevel() { return lightLevel; }
	public int getTickRate() { return tickRate; }
	public int getSlopeFindDistance() { return slopeFindDistance; }
	@SuppressWarnings("unused")
	public int getLevelDecreasePerBlock() { return levelDecreasePerBlock; }
	public float getExplosionResistance() { return explosionResistance; }

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
	public boolean showInCreative() {
		return !nbtData.hasKey("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative");
	}

}