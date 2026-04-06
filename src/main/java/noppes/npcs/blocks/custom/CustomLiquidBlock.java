package noppes.npcs.blocks.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.*;
import noppes.npcs.api.ICustomElement;
import noppes.npcs.api.INbt;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.fluids.CustomFluid;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Supplier;

// WaterFluid or LavaFluid
public class CustomLiquidBlock extends LiquidBlock implements ICustomElement {

    public static final HashMap<String, GameRules.Key<GameRules.BooleanValue>> gameRules = new HashMap<>();
    protected final @Nonnull CompoundTag nbtData;
    public final @Nonnull ResourceLocation name;

    public CustomLiquidBlock(@Nonnull ResourceLocation nameIn, @Nonnull Supplier<? extends CustomFluid> supplier, @Nonnull BlockBehaviour.Properties property, @Nonnull CompoundTag nbtBlock) {
        super(supplier, property);
        nbtData = nbtBlock;
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
