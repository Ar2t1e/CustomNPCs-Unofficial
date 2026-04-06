package noppes.npcs.items.custom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomTabs;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.util.Util;

import java.util.Objects;

public class CustomBow extends ItemBow implements ICustomElement {

	protected NBTTagCompound nbtData;
	protected ItemStack repairItemStack;
	protected final Item.ToolMaterial material;
	protected final int enchantability;

	protected final ItemStack itemArrow;
	protected final boolean isFlame;
	protected final float critChance;
	protected final double attackDamage;
	protected final float speed;

	public CustomBow(NBTTagCompound nbtItem) {
		super();
		nbtData = nbtItem;
		setRegistryName(CustomNpcs.MODID, "custom_" + nbtItem.getString("RegistryName"));
		setUnlocalizedName("custom_" + nbtItem.getString("RegistryName"));

		itemArrow = nbtItem.hasKey("Bullet", 10) ? new ItemStack(nbtItem.getCompoundTag("Bullet")) : ItemStack.EMPTY;
		isFlame = nbtItem.getBoolean("SetFlame");
		if (nbtItem.hasKey("CritChance", 5)) { critChance = nbtItem.getFloat("CritChance"); }
		else { critChance = 0.0f; }
		material = CustomItem.getMaterialTool(nbtItem);

		if (nbtItem.getInteger("MaxStackDamage") > 1) { setMaxDamage(nbtItem.getInteger("MaxStackDamage")); }
		if (nbtItem.hasKey("EntityDamage", 6)) { attackDamage = nbtItem.getDouble("EntityDamage"); }
		else { attackDamage = 2.0d; }
		if (nbtItem.hasKey("DrawstringSpeed", 5)) { speed = nbtItem.getFloat("DrawstringSpeed"); }
		else { speed = 30.0f; }
		if (nbtItem.hasKey("RepairItem", 10)) { repairItemStack = new ItemStack(nbtItem.getCompoundTag("RepairItem")); }
		else { repairItemStack = material.getRepairItemStack(); }
		if (nbtItem.hasKey("Enchantability", 3)) { enchantability = nbtItem.getInteger("Enchantability"); }
		else { enchantability = 1; }
		if (nbtItem.hasKey("IsFull3D", 1) && nbtItem.getBoolean("IsFull3D")) { setFull3D(); }
		setCreativeTab(CustomTabs.ITEMS);

		addPropertyOverride(new ResourceLocation("pull"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(@Nonnull ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				float f = 0.0f;
				if (entityIn != null) {
					f = (!(entityIn.getActiveItemStack().getItem() instanceof ItemBow)) ? 0.0F : (float) (stack.getMaxItemUseDuration() - entityIn.getItemInUseCount()) / ((CustomBow) stack.getItem()).speed;
				}
				return f;
			}
		});
	}

