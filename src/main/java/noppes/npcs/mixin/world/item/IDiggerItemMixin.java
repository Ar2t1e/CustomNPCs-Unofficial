package noppes.npcs.mixin.world.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = DiggerItem.class, priority = 502)
public interface IDiggerItemMixin {

    @Accessor("blocks")
    TagKey<Block> blocks();

}
