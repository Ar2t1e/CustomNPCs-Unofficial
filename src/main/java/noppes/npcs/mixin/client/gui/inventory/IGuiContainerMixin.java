package noppes.npcs.mixin.client.gui.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiContainer.class, priority = 502)
public interface IGuiContainerMixin {

    @Accessor Slot getClickedSlot();

    @Accessor ItemStack getDraggedStack();

    @Accessor boolean getIsRightMouseClick();

    @Accessor int getDragSplittingLimit();

    @Accessor void setHoveredSlot(Slot newHoveredSlot);

    @Accessor int getDragSplittingRemnant();

    @Accessor void setDragSplittingRemnant(int newDragSplittingRemnant);
}