	@Override
	protected @Nonnull ItemStack findAmmo(@Nonnull EntityPlayer player) {
		if (isArrow(player.getHeldItem(EnumHand.OFF_HAND))) {
			return player.getHeldItem(EnumHand.OFF_HAND);
		} else if (isArrow(player.getHeldItem(EnumHand.MAIN_HAND))) {
			return player.getHeldItem(EnumHand.MAIN_HAND);
		} else {
			for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
				ItemStack itemstack = player.inventory.getStackInSlot(i);
				if (isArrow(itemstack)) {
					return itemstack;
				}
			}
			return ItemStack.EMPTY;
		}
	}

	@Override
	public boolean getIsRepairable(@Nonnull ItemStack toRepair, @Nonnull ItemStack repair) {
		ItemStack mat = repairItemStack;
		if (repairItemStack.isEmpty()) {
			mat = material.getRepairItemStack();
		}
		if (!mat.isEmpty() && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false)) {
			return true;
		}
		return super.getIsRepairable(toRepair, repair);
	}

	@Override
	public int getItemEnchantability() {
		if (enchantability > 0) { return enchantability; }
		return super.getItemEnchantability();
	}

	@Override
	public void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> items) {
		if (showInCreative() && (tab == CustomTabs.ITEMS || tab == CreativeTabs.SEARCH)) {
			items.add(new ItemStack(this));
			if (tab == CustomTabs.ITEMS) { Util.instance.sort(items); }
		}
	}

	@Override
	protected boolean isArrow(@Nonnull ItemStack stack) {
		if (itemArrow != null && !itemArrow.isEmpty()) { return stack.isItemEqualIgnoreDurability(itemArrow); }
		return stack.getItem() instanceof ItemArrow;
	}

	@Override
	public void onPlayerStoppedUsing(@Nonnull ItemStack bowStack, @Nonnull World worldIn, @Nonnull EntityLivingBase entityIn, int timeLeft) {
		if (entityIn instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) entityIn;
			boolean flag = entityplayer.capabilities.isCreativeMode
					|| EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bowStack) > 0;
			ItemStack itemstack = findAmmo(entityplayer);
			int i = getMaxItemUseDuration(bowStack) - timeLeft;
			i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(bowStack, worldIn, entityplayer, i, !itemstack.isEmpty() || flag);
			if (i < 0 || (itemstack.isEmpty() && !flag)) { return; }
			if (itemstack.isEmpty()) { itemstack = new ItemStack(Items.ARROW); }
			float f = getArrowVelocity(i);
			if ((double) f < 0.1D) { return; }
			boolean flag1 = entityplayer.capabilities.isCreativeMode || (itemstack.getItem() instanceof ItemArrow
					&& ((ItemArrow) itemstack.getItem()).isInfinite(itemstack, bowStack, entityplayer));
			if (!worldIn.isRemote) {
				ItemArrow itemarrow = (ItemArrow) (itemstack.getItem() instanceof ItemArrow ? itemstack.getItem() : Items.ARROW);
				EntityArrow abstractarrow = itemarrow.createArrow(worldIn, itemstack, entityplayer);
				abstractarrow.shoot(entityplayer, entityplayer.rotationPitch, entityplayer.rotationYaw, 0.0F, f * 3.0F, 1.0F);
				// crit
				if (f == 1.0F) { abstractarrow.setIsCritical(!(critChance > 0.0f) || !(critChance <= 1.0f) || Item.itemRand.nextFloat() < critChance); }
				// ench power
				int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bowStack);
				double damage = (attackDamage > 0.0d ? attackDamage : abstractarrow.getDamage()) * (i > 40 ? 1.0d : (double) i / 40.0d);
				abstractarrow.setDamage(damage);
				if (j > 0) { abstractarrow.setDamage(damage + (double) j * 0.5D + 0.5D); }
				// ench punch
				int k = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bowStack);
				if (k > 0) { abstractarrow.setKnockbackStrength(k); }
				// ench flame
				if (isFlame || EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, bowStack) > 0) { abstractarrow.setFire(100); }
				bowStack.damageItem(1, entityplayer);
				if (flag1 || entityplayer.capabilities.isCreativeMode
						&& (itemstack.getItem() == Items.SPECTRAL_ARROW || itemstack.getItem() == Items.TIPPED_ARROW)) {
					abstractarrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
				}
				worldIn.spawnEntity(abstractarrow);
			}
			worldIn.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ,
					SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F,
					1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
			if (!flag1 && !entityplayer.capabilities.isCreativeMode) {
				itemstack.shrink(1);
				if (itemstack.isEmpty()) { entityplayer.inventory.deleteStack(itemstack); }
			}
			entityplayer.addStat(Objects.requireNonNull(StatList.getObjectUseStats(this)));
		}
	}

	@Override
	public String getCustomName() { return nbtData.getString("RegistryName"); }

	@Override
	public INbt getCustomNbt() { return Objects.requireNonNull(NpcAPI.Instance()).getINbt(nbtData); }

	@Override
	public int getElementType() {
		if (nbtData != null && nbtData.hasKey("ItemType", 1)) { return nbtData.getByte("ItemType"); }
		return 5;
	}

	@Override
	public boolean showInCreative() {
		return !nbtData.hasKey("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative");
	}

}
