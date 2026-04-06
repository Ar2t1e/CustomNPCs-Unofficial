package noppes.npcs.api.wrapper;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NpcDamageSource;
import noppes.npcs.api.IEntityDamageSource;
import noppes.npcs.api.IPos;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.entity.EntityNPCInterface;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class NpcEntityDamageSource extends DamageSource implements IEntityDamageSource {

    private static final Map<String, Holder<DamageType>> cashDamages = new HashMap<>();

    private Holder<DamageType> type;
    @Nullable
    private Entity causingEntity;
    @Nullable
    private Entity directEntity;
    @Nullable
    private Vec3 damageSourcePosition;

    public String deadMessage = "";
    public final Level level;

    public NpcEntityDamageSource(@Nonnull Holder<DamageType> damageTypeHolder, @Nonnull Entity entity) {
        super(damageTypeHolder, entity);
        type = damageTypeHolder;
        causingEntity = entity;
        directEntity = entity;
        level = entity.level();
        damageSourcePosition = null;
    }

    public static @Nonnull NpcEntityDamageSource create(String name, Entity source) {
        return new NpcEntityDamageSource(cashDamages.getOrDefault(name,
                source.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(
                        ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CustomNpcs.MODID, name)))), source);
    }

    @Nullable
    public Entity getDirectEntity() { return this.directEntity; }

    @Nullable
    public Entity getEntity() { return this.causingEntity; }

    public @NotNull Component getLocalizedDeathMessage(@NotNull LivingEntity entity) {
        String s = "death.attack." + (!deadMessage.isEmpty() ? deadMessage : type().msgId());
        if (causingEntity == null && directEntity == null) {
            LivingEntity livingEntity1 = entity.getKillCredit();
            String s1 = s + ".player";
            return livingEntity1 != null ? Component.translatable(s1, entity.getDisplayName(), livingEntity1.getDisplayName()) : Component.translatable(s, entity.getDisplayName());
        } else {
            Component component = causingEntity == null ? directEntity.getDisplayName() : causingEntity.getDisplayName();
            ItemStack itemstack = causingEntity instanceof LivingEntity livingentity ? livingentity.getMainHandItem() : ItemStack.EMPTY;
            return !itemstack.isEmpty() && itemstack.hasCustomHoverName() ? Component.translatable(s + ".item", entity.getDisplayName(), component, itemstack.getDisplayName()) : Component.translatable(s, entity.getDisplayName(), component);
        }
    }

    public @NotNull String getMsgId() { return type().msgId(); }

    public boolean scalesWithDifficulty() {
        return switch (this.type().scaling()) {
            case NEVER -> false;
            case WHEN_CAUSED_BY_LIVING_NON_PLAYER ->
                    this.causingEntity instanceof LivingEntity && !(this.causingEntity instanceof Player);
            case ALWAYS -> true;
            default -> throw new IncompatibleClassChangeError();
        };
    }

    public boolean isCreativePlayer() {
        return getEntity() instanceof Player player && player.getAbilities().instabuild;
    }

    @Nullable
    public Vec3 getSourcePosition() {
        if (damageSourcePosition != null) { return damageSourcePosition; }
        return directEntity != null ? directEntity.position() : null;
    }

    @Nullable
    public Vec3 sourcePositionRaw() { return damageSourcePosition; }

    public boolean is(@NotNull TagKey<DamageType> damageTypeKey) { return type.is(damageTypeKey); }

    public boolean is(@NotNull ResourceKey<DamageType> damageTypeHolder) { return type.is(damageTypeHolder); }

    public @NotNull DamageType type() { return type.value(); }

    public @NotNull Holder<DamageType> typeHolder() { return type; }

    public void setSourcePosition(IPos pos) { damageSourcePosition = pos == null ? null : pos.getMCVec3(); }

    @Override
    public String getDeadMessage() { return deadMessage; }

    @Override
    public IEntity<?> getIImmediateSource() {
        if (directEntity instanceof EntityNPCInterface npc) { return npc.wrappedNPC; }
        return WrapperEntityData.get(directEntity);
    }

    @Override
    public IEntity<?> getITrueSource() {
        if (causingEntity instanceof EntityNPCInterface npc) { return npc.wrappedNPC; }
        return WrapperEntityData.get(causingEntity);
    }

    @Override
    public String getType() { return type().msgId(); }

    @Override
    public void setDeadMessage(String message) {
        if (message == null) { message = ""; }
        deadMessage = message;
    }

    @Override
    public void setImmediateSource(IEntity<?> entity) {
        if (entity == null) { return; }
        directEntity = entity.getMCEntity();
    }

    @Override
    public void setTrueSource(IEntity<?> entity) {
        if (entity == null) { return; }
        causingEntity = entity.getMCEntity();
    }

    @Override
    public void setType(String damageType) {
        if (damageType == null || damageType.isEmpty()) { damageType = "npc"; }
        ResourceKey<DamageType> resourceKey = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(CustomNpcs.MODID, damageType));
        type = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(resourceKey);
    }

}
