package noppes.npcs.client.controllers;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import noppes.npcs.api.overlay.IOverlay;
import noppes.npcs.client.overlay.Overlay;

import java.util.ArrayList;
import java.util.List;

public class OverlayController {

   private static final OverlayController instance = new OverlayController();
   private final Int2ObjectOpenHashMap<Overlay> overlays = new Int2ObjectOpenHashMap<>();

   public static OverlayController getInstance() { return instance; }

   public void addOverlay(IOverlay overlay) { overlays.put(overlay.getId(), new Overlay(overlay)); }

   public void removeOverlay(int id) { overlays.remove(id); }

   public void clear() { overlays.clear(); }

   public Overlay get(int id) { return overlays.get(id); }

   public List<Overlay> getOverlays() { return new ArrayList<>(overlays.values()); }

}
