package noppes.npcs.items.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

public class CustomBucketItem extends BucketItem implements ICustomElement {

    protected final @Nonnull CompoundTag nbtData;
    public final @Nonnull ResourceLocation name;

    public CustomBucketItem(@Nonnull ResourceLocation nameIn, @Nonnull Supplier<? extends FlowingFluid> supplier, @Nonnull Properties property, @Nonnull CompoundTag nbtItem) {
        super(supplier, property);
        nbtData = nbtItem;
        name = nameIn;
    }

    @Override
    public String getCustomName() { return nbtData.getString("RegistryName"); }

    @Override
    public INbt getCustomNbt() { return Objects.requireNonNull(NpcAPI.Instance()).getINbt(nbtData); }

    @Override
    public int getElementType() {
        if (nbtData.contains("BlockType", 1)) { return nbtData.getByte("BlockType"); }
        return 1;
    }

    @Override
    public boolean showInCreative() { return !nbtData.contains("ShowInCreative", 1) || nbtData.getBoolean("ShowInCreative"); }
}
