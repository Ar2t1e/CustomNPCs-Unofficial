package noppes.npcs.roles;

import java.lang.reflect.Field;
import java.util.*;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.containers.NpcMiscInventory;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.entity.data.role.IRoleCompanion;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.constants.EnumCompanionStage;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.companion.CompanionFarmer;
import noppes.npcs.roles.companion.CompanionFoodStats;
import noppes.npcs.roles.companion.CompanionGuard;
import noppes.npcs.roles.companion.CompanionJobInterface;
import noppes.npcs.roles.companion.CompanionTrader;

import javax.annotation.Nonnull;

public class RoleCompanion extends RoleInterface implements IRoleCompanion {

	private static final CompanionJobInterface NONE = new CompanionJobInterface() {
		@Override
		public NBTTagCompound getNBT() { return null; }
		@Override
		public void setNBT(NBTTagCompound compound) { }
		@Override
		public EnumCompanionJobs getType() { return EnumCompanionJobs.NONE; }
	};

	public NpcMiscInventory inventory = new NpcMiscInventory(12);
	public String uuid = "";
	public String ownerName = "";
	public Map<EnumCompanionTalent, Integer> talents = new TreeMap<>();
	public boolean canAge = true;
	public long ticksActive = 0L;
	public EnumCompanionStage stage = EnumCompanionStage.FULLGROWN;
	public EntityPlayer owner = null;
	public int companionID;
	public @Nonnull CompanionJobInterface job = NONE;
	public boolean hasInv = true;
	public boolean defendOwner = true;
	public CompanionFoodStats foodstats = new CompanionFoodStats();
	private int eatingTicks = 20;
	private IItemStack eating = null;
	private int eatingDelay = 0;
	public int currentExp = 0;

	public RoleCompanion(EntityNPCInterface npc) {
		super(npc);
		type = RoleType.COMPANION;
	}

	@Override
	public boolean aiShouldExecute() {
		EntityPlayer prev = owner;
		owner = getOwner();
		if (job.isSelfSufficient()) { return true; }
		if (owner == null && !uuid.isEmpty()) {
			if (npc != null) { npc.isDead = true; }
		}
		else if (prev != owner && owner != null) {
			ownerName = owner.getDisplayNameString();
			PlayerData data = PlayerData.get(owner);
			if (data.companionID != companionID && npc != null) { npc.isDead = true; }
		}
		return owner != null;
	}

	@Override
	public void aiUpdateTask() {
		if (owner != null && !job.isSelfSufficient() && npc != null) { foodstats.onUpdate(npc); }
		if (npc != null && foodstats.getFoodLevel() >= 18) {
			npc.stats.healthRegen = 0;
			npc.stats.combatRegen = 0;
		}
		if (foodstats.needFood() && isSitting()) {
			if (eatingDelay > 0) { --eatingDelay; return; }
			IItemStack prev = eating;
			eating = getFood();
			if (npc != null && prev != null && eating == null) { npc.setRoleData(""); }
			if (prev == null && eating != null) {
				if (npc != null) { npc.setRoleData("eating"); }
				eatingTicks = 20;
			}
			if (isEating()) { doEating(); }
		}
		else if (eating != null && !isSitting()) {
			eating = null;
			eatingDelay = 20;
			if (npc != null) { npc.setRoleData(""); }
		}
		++ticksActive;
		if (canAge && stage != EnumCompanionStage.FULLGROWN) {
			if (stage == EnumCompanionStage.BABY && ticksActive > (long)EnumCompanionStage.CHILD.matureAge) {
				matureTo(EnumCompanionStage.CHILD);
			} else if (stage == EnumCompanionStage.CHILD && ticksActive > (long)EnumCompanionStage.TEEN.matureAge) {
				matureTo(EnumCompanionStage.TEEN);
			} else if (stage == EnumCompanionStage.TEEN && ticksActive > (long)EnumCompanionStage.ADULT.matureAge) {
				matureTo(EnumCompanionStage.ADULT);
			} else if (stage == EnumCompanionStage.ADULT && ticksActive > (long)EnumCompanionStage.FULLGROWN.matureAge) {
				matureTo(EnumCompanionStage.FULLGROWN);
			}
		}
	}

	@Override
	public void clientUpdate() {
		if (npc != null) {
			if (npc.getRoleData().equals("eating")) {
				eating = getFood();
				if (isEating()) { doEating(); }
			}
			else if (eating != null) { eating = null; }
		}
	}

