package noppes.npcs.roles;

import java.util.*;
import java.util.Map.Entry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.containers.inventories.NpcMiscInventory;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.constants.EnumCompanionJobs;
import noppes.npcs.constants.EnumCompanionStage;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.mixin.world.item.IArmorMaterialsMixin;
import noppes.npcs.roles.companion.CompanionFarmer;
import noppes.npcs.roles.companion.CompanionFoodStats;
import noppes.npcs.roles.companion.CompanionGuard;
import noppes.npcs.roles.companion.CompanionJobInterface;
import noppes.npcs.roles.companion.CompanionTrader;

import javax.annotation.Nonnull;

public class RoleCompanion extends RoleInterface {

   private static final CompanionJobInterface NONE = new CompanionJobInterface() {
      @Override
      public CompoundTag getNBT() { return null; }
      @Override
      public void setNBT(CompoundTag compound) { }
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
   public Player owner = null;
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
      Player prev = owner;
      owner = getOwner();
      if (job.isSelfSufficient()) { return true; }
      if (owner == null && !uuid.isEmpty()) {
         if (npc != null) { npc.discard(); }
      }
      else if (prev != owner && owner != null) {
         ownerName = owner.getDisplayName().getString();
         PlayerData data = PlayerData.get(owner);
         if (data.companionID != companionID && npc != null) { npc.discard(); }
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
      } else if (eating != null && !isSitting()) {
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
         RandomSource rand = npc.getRandom();
         if (npc.level().isClientSide) {
            for(int j = 0; j < 2; ++j) {
               Vec3 vec3 = new Vec3(((double)rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
               vec3.xRot(-npc.getXRot() * 3.1415927F / 180.0F);
               vec3.yRot(-npc.yBodyRot * 3.1415927F / 180.0F);
               Vec3 vec31 = new Vec3(((double)rand.nextFloat() - 0.5D) * 0.3D, (double)(-rand.nextFloat()) * 0.6D - 0.3D, (double)(npc.getBbWidth() / 2.0F) + 0.1D);
               vec31.xRot(-npc.getXRot() * 3.1415927F / 180.0F);
               vec31.yRot(-npc.yBodyRot * 3.1415927F / 180.0F);
               vec31 = vec31.add(npc.getX(), npc.getY() + (double)npc.getBbHeight() + 0.1D, npc.getZ());
               //String s = "iconcrack_" + Item.getId(eatingStack.getItem());
               npc.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, eatingStack), vec31.x, vec31.y, vec31.z, vec3.x, vec3.y + 0.05D, vec3.z);
            }
         }
         else {
            --eatingTicks;
            if (eatingTicks <= 0) {
               FoodProperties food = eatingStack.getItem().getFoodProperties(eatingStack, npc);
               if (inventory.removeItem(eatingStack, 1) && food != null) {
                  foodstats.onFoodEaten(food, eatingStack);
                  npc.playSound(SoundEvents.PLAYER_BURP, 0.5F, rand.nextFloat() * 0.1F + 0.9F);
               }
               eatingDelay = 20;
               npc.setRoleData("");
               eating = null;
            }
            else if (eatingTicks > 3 && eatingTicks % 2 == 0) {
               npc.playSound(SoundEvents.GENERIC_EAT, 0.5F + 0.5F * (float) rand.nextInt(2), rand.nextFloat() * 0.2F + 1.0F);
            }
         }
      }
   }

