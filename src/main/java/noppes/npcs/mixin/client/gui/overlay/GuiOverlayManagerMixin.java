package noppes.npcs.mixin.client.gui.overlay;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import noppes.npcs.api.event.gui.OverlaysGets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = GuiOverlayManager.class, priority = 498, remap = false)
public class GuiOverlayManagerMixin {

    @Shadow private static ImmutableList<NamedGuiOverlay> OVERLAYS;

    /**
     * @author BetaZavr
     * @reason allows you to define layers for rendering
     * see class net.minecraftforge.client.gui.overlay.VanillaGuiOverlay
     */
    @Overwrite
    public static ImmutableList<NamedGuiOverlay> getOverlays() {
        OverlaysGets event = new OverlaysGets(OVERLAYS);
        if (MinecraftForge.EVENT_BUS.post(event)) { return ImmutableList.of(); }
        return event.getOverlays();
    }

}
