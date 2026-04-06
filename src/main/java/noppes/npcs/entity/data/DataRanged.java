package noppes.npcs.entity.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.api.entity.data.INPCRanged;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.util.NoppesStringUtils;
import noppes.npcs.util.ValueUtil;

public class DataRanged implements INPCRanged {

   private final EntityNPCInterface npc;
   private float rangedRange = 15.0f;
   private int burstCount = 1;
   private int pDamage = 4;
   private int pSpeed = 10;
   private int pImpact = 0;
   private int pSize = 5;
   private int pArea = 0;
   private int pTrail = 0;
   private int minDelay = 20;
   private int maxDelay = 40;
   private int fireRate = 5;
   private int shotCount = 1;
   private int accuracy = 60;
   private int meleeDistance = 0;
   private int canFireIndirect = 0;
   private int pEffect = 0;
   private int pDur = 5;
   private int pEffAmp = 0;
   private boolean pRender3D = true;
   private boolean pSpin = false;
   private boolean pStick = false;
   private boolean pPhysics = true;
   private boolean pXlr8 = false;
   private boolean pGlows = false;
   private boolean aimWhileShooting = false;
   private String fireSound = "minecraft:entity.arrow.shoot";
   private String hitSound = "minecraft:entity.arrow.hit";
   private String groundSound = "minecraft:block.stone.break";

   public DataRanged(EntityNPCInterface npc) {
      this.npc = npc;
   }

   public void load(CompoundTag compound) {
      this.pDamage = compound.getInt("pDamage");
      this.pSpeed = compound.getInt("pSpeed");
      this.burstCount = compound.getInt("BurstCount");
      this.pImpact = compound.getInt("pImpact");
      this.pSize = compound.getInt("pSize");
      this.pArea = compound.getInt("pArea");
      this.pTrail = compound.getInt("pTrail");
      this.fireRate = compound.getInt("FireRate");
      this.minDelay = ValueUtil.correctInt(compound.getInt("minDelay"), 1, 9999);
      this.maxDelay = ValueUtil.correctInt(compound.getInt("maxDelay"), 1, 9999);
      this.shotCount = ValueUtil.correctInt(compound.getInt("ShotCount"), 1, 10);
      this.accuracy = compound.getInt("Accuracy");
      this.pRender3D = compound.getBoolean("pRender3D");
      this.pSpin = compound.getBoolean("pSpin");
      this.pStick = compound.getBoolean("pStick");
      this.pPhysics = compound.getBoolean("pPhysics");
      this.pXlr8 = compound.getBoolean("pXlr8");
      this.pGlows = compound.getBoolean("pGlows");
      this.aimWhileShooting = compound.getBoolean("AimWhileShooting");
      this.pEffect = compound.getInt("pEffect");
      this.pDur = compound.getInt("pDur");
      this.pEffAmp = compound.getInt("pEffAmp");
      this.fireSound = compound.getString("FiringSound");
      this.hitSound = compound.getString("HitSound");
      this.groundSound = compound.getString("GroundSound");
      this.canFireIndirect = compound.getInt("FireIndirect");
      this.meleeDistance = compound.getInt("DistanceToMelee");
      // New from Unofficial (BetaZavr)
      if (compound.contains("MaxFiringRange", 3)) { rangedRange = compound.getInt("MaxFiringRange"); }
      else { rangedRange = compound.getFloat("MaxFiringRange"); }
   }

   public CompoundTag save(CompoundTag compound) {
      compound.putFloat("MaxFiringRange", this.rangedRange);
      compound.putInt("BurstCount", this.burstCount);
      compound.putInt("pSpeed", this.pSpeed);
      compound.putInt("pDamage", this.pDamage);
      compound.putInt("pImpact", this.pImpact);
      compound.putInt("pSize", this.pSize);
      compound.putInt("pArea", this.pArea);
      compound.putInt("pTrail", this.pTrail);
      compound.putInt("FireRate", this.fireRate);
      compound.putInt("minDelay", this.minDelay);
      compound.putInt("maxDelay", this.maxDelay);
      compound.putInt("ShotCount", this.shotCount);
      compound.putInt("Accuracy", this.accuracy);
      compound.putInt("pEffect", this.pEffect);
      compound.putInt("pDur", this.pDur);
      compound.putInt("pEffAmp", this.pEffAmp);
      compound.putInt("FireIndirect", this.canFireIndirect);
      compound.putInt("DistanceToMelee", this.meleeDistance);
      compound.putBoolean("pRender3D", this.pRender3D);
      compound.putBoolean("pSpin", this.pSpin);
      compound.putBoolean("pStick", this.pStick);
      compound.putBoolean("pPhysics", this.pPhysics);
      compound.putBoolean("pXlr8", this.pXlr8);
      compound.putBoolean("pGlows", this.pGlows);
      compound.putBoolean("AimWhileShooting", this.aimWhileShooting);
      compound.putString("FiringSound", this.fireSound);
      compound.putString("HitSound", this.hitSound);
      compound.putString("GroundSound", this.groundSound);
      return compound;
   }

   public int getStrength() {
      return this.pDamage;
   }