	private void doEating() {
		if (npc != null && eating != null && !eating.isEmpty()) {
			ItemStack eatingStack = eating.getMCItemStack();
			Random rand = npc.getRNG();
			if (npc.world.isRemote) {
				for(int j = 0; j < 2; ++j) {
					Vec3d vec3 = new Vec3d((rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
					vec3.rotateYaw(-npc.rotationPitch * 3.1415927f / 180.0f);
					vec3.rotatePitch(-npc.renderYawOffset * 3.1415927f / 180.0f);
					Vec3d vec31 = new Vec3d((rand.nextFloat() - 0.5) * 0.3, -rand.nextFloat() * 0.6 - 0.3, npc.width / 2.0f + 0.1);
					vec31.rotateYaw(-npc.rotationPitch * 3.1415927f / 180.0f);
					vec31.rotatePitch(-npc.renderYawOffset * 3.1415927f / 180.0f);
					vec31 = vec31.addVector(npc.posX, npc.posY + npc.height + 0.1d, npc.posZ);
					npc.world.spawnParticle(EnumParticleTypes.ITEM_CRACK, vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05, vec3.z,
							Item.getIdFromItem(eatingStack.getItem()), eatingStack.getHasSubtypes() ? eatingStack.getMetadata() : 0);
				}
			}
			else {
				--eatingTicks;
				if (eatingTicks <= 0) {
					if (inventory.decrStackSize(eatingStack, 1)) {
						foodstats.onFoodEaten((ItemFood) eatingStack.getItem(), eatingStack);
						npc.playSound(SoundEvents.ENTITY_PLAYER_BURP, 0.5f, rand.nextFloat() * 0.1f + 0.9f);
					}
					eatingDelay = 20;
					npc.setRoleData("");
					eating = null;
				} else if (eatingTicks > 3 && eatingTicks % 2 == 0) {
					npc.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5F + 0.5F * (float) rand.nextInt(2), (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F);
				}
			}
		}
	}

	public void matureTo(EnumCompanionStage stageIn) {
		stage = stageIn;
		EntityCustomNpc cnpc = (EntityCustomNpc) npc;
		npc.ais.animationType = stage.animation;
		if (stage == EnumCompanionStage.BABY) {
			cnpc.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(0.5f, 0.5f, 0.5f);
			cnpc.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(0.5f, 0.5f, 0.5f);
			cnpc.modelData.getPartConfig(EnumParts.BODY).setScale(0.5f, 0.5f, 0.5f);
			cnpc.modelData.getPartConfig(EnumParts.HEAD).setScale(0.7f, 0.7f, 0.7f);
			npc.ais.onAttack = 1;
			npc.ais.setWalkingSpeed(3);
			if (!talents.containsKey(EnumCompanionTalent.INVENTORY)) {
				talents.put(EnumCompanionTalent.INVENTORY, 0);
			}
		}
		if (stage == EnumCompanionStage.CHILD) {
			cnpc.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(0.6f, 0.6f, 0.6f);
			cnpc.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(0.6f, 0.6f, 0.6f);
			cnpc.modelData.getPartConfig(EnumParts.BODY).setScale(0.6f, 0.6f, 0.6f);
			cnpc.modelData.getPartConfig(EnumParts.HEAD).setScale(0.8f, 0.8f, 0.8f);
			npc.ais.onAttack = 0;
			npc.ais.setWalkingSpeed(4);
			if (!talents.containsKey(EnumCompanionTalent.SWORD)) {
				talents.put(EnumCompanionTalent.SWORD, 0);
			}
		}
		if (stage == EnumCompanionStage.TEEN) {
			cnpc.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(0.8f, 0.8f, 0.8f);
			cnpc.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(0.8f, 0.8f, 0.8f);
			cnpc.modelData.getPartConfig(EnumParts.BODY).setScale(0.8f, 0.8f, 0.8f);
			cnpc.modelData.getPartConfig(EnumParts.HEAD).setScale(0.9f, 0.9f, 0.9f);
			npc.ais.onAttack = 0;
			npc.ais.setWalkingSpeed(5);
			if (!talents.containsKey(EnumCompanionTalent.ARMOR)) {
				talents.put(EnumCompanionTalent.ARMOR, 0);
			}
		}
		if (stage == EnumCompanionStage.ADULT || stage == EnumCompanionStage.FULLGROWN) {
			cnpc.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(1.0f, 1.0f, 1.0f);
			cnpc.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(1.0f, 1.0f, 1.0f);
			cnpc.modelData.getPartConfig(EnumParts.BODY).setScale(1.0f, 1.0f, 1.0f);
			cnpc.modelData.getPartConfig(EnumParts.HEAD).setScale(1.0f, 1.0f, 1.0f);
			npc.ais.onAttack = 0;
			npc.ais.setWalkingSpeed(5);
		}
	}

	@Override
	public NBTTagCompound save(NBTTagCompound compound) {
		super.save(compound);
		compound.setTag("CompanionInventory", inventory.save());
		compound.setString("CompanionOwner", uuid);
		compound.setString("CompanionOwnerName", ownerName);
		compound.setInteger("CompanionID", companionID);
		compound.setInteger("CompanionStage", stage.ordinal());
		compound.setInteger("CompanionExp", currentExp);
		compound.setBoolean("CompanionCanAge", canAge);
		compound.setLong("CompanionAge", ticksActive);
		compound.setBoolean("CompanionHasInv", hasInv);
		compound.setBoolean("CompanionDefendOwner", defendOwner);
		foodstats.save(compound);
		compound.setInteger("CompanionJob", job.getType().ordinal());
		if (job.getType() != EnumCompanionJobs.NONE) { compound.setTag("CompanionJobData", job.getNBT()); }
		NBTTagList list = new NBTTagList();
		for (EnumCompanionTalent talent : talents.keySet()) {
			NBTTagCompound c = new NBTTagCompound();
			c.setInteger("Talent", talent.ordinal());
			c.setInteger("Exp", talents.get(talent));
			list.appendTag(c);
		}
		compound.setTag("CompanionTalents", list);
		return compound;
	}

	@Override
	public void load(NBTTagCompound compound) {
		super.load(compound);
		type = RoleType.COMPANION;
		inventory.load(compound.getCompoundTag("CompanionInventory"));
		uuid = compound.getString("CompanionOwner");
		ownerName = compound.getString("CompanionOwnerName");
		companionID = compound.getInteger("CompanionID");
		stage = EnumCompanionStage.values()[compound.getInteger("CompanionStage")];
		currentExp = compound.getInteger("CompanionExp");
		canAge = compound.getBoolean("CompanionCanAge");
		ticksActive = compound.getLong("CompanionAge");
		hasInv = compound.getBoolean("CompanionHasInv");
		defendOwner = compound.getBoolean("CompanionDefendOwner");
		foodstats.load(compound);
		NBTTagList list = compound.getTagList("CompanionTalents", 10);
		talents.clear();
		for (int i = 0; i < list.tagCount(); ++i) {
			NBTTagCompound c = list.getCompoundTagAt(i);
			EnumCompanionTalent talent = EnumCompanionTalent.values()[c.getInteger("Talent")];
			talents.put(talent, c.getInteger("Exp"));
		}
		setJob(compound.getInteger("CompanionJob"));
		job.setNBT(compound.getCompoundTag("CompanionJobData"));
		setStats();
	}

	private void setJob(int id) {
		EnumCompanionJobs companionJob = EnumCompanionJobs.values()[id];
		if (companionJob == EnumCompanionJobs.SHOP) { job = new CompanionTrader(); }
		else if (companionJob == EnumCompanionJobs.FARMER) { job = new CompanionFarmer(); }
		else if (companionJob == EnumCompanionJobs.GUARD) { job = new CompanionGuard(); }
		else { job = NONE; }
		if (npc != null) { job.npc = npc; }
	}

	@Override
	public void interact(EntityPlayer player) { interact(player, false); }

	public void interact(EntityPlayer player, boolean openGui) {
		if (player != null && job.getType() == EnumCompanionJobs.SHOP) { ((CompanionTrader) job).interact(player); }
		if (player == owner && npc != null && npc.isEntityAlive() && !npc.isAttacking()) {
			if (player instanceof EntityPlayerMP && (player.isSneaking() || openGui)) { openGui((EntityPlayerMP) player); }
			else { setSitting(!isSitting()); }
		}
	}

	public int getTotalLevel() {
		int level = 0;
		for (EnumCompanionTalent talent : talents.keySet()) { level += getTalentLevel(talent); }
		return level;
	}

	public int getMaxExp() { return 500 + getTotalLevel() * 200; }

	public void addExp(int exp) {
		if (canAddExp(exp)) { currentExp += exp; }
	}

	public boolean canAddExp(int exp) {
		int newExp = currentExp + exp;
		return newExp >= 0 && newExp < getMaxExp();
	}

	public void gainExp(int chance) {
		if ((npc != null ? npc.getRNG().nextInt(chance) : new Random().nextInt(chance)) == 0) { addExp(1); }
	}

	private void openGui(EntityPlayerMP player) { NoppesUtilServer.sendOpenGui(player, EnumGuiType.Companion, npc); }

	public EntityPlayer getOwner() {
		if (uuid != null && !uuid.isEmpty()) {
			try {
				UUID id = UUID.fromString(uuid);
				MinecraftServer server = npc != null ? npc.getServer() : null;
				return NoppesUtilServer.getPlayer(server != null ? server : CustomNpcs.Server, id);
			} catch (Exception ignored) { }
		}
		return null;
	}

	public void setOwner(EntityPlayer player) { uuid = player.getUniqueID().toString(); }

	public boolean hasTalent(EnumCompanionTalent talent) { return getTalentLevel(talent) > 0; }

	public int getTalentLevel(EnumCompanionTalent talent) {
		if (!talents.containsKey(talent)) { return 0; }
		int exp = talents.get(talent);
		if (exp >= 5000) { return 5; }
		else if (exp >= 3000) { return 4; }
		else if (exp >= 1700) { return 3; }
		else if (exp >= 1000) { return 2; }
		return exp >= 400 ? 1 : 0;
	}

	public Integer getNextLevel(EnumCompanionTalent talent) {
		if (!talents.containsKey(talent)) { return 0; }
		int exp = talents.get(talent);
		if (exp < 400) { return 400; }
		else if (exp < 1000) { return 700; }
		else if (exp < 1700) { return 1700; }
		return exp < 3000 ? 3000 : 5000;
	}

	public int getExp(EnumCompanionTalent talent) {
		if (talents.containsKey(talent)) { return talents.get(talent); }
		return -1;
	}

	public void setExp(EnumCompanionTalent talent, int exp) { talents.put(talent, exp); }

	public boolean isWeapon(ItemStack item) {
		if (item == null) { return false; }
		return item.getItem() instanceof ItemSword || item.getItem() instanceof ItemBow || item.getItem() == Item.getItemFromBlock(Blocks.COBBLESTONE);
	}

	public boolean canWearWeapon(IItemStack stack) {
		if (stack != null) {
			Item item = stack.getMCItemStack().getItem();
			if (item instanceof ItemSword) { return canWearSword(stack); }
			else if (item instanceof ItemBow) { return getTalentLevel(EnumCompanionTalent.RANGED) > 2; }
			else if (item == Item.getItemFromBlock(Blocks.COBBLESTONE)) { return getTalentLevel(EnumCompanionTalent.RANGED) > 1; }
		}
		return false;
	}

	public boolean canWearArmor(ItemStack item) {
		int level = getTalentLevel(EnumCompanionTalent.ARMOR);
		if (item != null && item.getItem() instanceof ItemArmor && level > 0) {
			if (level >= 5) { return true; }
			ItemArmor armor = (ItemArmor)item.getItem();
			int reduction = 1;
			try {
				Field f;
				try { f = ItemArmor.ArmorMaterial.class.getDeclaredField("field_78048_f"); }
				catch (Exception e) { f = ItemArmor.ArmorMaterial.class.getDeclaredField("maxDamageFactor"); }
				f.setAccessible(true);
				reduction = (int) f.get(armor.getArmorMaterial());
			}
			catch (Exception e) { LogWriter.debug(e.toString()); }
			return reduction <= 5 || reduction <= 7 && level >= 2 || reduction <= 15 && level >= 3 || reduction <= 33 && level == 4;
		}
		return false;
	}

	public boolean canWearSword(IItemStack item) {
		int level = getTalentLevel(EnumCompanionTalent.SWORD);
		return item != null && item.getMCItemStack().getItem() instanceof ItemSword && level > 0
				&& (level >= 5 || getSwordDamage(item) - level < 4.0);
	}

	private double getSwordDamage(IItemStack item) { return item != null ? item.getAttackDamage() : 0.0d; }

	public void setStats() {
		if (npc != null) {
			IItemStack weapon = npc.inventory.getRightHand();
			npc.stats.melee.setStrength((int)(1.0D + getSwordDamage(weapon)));
			npc.stats.healthRegen = 0;
			npc.stats.combatRegen = 0;
			int ranged = getTalentLevel(EnumCompanionTalent.RANGED);
			if (ranged > 0 && weapon != null) {
				Item item = weapon.getMCItemStack().getItem();
				if (item == Item.getItemFromBlock(Blocks.COBBLESTONE)) { npc.inventory.setProjectile(weapon); }
				if (item instanceof ItemBow) {
					npc.inventory.setProjectile(Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(new ItemStack(Items.ARROW)));
				}
			}
			inventory.setNewSize(2 + getTalentLevel(EnumCompanionTalent.INVENTORY) * 2);
		}
	}

	public void setSelfsuficient(boolean bo) {
		if (owner != null && bo != job.isSelfSufficient()) {
			PlayerData data = PlayerData.get(owner);
			if (bo || !data.hasCompanion()) {
				data.setCompanion(bo ? null : npc);
				if (job.getType() == EnumCompanionJobs.GUARD) { ((CompanionGuard) job).isStanding = bo; }
				else if (job.getType() == EnumCompanionJobs.FARMER) { ((CompanionFarmer) job).isStanding = bo; }
			}
		}
	}

	public void setSitting(boolean sit) {
		if (npc != null) {
			if (sit) {
				npc.ais.animationType = 1;
				npc.ais.onAttack = 3;
				npc.ais.setStartPos(new BlockPos(npc));
				npc.getNavigator().clearPath();
				npc.setPositionAndUpdate(npc.getStartXPos(), npc.posY, npc.getStartZPos());
			} else {
				npc.ais.animationType = stage.animation;
				npc.ais.onAttack = 0;
			}
			npc.updateAI = true;
		}
	}

	public boolean isSitting() { return npc != null && npc.ais.animationType == 1; }

	public float getDamageAfterArmorAbsorb(DamageSource source, float damage) {
		if (hasInv && getTalentLevel(EnumCompanionTalent.ARMOR) > 0 && !source.isUnblockable()) {
			damageArmor(damage);
			int i = 25 - getTotalArmorValue();
			float f1 = damage * i;
			damage = f1 / 25.0f;
		}
		return damage;
	}

	private void damageArmor(float damage) {
		damage /= 4.0f;
		if (damage < 1.0F) { damage = 1.0F; }
		boolean hasArmor = false;
		if (npc != null) {
			Iterator<Map.Entry<Integer, IItemStack>> ita = npc.inventory.armor.entrySet().iterator();
			while (ita.hasNext()) {
				Map.Entry<Integer, IItemStack> entry = ita.next();
				IItemStack item = entry.getValue();
				if (item != null && item.getMCItemStack().getItem() instanceof ItemArmor) {
					hasArmor = true;
					item.getMCItemStack().damageItem((int)damage, npc);
					if (item.getStackSize() <= 0) { ita.remove(); }
				}
			}
		}
		gainExp(hasArmor ? 4 : 8);
	}

	public int getTotalArmorValue() {
		int armorValue = 0;
		if (npc != null) {
			for (IItemStack armor : npc.inventory.armor.values()) {
				if (armor != null && armor.getMCItemStack().getItem() instanceof ItemArmor) {
					armorValue += ((ItemArmor) armor.getMCItemStack().getItem()).damageReduceAmount;
				}
			}
		}
		return armorValue;
	}

	@Override
	public boolean isFollowing() { return !job.isSelfSufficient() && owner != null && !isSitting(); }

	@Override
	public boolean defendOwner() { return defendOwner && owner != null && stage != EnumCompanionStage.BABY && !job.isSelfSufficient(); }

	public boolean hasOwner() { return !uuid.isEmpty(); }

	public void addMovementStat(double x, double y, double z) {
		long i = Math.round(Math.sqrt(x * x + y * y + z * z) * 100.0D);
		if (npc != null && npc.isAttacking()) { foodstats.addExhaustion(0.04F * (float)i * 0.01F); }
		else { foodstats.addExhaustion(0.02F * (float)i * 0.01F); }
	}

	private IItemStack getFood() {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (!NoppesUtilServer.isItemStackNull(stack) && stack.getItem() instanceof ItemFood) {
				return Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(stack);
			}
		}
		return null;
	}

	public IItemStack getItemInHand() { return eating != null && !eating.isEmpty() ? eating : npc != null ? npc.inventory.getRightHand() : ItemStackWrapper.AIR; }

	public boolean isEating() { return eating != null && !eating.isEmpty(); }

	public boolean hasInv() { return hasInv && (hasTalent(EnumCompanionTalent.INVENTORY) || hasTalent(EnumCompanionTalent.ARMOR) || hasTalent(EnumCompanionTalent.SWORD)); }

	public void attackedEntity(Entity ignoredEntity) {
		if (npc != null) {
			IItemStack weapon = npc.inventory.getRightHand();
			gainExp(weapon == null ? 8 : 4);
			if (weapon != null) {
				weapon.getMCItemStack().damageItem(1, npc);
				if (weapon.getMCItemStack().getCount() <= 0) { npc.inventory.setRightHand(null); }
			}
		}
	}

	public void addTalentExp(EnumCompanionTalent talent, int exp) {
		if (talents.containsKey(talent)) { exp += talents.get(talent); }
		talents.put(talent, exp);
	}

}
