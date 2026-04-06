package noppes.npcs.api.wrapper;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import noppes.npcs.api.IDamageSource;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;

import java.util.Objects;

public class DamageSourceWrapper implements IDamageSource {

   private final DamageSource source;

   public DamageSourceWrapper(DamageSource source) { this.source = source; }

   public String getType() { return this.source.getMsgId(); }

   public boolean isUnblockable() { return this.source.is(DamageTypeTags.BYPASSES_ARMOR); }

   public boolean isProjectile() { return this.source.is(DamageTypeTags.IS_PROJECTILE); }

   public DamageSource getMCDamageSource() { return this.source; }

   public IEntity<?> getTrueSource() { return Objects.requireNonNull(NpcAPI.Instance()).getIEntity(this.source.getEntity()); }

   public IEntity<?> getImmediateSource() { return Objects.requireNonNull(NpcAPI.Instance()).getIEntity(this.source.getDirectEntity()); }

}
