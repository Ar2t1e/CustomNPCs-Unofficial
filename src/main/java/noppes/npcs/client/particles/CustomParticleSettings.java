package noppes.npcs.client.particles;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;

import java.util.Objects;

public class CustomParticleSettings implements ICustomElement {

	public final NBTTagCompound nbtData;
	public final int id;
	public final int argumentCount;
	public final boolean shouldIgnoreRange;
	public final String enumName;
	public final String name;

	public CustomParticleSettings(NBTTagCompound nbtParticle, int idIn) {
		nbtData = nbtParticle;
		id = idIn;
		String tempEnumName = nbtParticle.getString("RegistryName").toUpperCase();
		while (tempEnumName.contains(" ")) { tempEnumName = tempEnumName.replace(" ", "_"); }
		nbtParticle.setString("RegistryName", tempEnumName);
		name = tempEnumName.toLowerCase();
		enumName = "CUSTOM_" + tempEnumName;
		if (nbtParticle.hasKey("ShouldIgnoreRange", 1)) { shouldIgnoreRange = nbtParticle.getBoolean("ShouldIgnoreRange"); }
		else { shouldIgnoreRange = false; }
		if (nbtParticle.hasKey("ArgumentCount", 3)) { argumentCount = nbtParticle.getInteger("ArgumentCount"); }
		else { argumentCount = 0; }
	}

	@Override
	public String getCustomName() { return nbtData.getString("RegistryName").toLowerCase(); }

	@Override
	public INbt getCustomNbt() { return Objects.requireNonNull(NpcAPI.Instance()).getINbt(nbtData); }

	@Override
	public int getElementType() { return nbtData.hasKey("OBJModel", 8) && !nbtData.getString("OBJModel").isEmpty() ? 1 : 0; }

	@Override
	public boolean showInCreative() { return false; }

}
