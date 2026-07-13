package noppes.npcs.mixin.minecraftforge.client.gui.widget;

import net.minecraftforge.client.gui.widget.ScrollPanel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ScrollPanel.class, priority = 502, remap = false)
public interface IScrollPanelMixin {

    @Accessor void setScrollDistance(float newScrollDistance);

}
