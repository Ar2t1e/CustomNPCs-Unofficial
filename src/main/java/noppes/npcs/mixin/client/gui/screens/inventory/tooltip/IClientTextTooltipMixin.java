package noppes.npcs.mixin.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTextTooltip;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ClientTextTooltip.class, priority = 499)
public interface IClientTextTooltipMixin {

   @Accessor FormattedCharSequence getText();

}
