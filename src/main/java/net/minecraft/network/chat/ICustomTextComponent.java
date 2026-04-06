package net.minecraft.network.chat;

import net.minecraft.util.text.ITextComponent;
import noppes.npcs.api.interfaces.IgnoreForAPI;

import javax.annotation.Nonnull;

@IgnoreForAPI
public interface ICustomTextComponent extends ITextComponent {

    @Nonnull Component withColor(int color);

    @Nonnull
    Component append(@Nonnull String text);

    @Nonnull Component append(@Nonnull ITextComponent component);

    @Nonnull String getString();

}
