package noppes.npcs.blocks.custom;

import net.minecraft.block.BlockStairs;
import net.minecraft.block.SoundType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomTabs;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CustomBlockStairs extends BlockStairs implements ICustomElement {

	public NBTTagCompound nbtData;

	public CustomBlockStairs(NBTTagCompound nbtBlock) {
		super(Blocks.COBBLESTONE.getDefaultState());
		nbtData = nbtBlock;
		String name = "custom_" + nbtBlock.getString("RegistryName");
		setRegistryName(CustomNpcs.MODID, name.toLowerCase());
		setUnlocalizedName(name.toLowerCase());

		enableStats = true;
		blockSoundType = SoundType.STONE;
		blockParticleGravity = 1.0F;
		lightOpacity = fullBlock ? 255 : 0;
		translucent = !blockMaterial.blocksLight();
		setHardness(0.0f);
		setResistance(10.0f);
		if (nbtBlock.hasKey("Hardness", 5)) { setHardness(nbtBlock.getFloat("Hardness")); }
		if (nbtBlock.hasKey("Resistance", 5)) { setResistance(nbtBlock.getFloat("Resistance")); }
		if (nbtBlock.hasKey("LightLevel", 5)) { setLightLevel(nbtBlock.getFloat("LightLevel")); }
		setSoundType(CustomBlock.getNbtSoundType(nbtBlock.getString("SoundType")));
		setCreativeTab(CustomTabs.BLOCKS);
	}

	@Override
	public void getSubBlocks(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
		if (showInCreative() && (tab == CustomTabs.BLOCKS || tab == CreativeTabs.SEARCH)) {
			items.add(new ItemStack(this));
			if (tab == CustomTabs.BLOCKS) { Util.instance.sort(items); }
		}
	}

	@Override
	public String getCustomName() { return nbtData.getString("RegistryName"); }

	@Override
	public INbt getCustomNbt() { return Objects.requireNonNull(NpcAPI.Instance()).getINbt(nbtData); }

	@Override
	public int getElementType() {
		if (nbtData != null && nbtData.hasKey("BlockType", 1)) { return nbtData.getByte("BlockType"); }
		return 3;
	}

	@Override
	public boolean showInCreative() {
		return !nbtData.hasKey("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative");
	}

}