   public void matureTo(EnumCompanionStage stageIn) {
      stage = stageIn;
      if (npc == null) { return; }
      EntityCustomNpc npcIn = (EntityCustomNpc) npc;
      npc.ais.animationType = stage.animation;
      if (stage == EnumCompanionStage.BABY) {
         npcIn.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(0.5F, 0.5F, 0.5F);
         npcIn.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(0.5F, 0.5F, 0.5F);
         npcIn.modelData.getPartConfig(EnumParts.BODY).setScale(0.5F, 0.5F, 0.5F);
         npcIn.modelData.getPartConfig(EnumParts.HEAD).setScale(0.7F, 0.7F, 0.7F);
         npc.ais.onAttack = 1;
         npc.ais.setWalkingSpeed(3);
         if (!talents.containsKey(EnumCompanionTalent.INVENTORY)) { talents.put(EnumCompanionTalent.INVENTORY, 0); }
      }
      if (stage == EnumCompanionStage.CHILD) {
         npcIn.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(0.6F, 0.6F, 0.6F);
         npcIn.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(0.6F, 0.6F, 0.6F);
         npcIn.modelData.getPartConfig(EnumParts.BODY).setScale(0.6F, 0.6F, 0.6F);
         npcIn.modelData.getPartConfig(EnumParts.HEAD).setScale(0.8F, 0.8F, 0.8F);
         npc.ais.onAttack = 0;
         npc.ais.setWalkingSpeed(4);
         if (!talents.containsKey(EnumCompanionTalent.SWORD)) { talents.put(EnumCompanionTalent.SWORD, 0); }
      }
      if (stage == EnumCompanionStage.TEEN) {
         npcIn.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(0.8F, 0.8F, 0.8F);
         npcIn.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(0.8F, 0.8F, 0.8F);
         npcIn.modelData.getPartConfig(EnumParts.BODY).setScale(0.8F, 0.8F, 0.8F);
         npcIn.modelData.getPartConfig(EnumParts.HEAD).setScale(0.9F, 0.9F, 0.9F);
         npc.ais.onAttack = 0;
         npc.ais.setWalkingSpeed(5);
         if (!talents.containsKey(EnumCompanionTalent.ARMOR)) { talents.put(EnumCompanionTalent.ARMOR, 0); }
      }
      if (stage == EnumCompanionStage.ADULT || stage == EnumCompanionStage.FULLGROWN) {
         npcIn.modelData.getPartConfig(EnumParts.ARM_LEFT).setScale(1.0F, 1.0F, 1.0F);
         npcIn.modelData.getPartConfig(EnumParts.LEG_LEFT).setScale(1.0F, 1.0F, 1.0F);
         npcIn.modelData.getPartConfig(EnumParts.BODY).setScale(1.0F, 1.0F, 1.0F);
         npcIn.modelData.getPartConfig(EnumParts.HEAD).setScale(1.0F, 1.0F, 1.0F);
         npc.ais.onAttack = 0;
         npc.ais.setWalkingSpeed(5);
      }
   }

   @Override
   public CompoundTag save(CompoundTag compound) {
      super.save(compound);
      compound.put("CompanionInventory", inventory.save());
      compound.putString("CompanionOwner", uuid);
      compound.putString("CompanionOwnerName", ownerName);
      compound.putInt("CompanionID", companionID);
      compound.putInt("CompanionStage", stage.ordinal());
      compound.putInt("CompanionExp", currentExp);
      compound.putBoolean("CompanionCanAge", canAge);
      compound.putLong("CompanionAge", ticksActive);
      compound.putBoolean("CompanionHasInv", hasInv);
      compound.putBoolean("CompanionDefendOwner", defendOwner);
      foodstats.save(compound);
      compound.putInt("CompanionJob", job.getType().ordinal());
      if (job.getType() != EnumCompanionJobs.NONE) { compound.put("CompanionJobData", job.getNBT()); }
      ListTag list = new ListTag();
      for (EnumCompanionTalent talent : talents.keySet()) {
         CompoundTag c = new CompoundTag();
         c.putInt("Talent", talent.ordinal());
         c.putInt("Exp", talents.get(talent));
         list.add(c);
      }
      compound.put("CompanionTalents", list);
      return compound;
   }

