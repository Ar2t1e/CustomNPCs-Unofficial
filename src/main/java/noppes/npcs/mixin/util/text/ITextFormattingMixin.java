package noppes.npcs.mixin.util.text;

import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TextFormatting.class, priority = 502)
public interface ITextFormattingMixin {

    @Accessor char getFormattingCode();

}
