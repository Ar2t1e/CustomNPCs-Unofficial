package noppes.npcs.roles.data;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.entity.data.role.IJobSpawner;
import noppes.npcs.api.wrapper.NBTWrapper;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.util.ValueUtil;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Optional;

public class JobSpawnerNbtData implements IJobSpawner.IJobSpawnerData {

    protected final @Nonnull  EntityNPCInterface parent;
    protected int count;
    protected CompoundTag compound;
    protected Component title;

    public JobSpawnerNbtData(@Nonnull EntityNPCInterface npc) { parent = npc; }

    @Override
    public Component getTitle() { return title; }

    @Override
    public int getCount() { return count; }

    @Override
    public void setCount(int countIn) { count = ValueUtil.correctInt(countIn, 1, 7); }

    @Override
    public INbt getNbt() { return new NBTWrapper(save()); }

    @Override
    public IEntity<?> getEntity() {
        Optional<Entity> entityO = EntityType.create(compound, parent.level());
        return entityO.map(entity -> Objects.requireNonNull(NpcAPI.Instance()).getIEntity(entity)).orElse(null);
    }

    @Override
    public boolean isValid() {
        return EntityType.create(compound, parent.level()).isPresent();
    }

    @Override
    public void setNbt(INbt nbt) {
        if (nbt != null) { load(nbt.getMCNBT()); }
    }

    public boolean isClientClone() { return compound.getBoolean("ClientClone"); }

    public void load(@Nonnull CompoundTag nbt) {
        compound = nbt;
        Optional<Entity> entityO = EntityType.create(compound, parent.level());
        title = entityO.map(entity -> Component.literal(entity.getName().getString()).withStyle(ChatFormatting.RESET))
                .orElseGet(() -> Component.literal(nbt.getString("id")).withStyle(ChatFormatting.RESET));
    }

    public @Nonnull CompoundTag save() { return compound; }

    @Override
    public String toString() { return "JobSpawnerNbtData{ Name: \"" + getTitle() + "\", isClientClone: " + isClientClone() + "}"; }

}