   @Override
   public void load(CompoundTag compound) {
      super.load(compound);
      type = RoleType.COMPANION;
      inventory.load(compound.getCompound("CompanionInventory"));
      uuid = compound.getString("CompanionOwner");
      ownerName = compound.getString("CompanionOwnerName");
      companionID = compound.getInt("CompanionID");
      stage = EnumCompanionStage.values()[compound.getInt("CompanionStage")];
      currentExp = compound.getInt("CompanionExp");
      canAge = compound.getBoolean("CompanionCanAge");
      ticksActive = compound.getLong("CompanionAge");
      hasInv = compound.getBoolean("CompanionHasInv");
      defendOwner = compound.getBoolean("CompanionDefendOwner");
      foodstats.load(compound);
      ListTag list = compound.getList("CompanionTalents", 10);
      talents.clear();
      for(int i = 0; i < list.size(); ++i) {
         CompoundTag c = list.getCompound(i);
         EnumCompanionTalent talent = EnumCompanionTalent.values()[c.getInt("Talent")];
         talents.put(talent, c.getInt("Exp"));
      }
      setJob(compound.getInt("CompanionJob"));
      job.setNBT(compound.getCompound("CompanionJobData"));
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
   public void interact(Player player) { interact(player, false); }

   public void interact(Player player, boolean openGui) {
      if (player != null && job.getType() == EnumCompanionJobs.SHOP) { ((CompanionTrader) job).interact(player); }
      if (player == owner && npc != null && npc.isAlive() && !npc.isAttacking()) {
         if (player instanceof ServerPlayer sPlayer && (sPlayer.isCrouching() || openGui)) { openGui(sPlayer); }
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
      if ((npc != null ? npc.getRandom().nextInt(chance) : new Random().nextInt(chance)) == 0) { addExp(1); }
   }

   private void openGui(ServerPlayer player) { NoppesUtilServer.sendOpenGui(player, EnumGuiType.Companion, npc); }

   public Player getOwner() {
      if (uuid != null && !uuid.isEmpty()) {
         try {
            UUID id = UUID.fromString(uuid);
            MinecraftServer server = npc != null ? npc.getServer() : null;
            return NoppesUtilServer.getPlayer(server != null ? server : CustomNpcs.Server, id);
         } catch (Exception ignored) { }
      }
      return null;
   }

   public void setOwner(Player player) { uuid = player.getUUID().toString(); }

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

   public int getExp(EnumCompanionTalent talent) { return talents.getOrDefault(talent, -1); }

   public void setExp(EnumCompanionTalent talent, int exp) { talents.put(talent, exp); }

   public boolean isWeapon(ItemStack item) {
      if (item == null) { return false; }
      return item.getItem() instanceof SwordItem || item.getItem() instanceof BowItem || item.getItem() == Item.byBlock(Blocks.COBBLESTONE);
   }

   public boolean canWearWeapon(IItemStack stack) {
      if (stack != null) {
         Item item = stack.getMCItemStack().getItem();
         if (item instanceof SwordItem) { return canWearSword(stack); }
         else if (item instanceof BowItem) { return getTalentLevel(EnumCompanionTalent.RANGED) > 2; }
         else if (item == Item.byBlock(Blocks.COBBLESTONE)) { return getTalentLevel(EnumCompanionTalent.RANGED) > 1; }
      }
      return false;
   }

   public boolean canWearArmor(ItemStack item) {
      int level = getTalentLevel(EnumCompanionTalent.ARMOR);
      if (item != null && item.getItem() instanceof ArmorItem armor && level > 0) {
         if (level >= 5) { return true; }
         int reduction = 1;
         if (armor.getMaterial() instanceof ArmorMaterials) {
            reduction = ((IArmorMaterialsMixin) armor.getMaterial()).getDurabilityMultiplier();
         }
         return reduction <= 5 || reduction <= 7 && level >= 2 || reduction <= 15 && level >= 3 || reduction <= 33 && level == 4;
      }
      return false;
   }

   public boolean canWearSword(IItemStack item) {
      int level = getTalentLevel(EnumCompanionTalent.SWORD);
      if (item != null && item.getMCItemStack().getItem() instanceof SwordItem && level > 0) {
         if (level >= 5) { return true; }
         return getSwordDamage(item) - (double)level < 4.0D;
      }
      return false;
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
            if (item == Item.byBlock(Blocks.COBBLESTONE)) { npc.inventory.setProjectile(weapon); }
            if (item instanceof BowItem) {
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
            npc.ais.setStartPos(npc.blockPosition());
            npc.getNavigation().stop();
            npc.teleportTo(npc.getStartXPos(), npc.getY(), npc.getStartZPos());
         }
         else {
            npc.ais.animationType = stage.animation;
            npc.ais.onAttack = 0;
         }
         npc.updateAI = true;
      }
   }

   public boolean isSitting() { return npc != null && npc.ais.animationType == 1; }

   public float getDamageAfterArmorAbsorb(DamageSource source, float damage) {
      if (hasInv && getTalentLevel(EnumCompanionTalent.ARMOR) > 0 && !source.is(DamageTypeTags.BYPASSES_SHIELD)) {
         damageArmor(damage);
         int i = 25 - getTotalArmorValue();
         float f1 = damage * (float)i;
         damage = f1 / 25.0F;
      }
      return damage;
   }

   private void damageArmor(float damage) {
      damage /= 4.0F;
      if (damage < 1.0F) { damage = 1.0F; }
      boolean hasArmor = false;
      if (npc != null) {
         Iterator<Entry<Integer, IItemStack>> ita = npc.inventory.armor.entrySet().iterator();
         while(ita.hasNext()) {
            Entry<Integer, IItemStack> entry = ita.next();
            IItemStack item = entry.getValue();
            if (item != null && item.getMCItemStack().getItem() instanceof ArmorItem) {
               hasArmor = true;
               item.getMCItemStack().hurtAndBreak((int)damage, npc, (entity) -> entity.broadcastBreakEvent(EquipmentSlot.byTypeAndIndex(Type.ARMOR, entry.getKey())));
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
            if (armor != null && armor.getMCItemStack().getItem() instanceof ArmorItem) {
               armorValue += ((ArmorItem) armor.getMCItemStack().getItem()).getDefense();
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
      for (int i = 0; i < inventory.getContainerSize(); i++) {
         ItemStack stack = inventory.getItem(i);
         if (!NoppesUtilServer.isItemStackNull(stack) && stack.getItem().getFoodProperties() != null) {
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
            weapon.getMCItemStack().hurtAndBreak(1, npc, (e) -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
            if (weapon.getMCItemStack().getCount() <= 0) { npc.inventory.setRightHand(null); }
         }
      }
   }

   public void addTalentExp(EnumCompanionTalent talent, int exp) {
      if (talents.containsKey(talent)) { exp += talents.get(talent); }
      talents.put(talent, exp);
   }

}
