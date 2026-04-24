package noppes.npcs.mixin.client.gui.components;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.BiFunction;

@Mixin(value = EditBox.class, priority = 502)
public interface IEditBoxMixin {

    @Accessor boolean getBordered();

    @Accessor boolean getIsEditable();

    @Accessor int getCursorPos();

    @Accessor int getTextColor();

    @Accessor int getTextColorUneditable();

    @Accessor int getDisplayPos();

    @Accessor void setDisplayPos(int newDisplayPos);

    @Accessor int getHighlightPos();

    @Accessor("highlightPos") void setHighLPos(int newHighlightPos);

    @Accessor int getFrame();

    @Accessor Font getFont();

    @Accessor BiFunction<String, Integer, FormattedCharSequence> getFormatter();

    @Accessor Component getHint();

    @Accessor String getSuggestion();

    @Invoker int invokeGetMaxLength();

}
