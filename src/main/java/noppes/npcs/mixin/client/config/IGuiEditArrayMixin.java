package noppes.npcs.mixin.client.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiEditArray;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = GuiEditArray.class, remap = false, priority = 502)
public interface IGuiEditArrayMixin {

    @Accessor GuiScreen getParentScreen();

    @Accessor int getSlotIndex();

    @Accessor boolean getEnabled();

}