   public void setStrength(int strength) {
      this.pDamage = strength;
   }

   public int getSpeed() {
      return this.pSpeed;
   }

   public void setSpeed(int speed) {
      this.pSpeed = ValueUtil.correctInt(speed, 0, 100);
   }

   public int getKnockback() {
      return this.pImpact;
   }

   public void setKnockback(int punch) {
      this.pImpact = punch;
   }

   public int getSize() {
      return this.pSize;
   }

   public void setSize(int size) {
      this.pSize = size;
   }

   public boolean getRender3D() {
      return this.pRender3D;
   }

   public void setRender3D(boolean render3d) {
      this.pRender3D = render3d;
   }

   public boolean getSpins() {
      return this.pSpin;
   }

   public void setSpins(boolean spins) {
      this.pSpin = spins;
   }

   public boolean getSticks() {
      return this.pStick;
   }

   public void setSticks(boolean sticks) {
      this.pStick = sticks;
   }

   public boolean getHasGravity() {
      return this.pPhysics;
   }

   public void setHasGravity(boolean hasGravity) {
      this.pPhysics = hasGravity;
   }

   public boolean getAccelerate() {
      return this.pXlr8;
   }

   public void setAccelerate(boolean accelerate) {
      this.pXlr8 = accelerate;
   }

   public int getExplodeSize() {
      return this.pArea;
   }

   public void setExplodeSize(int size) {
      this.pArea = size;
   }

   public int getEffectType() {
      return this.pEffect;
   }

   public int getEffectTime() {
      return this.pDur;
   }

   public int getEffectStrength() {
      return this.pEffAmp;
   }

   public void setEffect(int type, int strength, int time) {
      this.pEffect = type;
      this.pDur = time;
      this.pEffAmp = strength;
   }

   public boolean getGlows() {
      return this.pGlows;
   }

   public void setGlows(boolean glows) {
      this.pGlows = glows;
   }

   public int getParticle() {
      return this.pTrail;
   }

   public void setParticle(int type) {
      this.pTrail = type;
   }

   public int getAccuracy() {
      return this.accuracy;
   }

   public void setAccuracy(int accuracy) {
      this.accuracy = ValueUtil.correctInt(accuracy, 1, 100);
   }

   public float getRange() {
      return this.rangedRange;
   }

   public void setRange(float range) { rangedRange = ValueUtil.correctFloat(range, 1.0f, 100.0f); }

   public int getDelayMin() {
      return this.minDelay;
   }

   public int getDelayMax() {
      return this.maxDelay;
   }

   public int getDelayRNG() {
      int delay = this.minDelay;
      if (this.maxDelay - this.minDelay > 0) {
         int var10002 = this.maxDelay - this.minDelay;
         delay += this.npc.level().random.nextInt(var10002);
      }

      return delay;
   }

   public void setDelay(int min, int max) {
      min = Math.min(min, max);
      this.minDelay = min;
      this.maxDelay = max;
   }

   public int getBurst() {
      return this.burstCount;
   }

   public void setBurst(int count) {
      this.burstCount = count;
   }

   public int getBurstDelay() {
      return this.fireRate;
   }

   public void setBurstDelay(int delay) {
      this.fireRate = delay;
   }

   public String getSound(int type) {
      String sound = null;
      if (type == 0) {
         sound = this.fireSound;
      }

      if (type == 1) {
         sound = this.hitSound;
      }

      if (type == 2) {
         sound = this.groundSound;
      }

      return sound != null && !sound.isEmpty() ? NoppesStringUtils.cleanResource(sound) : null;
   }

   public SoundEvent getSoundEvent(int type) {
      String sound = this.getSound(type);
      if (sound == null) {
         return null;
      } else {
         ResourceLocation res = ResourceLocation.tryParse(sound);
         SoundEvent ev = ForgeRegistries.SOUND_EVENTS.getValue(res);
         return ev != null ? ev : SoundEvent.createVariableRangeEvent(res != null ? res : new ResourceLocation("minecraft", sound));
      }
   }

   public void setSound(int type, String sound) {
      if (sound == null) {
         sound = "";
      }

      if (type == 0) {
         this.fireSound = NoppesStringUtils.cleanResource(sound);
      }

      if (type == 1) {
         this.hitSound = NoppesStringUtils.cleanResource(sound);
      }

      if (type == 2) {
         this.groundSound = NoppesStringUtils.cleanResource(sound);
      }

      this.npc.updateClient = true;
   }

   public int getShotCount() {
      return this.shotCount;
   }

   public void setShotCount(int count) {
      this.shotCount = count;
   }

   public boolean getHasAimAnimation() {
      return this.aimWhileShooting;
   }

   public void setHasAimAnimation(boolean aim) {
      this.aimWhileShooting = aim;
   }

   public int getFireType() {
      return this.canFireIndirect;
   }

   public void setFireType(int type) {
      this.canFireIndirect = type;
   }

   public int getMeleeRange() {
      return this.meleeDistance;
   }

   public void setMeleeRange(int range) {
      this.meleeDistance = range;
      this.npc.updateAI = true;
   }
}
