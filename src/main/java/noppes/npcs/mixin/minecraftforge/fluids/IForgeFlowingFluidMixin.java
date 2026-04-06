package noppes.npcs.mixin.minecraftforge.fluids;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.function.Supplier;

@Mixin(value = ForgeFlowingFluid.class, priority = 502, remap = false)
public interface IForgeFlowingFluidMixin {

    @Accessor Supplier<? extends LiquidBlock> getBlock();

}
