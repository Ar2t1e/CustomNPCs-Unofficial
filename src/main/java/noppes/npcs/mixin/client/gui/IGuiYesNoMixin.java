package noppes.npcs.mixin.client.gui;

import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// Used by custom GUI
@Mixin(value = GuiYesNo.class, priority = 502)
public interface IGuiYesNoMixin {

    @Accessor
    GuiYesNoCallback getParentScreen();

    @Accessor
    int getParentButtonClickedId();

}
