package noppes.npcs.mixin.world;

import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = SimpleContainer.class, priority = 502)
public interface ISimpleContainerMixin {

    @Accessor NonNullList<ItemStack> getItems();

    @Accessor int getSize();

    @Mutable @Accessor void setSize(int newSize);

}
