package noppes.npcs.client.gui.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomNpcs;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;

public class GuiCreationEntities extends GuiCreationScreenInterface implements ICustomScrollListener {

   protected final List<EntityType<? extends Entity>> types;
   protected GuiCustomScrollNop scroll;
   protected boolean resetToSelected = true;

   public GuiCreationEntities(EntityNPCInterface npc) {
      super(npc);
      types = getAllEntities(npc.level());
      types.sort(Comparator.comparing((t) -> t.getDescriptionId().toLowerCase()));
      active = 1;
      xOffset = 60;
   }

   private static List<EntityType<? extends Entity>> getAllEntities(Level level) {
      List<EntityType<? extends Entity>> data = new ArrayList<>();
      for (EntityType<?> ent : ForgeRegistries.ENTITY_TYPES.getValues()) {
         try {
            Entity e = ent.create(level);
            if (e != null) {
               if (LivingEntity.class.isAssignableFrom(e.getClass()) && !EnderDragon.class.isAssignableFrom(e.getClass())) { data.add(ent); }
               e.discard();
            }
         } catch (Exception ignored) {}
      }
      return data;
   }

   @Override
   public void init() {
      super.init();
      add(new GuiButtonNop(this, 10, "Reset To NPC", guiLeft, guiTop + 46,
              button -> {
                 playerdata.setEntity(null);
                 npc.display.setSkinTexture(CustomNpcs.MODID + ":textures/entity/humanmale/steve.png");
                 resetToSelected = true;
                 init();
              }).setSize(120, 20));
      if (scroll == null) {
         List<Component> list = new ArrayList<>();
         for (String line : types.stream().map(EntityType::getDescriptionId).toList()) { list.add(Component.translatable(line)); }
         scroll = addScroll(0).setUnsortedList(list);
      }
      int index = -1;
      if (entity != null) {
         for(int i = 0; i < types.size(); ++i) {
            EntityType<?> type = types.get(i);
            if (type == entity.getType()) {
               index = i;
               break;
            }
         }
      }
      if (index >= 0) { scroll.setSelected(index); }
      else { scroll.setSelected("entity." + CustomNpcs.MODID + ".customnpc"); }
      if (resetToSelected) {
         scroll.scrollTo(scroll.getSelected());
         resetToSelected = false;
      }
      add(scroll.setPos(guiLeft, guiTop + 68)
              .setSize(120, imageHeight - 96));
      addLabel(110, guiLeft + 124, guiTop + 5, "gui.simpleRenderer")
              .setColor(CustomNpcs.MainColor.getRGB());
      add(new GuiButtonYesNo(this, 110, guiLeft + 260, guiTop, playerdata.simpleRender,
              (b) -> playerdata.simpleRender = ((GuiButtonYesNo)b).getBoolean()));
   }

   @Override
   public void scrollClicked(GuiCustomScrollNop scroll) {
      String selected = scroll.getSelected();
      if (selected.equals("Custom NPC")) { playerdata.setEntity(null); }
      else { playerdata.setEntity(ForgeRegistries.ENTITY_TYPES.getKey(types.get(scroll.getSelectedIndex()))); }

      Entity entity = playerdata.getEntity(npc);
      if (entity != null) {
         if (minecraft == null) { minecraft = Minecraft.getInstance(); }
         EntityRenderer<? super Entity> render = minecraft.getEntityRenderDispatcher().getRenderer(entity);
         try {
            if (render instanceof LivingEntityRenderer && !render.getTextureLocation(entity).toString().equals("minecraft:missingno")) {
               npc.display.setSkinTexture(render.getTextureLocation(entity).toString());
            }
         } catch (Exception var11) {
            npc.display.setSkinTexture(CustomNpcs.MODID + ":textures/entity/humanmale/steve.png");
         }
      }
      else {
         npc.display.setSkinTexture(CustomNpcs.MODID + ":textures/entity/humanmale/steve.png");
      }
      init();
   }

   @Override
   public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

}
