package noppes.npcs.mixin.client.gui;

import net.minecraft.client.gui.GuiButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiButton.class, priority = 502)
public interface IGuiButton {

    @Accessor void setHovered(boolean newHovered);

}
