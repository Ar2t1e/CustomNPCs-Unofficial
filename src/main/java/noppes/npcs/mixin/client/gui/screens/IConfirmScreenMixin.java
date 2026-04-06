package noppes.npcs.mixin.client.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screens.ConfirmScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ConfirmScreen.class, priority = 502)
public interface IConfirmScreenMixin {

    @Accessor BooleanConsumer getCallback();

}
