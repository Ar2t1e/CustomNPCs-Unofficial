package noppes.npcs.items.custom;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
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

public class CustomItem extends Item implements ICustomElement {

	public static Material getMaterial(String materialName) {
		switch (materialName.toLowerCase()) {
		case "air":
			return Material.AIR;
		case "grass":
			return Material.GRASS;
		case "ground":
			return Material.GROUND;
		case "wood":
			return Material.WOOD;
		case "iron":
			return Material.IRON;
		case "anvil":
			return Material.ANVIL;
		case "water":
			return Material.WATER;
		case "lava":
			return Material.LAVA;
		case "leaves":
			return Material.LEAVES;
		case "plants":
			return Material.PLANTS;
		case "vine":
			return Material.VINE;
		case "sponge":
			return Material.SPONGE;
		case "cloth":
			return Material.CLOTH;
		case "fire":
			return Material.FIRE;
		case "sand":
			return Material.SAND;
		case "circuits":
			return Material.CIRCUITS;
		case "carpet":
			return Material.CARPET;
		case "glass":
			return Material.GLASS;
		case "redstone_light":
			return Material.REDSTONE_LIGHT;
		case "tnt":
			return Material.TNT;
		case "coral":
			return Material.CORAL;
		case "ice":
			return Material.ICE;
		case "packed_ice":
			return Material.PACKED_ICE;
		case "snow":
			return Material.SNOW;
		case "crafted_snow":
			return Material.CRAFTED_SNOW;
		case "cactus":
			return Material.CACTUS;
		case "clay":
			return Material.CLAY;
		case "gourd":
			return Material.GOURD;
		case "dragon_egg":
			return Material.DRAGON_EGG;
		case "portal":
			return Material.PORTAL;
		case "cake":
			return Material.CAKE;
		case "web":
			return Material.WEB;
		case "piston":
			return Material.PISTON;
		case "barrier":
			return Material.BARRIER;
		case "structure_void":
			return Material.STRUCTURE_VOID;
		default:
			return Material.ROCK;
		}
	}

	public static Item.ToolMaterial getMaterialTool(NBTTagCompound nbtItem) {
		String materialName = nbtItem.hasKey("Material", 8) ? nbtItem.getString("Material").toLowerCase() : "stone";
		switch (materialName) {
		case "wood":
			return Item.ToolMaterial.WOOD;
		case "iron":
			return Item.ToolMaterial.IRON;
		case "diamond":
			return Item.ToolMaterial.DIAMOND;
		case "gold":
			return Item.ToolMaterial.GOLD;
		default:
			return Item.ToolMaterial.STONE;
		}
	}

	protected final NBTTagCompound nbtData;
	protected final int enchantability;
	protected final ItemStack repairItemStack;
	protected final Item.ToolMaterial toolMaterial;
	protected final Material collectionMaterial;
	protected final float speedCollectionMaterial;
	protected final float efficiency;
	protected final Set<Block> effectiveBlocks = new HashSet<>();
	protected final double attackDamage;
	protected final double attackSpeed;

	public CustomItem(NBTTagCompound nbtItem) {
		super();
		nbtData = nbtItem;
		setRegistryName(CustomNpcs.MODID, "custom_" + nbtItem.getString("RegistryName"));
		setUnlocalizedName("custom_" + nbtItem.getString("RegistryName"));
		maxStackSize = nbtItem.hasKey("MaxStackSize", 3) ? nbtItem.getInteger("MaxStackSize") : 64;
		if (maxStackSize > 64) { maxStackSize = 64; }
		if (nbtItem.hasKey("IsFull3D", 1) && nbtItem.getBoolean("IsFull3D")) { setFull3D(); }
		if (nbtItem.getInteger("MaxStackDamage") > 1) { setMaxDamage(nbtItem.getInteger("MaxStackDamage")); }

		if (nbtItem.hasKey("SpeedAttack", 6)) { attackSpeed = nbtItem.getDouble("SpeedAttack"); }
		else { attackSpeed = -2.4d; }
		if (nbtItem.hasKey("EntityDamage", 6)) { attackDamage = nbtItem.getDouble("EntityDamage"); }
		else { attackDamage = 0.0f; }
		if (nbtItem.hasKey("Efficiency", 5)) { efficiency = nbtItem.getFloat("Efficiency"); }
		else { efficiency = 1.0f; }
		if (nbtItem.hasKey("CollectionMaterial", 10)) {
			collectionMaterial = CustomItem.getMaterial(nbtItem.getCompoundTag("collectionMaterial").getString("Material"));
			speedCollectionMaterial = nbtItem.getCompoundTag("collectionMaterial").getFloat("Speed");
		}
		else {
			collectionMaterial = null;
			speedCollectionMaterial = 1.0f;
		}
		if (nbtItem.hasKey("Enchantability", 3)) { enchantability = nbtItem.getInteger("Enchantability"); }
		else { enchantability = 10; }
		if (nbtItem.hasKey("RepairItem", 10)) { repairItemStack = new ItemStack(nbtItem.getCompoundTag("RepairItem")); }
		else { repairItemStack = ItemStack.EMPTY; }
		toolMaterial = CustomItem.getMaterialTool(nbtItem);
		if (nbtItem.hasKey("CollectionBlocks", 9)) {
			for (int j = 0; j < nbtItem.getTagList("CollectionBlocks", 8).tagCount(); j++) {
				Block block = Block.getBlockFromName(nbtItem.getTagList("CollectionBlocks", 8).getStringTagAt(j));
				if (block != null) {
					effectiveBlocks.add(block);
				}
			}
		}
		setCreativeTab(CustomTabs.ITEMS);
		setHasSubtypes(true);
	}

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull IBlockState state) {
		if (state.getMaterial() == collectionMaterial) { return speedCollectionMaterial; }
		else if (effectiveBlocks.contains(state.getBlock())) {
			for (String type : getToolClasses(stack)) {
				if (state.getBlock().isToolEffective(type, state)) {
					return efficiency;
				}
			}
			return efficiency;
		}
		else if (state.getBlock() == Blocks.WEB) { return 15.0F; }
		return 1.0f;
	}

	@Override
	public double getDurabilityForDisplay(@Nonnull ItemStack stack) {
		if (nbtData != null && nbtData.hasKey("DurabilityValue", 6)) {
			return 1.0 - nbtData.getDouble("DurabilityValue");
		}
		return super.getDurabilityForDisplay(stack);
	}

	@Override
	public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
		if (showInCreative() && (tab == CustomTabs.ITEMS || tab == CreativeTabs.SEARCH)) {
			items.add(new ItemStack(this));
			if (tab == CustomTabs.ITEMS) { Util.instance.sort(items); }
		}
	}

	@Override
	public String getCustomName() { return nbtData.getString("RegistryName"); }

	@Override
	public INbt getCustomNbt() { return Objects.requireNonNull(NpcAPI.Instance()).getINbt(nbtData); }

	@Override
	public int getElementType() {
		if (nbtData != null && nbtData.hasKey("ItemType", 1)) { return nbtData.getByte("ItemType"); }
		return 0;
	}

	@Override
	public boolean showInCreative() { return !nbtData.hasKey("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative"); }

}
