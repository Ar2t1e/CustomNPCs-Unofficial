package noppes.npcs.mixin.client.gui.screens.inventory;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import noppes.npcs.shared.client.gui.GuiBasicContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AbstractContainerScreen.class, priority = 498)
public class AbstractContainerScreenMixin {

    @Inject(at = {@At("RETURN")}, method = {"findSlot"}, cancellable = true)
    private void npcs$findSlot(double mouseX, double mouseY, CallbackInfoReturnable<Slot> cir) {
        if ((Object) this instanceof GuiBasicContainer<?> modGui) {
            cir.setReturnValue(modGui.findSlot(mouseX, mouseY, cir.getReturnValue()));
        }
    }

}
