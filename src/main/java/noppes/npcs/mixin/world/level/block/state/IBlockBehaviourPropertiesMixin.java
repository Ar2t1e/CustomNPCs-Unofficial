package noppes.npcs.mixin.world.level.block.state;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BlockBehaviour.Properties.class, priority = 502)
public interface IBlockBehaviourPropertiesMixin {

    @Accessor void setDrops(ResourceLocation newResourceLocation);

}
