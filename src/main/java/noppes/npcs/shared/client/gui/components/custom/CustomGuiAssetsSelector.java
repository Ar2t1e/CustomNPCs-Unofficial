package noppes.npcs.shared.client.gui.components.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.gui.ICustomGuiComponent;
import noppes.npcs.api.wrapper.gui.CustomGuiAssetsSelectorWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiLabelWrapper;
import noppes.npcs.client.gui.custom.GuiCustom;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCustomGuiButton;
import noppes.npcs.packets.server.SPacketCustomGuiTextUpdate;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiLabel;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.custom.IComponentCustomGui;
import noppes.npcs.shared.client.util.AssetsFinder;
import noppes.npcs.util.Util;

import javax.annotation.Nonnull;

public class CustomGuiAssetsSelector
        extends GuiLabel
        implements IComponentCustomGui, ICustomScrollListener {

   protected static final HashMap<String, List<ResourceLocation>> domains = new HashMap<>();
   protected static final HashMap<String, ResourceLocation> textures = new HashMap<>();
   protected final String up = "..<" + Component.translatable("gui.up") + ">..";
   protected final CustomGuiAssetsSelectorWrapper component;
   protected final GuiCustomScrollNop folders;
   protected final GuiCustomScrollNop items;
   protected final CustomGuiLabel label;
   protected String location = "";
   protected String path = "";
   protected String selectedDomain;
   public ResourceLocation prevResource = null;
   public ResourceLocation selectedResource = null;

   public CustomGuiAssetsSelector(GuiCustom parent, CustomGuiAssetsSelectorWrapper componentIn) {
      super(parent, componentIn.getId(), Component.empty(), componentIn.getPosX(), componentIn.getPosY());
      component = componentIn;
      folders = new GuiCustomScrollNop(this, 101);
      items = new GuiCustomScrollNop(this, 102);
      label = new CustomGuiLabel(parent, new CustomGuiLabelWrapper().setCentered(true));
      init();
      if (!componentIn.getSelected().isEmpty()) { selectedResource = prevResource = ResourceLocation.tryParse(componentIn.getSelected()); }
      List<ResourceLocation> resources = AssetsFinder.find(componentIn.getRoot(), "." + componentIn.getFileType());
      for (ResourceLocation loc : resources) { domains.computeIfAbsent(loc.getNamespace(), (k) -> new ArrayList<>()).add(loc); }
      if (selectedResource != null && !selectedResource.getPath().isEmpty()) {
         selectedDomain = selectedResource.getNamespace();
         if (!domains.containsKey(selectedDomain)) { selectedDomain = null; }
         int i = selectedResource.getPath().lastIndexOf(47);
         location = path = selectedResource.getPath().substring(0, i + 1);
         i = path.lastIndexOf(47, path.length() - 2);
         if (i > 0) { location = path.substring(0, i + 1); }
         label.setText(selectedDomain + ":" + location);
      }
      setFolders();
      setItems();
   }

   @Override
   public void init() {
      id = component.getId();
      folders.id = id;
      items.id = id;
      setX(component.getPosX());
      setY(component.getPosY());
      setWidth(component.getWidth());
      setHeight(component.getHeight());
      folders.setPos(folders.getX(), getY() + 10);
      items.setPos(getX() + component.getWidth() / 2 + 1, getY() + 10);
      folders.setSize(component.getWidth() / 2 - 1, component.getHeight() - 10);
      items.setSize(component.getWidth() / 2 - 1, component.getHeight() - 10);
      label.setWidth(component.getWidth());
      label.setX(getX());
      label.setY(getY());
      label.setHeight(10);
      enabled = component.getEnabled();
      visible = component.getVisible();
      if (component.hasHoverText()) { hoverText = component.getHoverTextList(); }
      if (!component.getSelected().isEmpty()) { selectedResource = ResourceLocation.tryParse(component.getSelected()); }
   }

   private void setFolders() {
      if (selectedDomain == null) {
         folders.setList(new ArrayList<>(domains.keySet()));
         if (selectedResource != null) {
            selectedDomain = selectedResource.getNamespace();
            folders.setSelectedIndex(selectedDomain);
         }
      }
      else {
         List<String> list = new ArrayList<>();
         list.add(up);
         Iterator<ResourceLocation> var2 = domains.get(selectedDomain).iterator();
         while(true) {
            String fullPath;
            do {
               if (!var2.hasNext()) {
                  folders.clearSelection();
                  folders.setList(list);
                  if (selectedResource != null && selectedResource.getPath().startsWith(location) && !location.equals(path)) {
                     folders.setSelectedIndex(path.substring(location.length(), path.length() - 1));
                     folders.scrollTo(folders.getSelected());
                  }
                  return;
               }
               ResourceLocation td = var2.next();
               fullPath = td.getPath();
               if (fullPath.indexOf(47) >= 0) {
                  fullPath = fullPath.substring(0, fullPath.lastIndexOf(47) + 1);
               }
            } while(!location.isEmpty() && (!fullPath.startsWith(location) || fullPath.equals(location)));

            String path = fullPath.substring(location.length());
            int i = path.indexOf(47);
            if (i >= 0) {
               path = path.substring(0, i);
               if (!path.isEmpty() && !list.contains(path)) { list.add(path); }
            }
         }
      }
   }

   private void setItems() {
      if (selectedDomain != null) {
         textures.clear();
         List<ResourceLocation> data = domains.get(selectedDomain);
         List<String> list = new ArrayList<>();
         Iterator<ResourceLocation> var3 = data.iterator();
         String name;
         while(var3.hasNext()) {
            ResourceLocation td = var3.next();
            name = td.getPath();
            String p = td.getPath();
            if (name.indexOf(47) >= 0) {
               name = name.substring(name.lastIndexOf(47) + 1);
               p = p.substring(0, p.lastIndexOf(47) + 1);
            }
            if (p.equals(path) && !list.contains(name)) {
               list.add(name);
               textures.put(name, td);
            }
         }

         items.clearSelection();
         items.setList(list);
         if (selectedResource != null) {
            int i = selectedResource.getPath().lastIndexOf(47);
            if (selectedResource.getPath().substring(0, i + 1).equals(path)) {
               items.setSelectedIndex(selectedResource.getPath().substring(i + 1));
            }
         }
      }
   }

   @Override
   public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (!enabled || !visible) { return; }
      super.render(graphics, mouseX, mouseY, partialTicks);
      if (isHovered && component.hasHoverText() && !hoverText.isEmpty() && listener != null) {
         listener.setHoverText(component.getHoverTextList());
      }
   }

   @Override
   protected void renderWidget(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      isHovered = false;
      if (!visible) { return; }
      label.render(graphics, mouseX, mouseY, partialTicks);
      folders.render(graphics, mouseX, mouseY, partialTicks);
      items.render(graphics, mouseX, mouseY, partialTicks);
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double mouseScrolled) {
      return folders.mouseScrolled(mouseX, mouseY, mouseScrolled) || items.mouseScrolled(mouseX, mouseY, mouseScrolled);
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return folders.mouseClicked(mouseX, mouseY, button) || items.mouseClicked(mouseX, mouseY, button);
   }

   @Override
   public boolean keyPressed(int key, int key_1, int key_2) {
      return folders.keyPressed(key, key_1, key_2) || items.keyPressed(key, key_1, key_2);
   }

   @Override
   public boolean charTyped(char c, int keyId) {
      return folders.charTyped(c, keyId) || items.charTyped(c, keyId);
   }

   @Override
   public ICustomGuiComponent component() { return component; }

   @Override
   public void scrollClicked(GuiCustomScrollNop scroll) {
      if (scroll.id == 101 && !Util.instance.equalsDeleteColor(scroll.getSelected(), up, false)) {
         path = location + scroll.getSelected() + "/";
         setItems();
      }
      else if (scroll.id == 102) {
         String key = Util.instance.deleteColor(scroll.getSelected());
         selectedResource = CustomGuiAssetsSelector.textures.get(key);
         component.setSelected(selectedResource.toString());
         if (!component.disablePackets) { Packets.sendServer(new SPacketCustomGuiTextUpdate(component.getUniqueID(), component.getSelected())); }
         else { component.onChange(null); }
      }
   }

   @Override
   public void scrollDoubleClicked(GuiCustomScrollNop scroll) {
      if (scroll.id == 101) {
         if (selectedDomain == null) {
            selectedDomain = scroll.getSelected();
            if (!component.getRoot().isEmpty()) { path = location = component.getRoot() + "/"; }
         }
         else if (Util.instance.equalsDeleteColor(scroll.getSelected(), up, false)) {
            int i = location.lastIndexOf(47, location.length() - 2);
            if (i > 0) {
               path = location;
               location = location.substring(0, i + 1);
            }
            else { path = location = ""; }
            if (location.isEmpty()) { selectedDomain = null; }
         } else {
            String var10002 = location;
            path = location = var10002 + scroll.getSelected() + "/";
         }
         setFolders();
         setItems();
         label.setText(selectedDomain + ":" + location);
      }
      else if (scroll.id == 102) {
         if (!component.disablePackets) { Packets.sendServer(new SPacketCustomGuiButton(component.getUniqueID())); }
         else { component.onPress(null); }
      }
   }

   @Override
   public GuiComponentType getElementType() { return GuiComponentType.ASSETS_SELECTOR; }

}
