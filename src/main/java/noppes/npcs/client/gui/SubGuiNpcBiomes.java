package noppes.npcs.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import noppes.npcs.controllers.data.SpawnData;
import noppes.npcs.shared.client.gui.GuiBasic;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;

public class SubGuiNpcBiomes extends GuiBasic implements ICustomScrollListener {

   protected final SpawnData data;
   protected GuiCustomScrollNop availableBiomes;
   protected GuiCustomScrollNop spawningBiomes;

   public SubGuiNpcBiomes(SpawnData dataIn) {
      super();
      data = dataIn;
      setBackground("menubg.png");

      imageWidth = 346;
      imageHeight = 216;
   }

   @Override
   public void init() {
      super.init();
      if (availableBiomes == null) { availableBiomes = addScroll(0).setSize(140, 180); }
      if (spawningBiomes == null) { spawningBiomes = addScroll(1).setSize(140, 180); }
      List<Component> biomes = new ArrayList<>();
      Registry<Biome> biomeRegistry = player.level().registryAccess().registryOrThrow(Registries.BIOME);
      for (Biome base : biomeRegistry) {
         ResourceLocation registryName = biomeRegistry.getKey(base);
         if (registryName != null && !data.biomes.contains(registryName)) { biomes.add(Component.translatable(registryName.toString())); }
      }
      int x0 = guiLeft + 4;
      int x1 = x0 + 196;
      int y = guiTop + 14;
      add(availableBiomes.setPos(x0, y)
              .setNormalList(biomes));
      add(spawningBiomes.setPos(x1, y)
              .setList(data.biomes.stream().map(Object::toString).collect(Collectors.toList())));
      addLabel(1, x0 + 1, y - 10, "spawning.availableBiomes");
      addLabel(2, x1 + 1, y - 10, "spawning.spawningBiomes");
      x0 = guiLeft + 145;
      y = guiTop + 40;
      addButton(1, x0, y, ">").setSize(55, 20);
      addButton(2, x0, y += 22, "<").setSize(55, 20);
      addButton(3, x0, y += 28, ">>").setSize(55, 20);
      addButton(4, x0, y + 22, "<<").setSize(55, 20);
      addButton(66, guiLeft + 260, guiTop + 194, "gui.done")
              .setSize(60, 20)
              .setHoverTexts("hover.back");
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      switch (button.id) {
         case 1: {
            if (availableBiomes.hasSelected()) {
               data.biomes.add(ResourceLocation.tryParse(availableBiomes.getSelected()));
               availableBiomes.clearSelection();
               spawningBiomes.clearSelection();
               init();
            }
            break;
         }
         case 2: {
            if (spawningBiomes.hasSelected()) {
               data.biomes.remove(ResourceLocation.tryParse(spawningBiomes.getSelected()));
               spawningBiomes.clearSelection();
               init();
            }
            break;
         }
         case 3: {
            data.biomes.clear();
            Registry<Biome> biomeRegistry = player.level().registryAccess().registryOrThrow(Registries.BIOME);
            for (Biome base : biomeRegistry) {
               if (base != null) {
                  data.biomes.add(biomeRegistry.getKey(base));
               }
            }
            availableBiomes.clearSelection();
            spawningBiomes.clearSelection();
            init();
            break;
         }
         case 4: {
            data.biomes.clear();
            availableBiomes.clearSelection();
            spawningBiomes.clearSelection();
            init();
            break;
         }
         case 66: onClose(); break;
      }
   }

   @Override
   public void scrollClicked(GuiCustomScrollNop scroll) { }

   @Override
   public void scrollDoubleClicked(GuiCustomScrollNop scroll) {
      if (scroll.hasSelected()) {
         if (scroll.id == 1) {
            data.biomes.add(ResourceLocation.tryParse(scroll.getSelected()));
            scroll.setSelect(-1);
            init();
         }
         if (scroll.id == 2) {
            data.biomes.remove(ResourceLocation.tryParse(scroll.getSelected()));
            scroll.setSelect(-1);
            init();
         }
      }
   }

}
