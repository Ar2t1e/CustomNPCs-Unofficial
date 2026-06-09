package noppes.npcs.items.custom;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomTabs;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.mixin.item.IItemSwordMixin;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;

public class CustomWeapon extends ItemSword implements ICustomElement {

	protected final Multimap<String, AttributeModifier> defaultModifiers = HashMultimap.create();
	protected final @Nonnull NBTTagCompound nbtData;

	protected Material collectionMaterial = null;
	protected float speedCollectionMaterial = 1.0f;
	protected Set<Block> effectiveBlocks = new HashSet<>();
	protected float efficiency = 1.0f;
	protected ItemStack repairItemStack;
	protected int enchantability = 0;

	public CustomWeapon(@Nonnull Item.ToolMaterial material,  @Nonnull NBTTagCompound nbtItem) {
		super(material);
		nbtData = nbtItem;
		setRegistryName(CustomNpcs.MODID, "custom_" + nbtItem.getString("RegistryName"));
		setUnlocalizedName("custom_" + nbtItem.getString("RegistryName"));
		double attackSpeed = -2.4d;
		if (nbtItem.hasKey("SpeedAttack", 6)) { attackSpeed = nbtItem.getDouble("SpeedAttack"); }
		if (nbtItem.hasKey("EntityDamage", 6)) {
			((IItemSwordMixin) this).setEntityDamage((float) nbtItem.getDouble("EntityDamage"));
		}
		if (nbtItem.getInteger("MaxStackDamage") > 1) {
			setMaxDamage(nbtItem.getInteger("MaxStackDamage"));
		}
		if (nbtItem.hasKey("CollectionMaterial", 10)) {
			collectionMaterial = CustomItem.getMaterial(nbtItem.getCompoundTag("collectionMaterial").getString("Material"));
			speedCollectionMaterial = nbtItem.getCompoundTag("collectionMaterial").getFloat("Speed");
		}
		if (nbtItem.hasKey("CollectionBlocks", 9)) {
			for (int i = 0; i < nbtItem.getTagList("CollectionBlocks", 8).tagCount(); i++) {
				Block block = Block.getBlockFromName(nbtItem.getTagList("CollectionBlocks", 8).getStringTagAt(i));
				if (block != null) {
					effectiveBlocks.add(block);
				}
			}
		}
		if (nbtItem.hasKey("Efficiency", 5)) {
			efficiency = nbtItem.getFloat("Efficiency");
		}
		if (nbtItem.hasKey("RepairItem", 10)) {
			repairItemStack = new ItemStack(nbtItem.getCompoundTag("RepairItem"));
		}
		else {
			repairItemStack = material.getRepairItemStack();
		}
		if (nbtItem.hasKey("Enchantability", 3)) {
			enchantability = nbtItem.getInteger("Enchantability");
		}
		if (nbtItem.hasKey("IsFull3D", 1) && nbtItem.getBoolean("IsFull3D")) {
			setFull3D();
		}
		setCreativeTab(CustomTabs.ITEMS);
		defaultModifiers.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", getAttackDamage(), 0));
		defaultModifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", attackSpeed, 0));
	}

	@Override
	public boolean canHarvestBlock(@Nonnull IBlockState state) {
		return (collectionMaterial != null && state.getMaterial() == collectionMaterial) || super.canHarvestBlock(state);
	}

	@Override
	public float getAttackDamage() { return ((IItemSwordMixin) this).getEntityDamage(); }

	@Override
	public float getDestroySpeed(@Nonnull ItemStack stack, @Nonnull IBlockState state) {
		if (state.getMaterial() == collectionMaterial) {
			return speedCollectionMaterial;
		}
		else if (effectiveBlocks.contains(state.getBlock())) {
			for (String type : getToolClasses(stack)) {
				if (state.getBlock().isToolEffective(type, state)) {
					return efficiency;
				}
			}
			return efficiency;
		}
		return super.getDestroySpeed(stack, state);
	}

	@Override
	public boolean getIsRepairable(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
		ItemStack mat = repairItemStack.isEmpty() ? ((IItemSwordMixin) this).getMaterial().getRepairItemStack() : repairItemStack;
        return !mat.isEmpty() && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false);
    }

	@Override
	public @Nonnull Multimap<String, AttributeModifier> getItemAttributeModifiers(@Nonnull EntityEquipmentSlot equipmentSlot) {
		return equipmentSlot == EntityEquipmentSlot.MAINHAND ? defaultModifiers : HashMultimap.create();
	}

	@Override
	public int getItemEnchantability() {
		if (enchantability > 0) {
			return enchantability;
		}
		return super.getItemEnchantability();
	}

	@Override
	public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
		if (showInCreative() && (tab == CustomTabs.ITEMS || tab == CreativeTabs.SEARCH)) {
			items.add(new ItemStack(this));
			if (tab == CustomTabs.ITEMS) { Util.instance.sort(items); }
		}
	}

	@SideOnly(Side.CLIENT)
	public boolean isFull3D() { return bFull3D; }

	@Override
	public String getCustomName() {
		return nbtData.getString("RegistryName");
	}

	@Override
	public INbt getCustomNbt() {
		return Objects.requireNonNull(NpcAPI.Instance()).getINbt(nbtData);
	}

	@Override
	public int getElementType() {
		if (nbtData.hasKey("ItemType", 1)) { return nbtData.getByte("ItemType"); }
		return 1;
	}

	@Override
	public boolean showInCreative() {
		return !nbtData.hasKey("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative");
	}

}
