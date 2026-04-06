package noppes.npcs.api.event.gui;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import noppes.npcs.api.interfaces.IgnoreForAPI;
import noppes.npcs.api.interfaces.ParamName;

@Cancelable
@IgnoreForAPI
public class OverlaysGets extends Event {

    private ImmutableList<NamedGuiOverlay> arrayList;

    public OverlaysGets(@ParamName("overlays") ImmutableList<NamedGuiOverlay> overlays) { arrayList = overlays; }

    public ImmutableList<NamedGuiOverlay> getOverlays() { return arrayList; }

    public void setOverlays(@ParamName("newOverlays") ImmutableList<NamedGuiOverlay> newOverlays) {
        if (newOverlays == null) { newOverlays = ImmutableList.of(); }
        arrayList = newOverlays;
    }

}
