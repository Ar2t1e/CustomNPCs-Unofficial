package noppes.npcs.potions;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.wrapper.NBTWrapper;

import javax.annotation.Nonnull;

public class PotionData implements ICustomElement {

	public final @Nonnull ResourceLocation location;
	public final @Nonnull NBTTagCompound nbtData;

	public final @Nonnull PotionType POTION_TYPE;
	public final @Nonnull CustomPotion POTION;

	public PotionData(@Nonnull ResourceLocation locationIn, @Nonnull NBTTagCompound nbtPotionIn) {
		location = locationIn;
		nbtData = nbtPotionIn;

		POTION = new CustomPotion(locationIn, nbtData);
		int delay = nbtData.getBoolean("IsInstant") ? 0 :
				nbtData.hasKey("BaseDelay", 3) ? nbtData.getInteger("BaseDelay") : 200;
		POTION_TYPE = new PotionType(locationIn.getResourcePath(),
				new PotionEffect(POTION, nbtData.getBoolean("IsInstant") ? 0 : delay))
				.setRegistryName(locationIn);

        /*
        // PotionBrewing
        addMix(Potions.AWKWARD, Items.BLAZE_POWDER, POTION);
        addMix(POTION, Items.REDSTONE, LONG);
        addMix(POTION, Items.GLOWSTONE_DUST, STRONG);
        */
	}

	@Override
	public String getCustomName() { return nbtData.getString("RegistryName"); }

	@Override
	public INbt getCustomNbt() { return new NBTWrapper(nbtData); }

	@Override
	public int getElementType() {
		if (nbtData.hasKey("ItemType", 1)) { return nbtData.getByte("ItemType"); }
		return 7;
	}

	@Override
	public boolean showInCreative() { return !nbtData.hasKey("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative"); }

}
