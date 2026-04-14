package noppes.npcs.client.gui.roles;

import java.util.*;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.client.gui.util.GuiNPCInterface2;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketNpcJobSave;
import noppes.npcs.roles.JobGuard;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;

public class GuiNpcGuard extends GuiNPCInterface2 implements ICustomScrollListener {

   protected final JobGuard role;
   protected final HashMap<String, EntityType<?>> entityData = new HashMap<>(); // descriptionId, resource
   protected final Map<Component, String> namesData = new HashMap<>();
   protected GuiCustomScrollNop scrollAllEntities;
   protected GuiCustomScrollNop scrollSelected;

   protected Component select = Component.empty();

   public GuiNpcGuard(EntityNPCInterface npc) {
      super(npc);
      backGui = EnumGuiType.MainMenuAdvanced;
      role = (JobGuard) npc.job;
      for (EntityType<?> ent : ForgeRegistries.ENTITY_TYPES.getValues()) {
         try {
            Entity entity = ent.create(player.level());
            if (entity != null) {
               if (LivingEntity.class.isAssignableFrom(entity.getClass()) && !EntityNPCInterface.class.isAssignableFrom(entity.getClass())) {
                  entityData.put(ent.getDescriptionId(), ent);
                  namesData.put(entity.getName(), ent.getDescriptionId());
               }
               entity.discard();
            }
         } catch (Exception ignored) {}
      }
   }

   @Override
   public void init() {
      super.init();
      int x = guiLeft + 5;
      int y = guiTop + 5;
      List<Component> selected = new ArrayList<>();
      for (String descriptionId : role.targets) {
         EntityType<?> type = entityData.get(descriptionId);
         boolean needAdd = true;
         if (type != null) {
            Entity entity = type.create(player.level());
            if (entity != null) {
               selected.add(entity.getName());
               needAdd = false;
            }
         }
         if (needAdd) { selected.add(Component.literal(descriptionId)); }
      }
      List<Component> allNames = new ArrayList<>();
      for (Component key : namesData.keySet()) {
         boolean needAdd = true;
         for (Component name : selected) {
            if (name.getString().equals(key.getString())) {
               needAdd = false;
               break;
            }
         }
         if (needAdd) { allNames.add(key); }
      }
      addButton(0, x, y, "guard.animals")
              .setSize(100, 20);
      addButton(1, x + 130, y, "guard.mobs")
              .setSize(100, 20);
      addButton(2, x + 260, y, "guard.creepers")
              .setSize(100, 20);
      y += 34;
      if (scrollAllEntities == null) { scrollAllEntities = addScroll(0).setSize(175, 174); }
      scrollAllEntities.setNormalList(allNames);
      if (!select.getString().isEmpty()) { scrollAllEntities.setSelectedIndex(select); }
      add(scrollAllEntities.setPos(x, y));
      addLabel(11, x + 1, y - 10, "guard.availableTargets");
      if (scrollSelected == null) { scrollSelected = addScroll(1).setSize(175, 174); }
      x = guiLeft + 183;
      scrollSelected.setNormalList(selected);
      if (!select.getString().isEmpty()) { scrollSelected.setSelectedIndex(select); }
      add(scrollSelected.setPos(x + 58, y));
      addLabel(12, x + 59, y - 10, "guard.currentTargets");
      addButton(11, x, y += 22, ">")
              .setIsEnabled(scrollAllEntities.hasSelected())
              .setSize(55, 20);
      addButton(12, x, y += 22, "<")
              .setIsEnabled(scrollSelected.hasSelected())
              .setSize(55, 20);
      addButton(13, x, y += 22, ">>")
              .setIsEnabled(!allNames.isEmpty())
              .setSize(55, 20);
      addButton(14, x, y + 22, "<<")
              .setIsEnabled(!selected.isEmpty())
              .setSize(55, 20);
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      switch (button.id) {
         case 0: {
            for (Entry<EntityType<? extends Entity>, Class<? extends Entity>> entry : EntityUtil.getAllEntitiesClasses(player.level()).entrySet()) {
               String descriptionId = entry.getKey().getDescriptionId();
               if (Animal.class.isAssignableFrom(entry.getValue()) && !role.targets.contains(descriptionId)) { role.targets.add(descriptionId); }
            }
            scrollAllEntities.clearSelection();
            scrollSelected.clearSelection();
            init();
            break;
         } // all animals
         case 1: {
            for (Entry<EntityType<? extends Entity>, Class<? extends Entity>> entry : EntityUtil.getAllEntitiesClasses(player.level()).entrySet()) {
               String descriptionId = entry.getKey().getDescriptionId();
               if (Monster.class.isAssignableFrom(entry.getValue()) || Enemy.class.isAssignableFrom(entry.getValue())) {
                  if (!Creeper.class.isAssignableFrom(entry.getValue()) && !role.targets.contains(descriptionId)) { role.targets.add(descriptionId); }
               }
            }
            scrollAllEntities.clearSelection();
            scrollSelected.clearSelection();
            init();
            break;
         } // all mobs
         case 2: {
            if (minecraft == null) { minecraft = Minecraft.getInstance(); }
            for (Entry<EntityType<? extends Entity>, Class<? extends Entity>> entry : EntityUtil.getAllEntitiesClasses(player.level()).entrySet()) {
               String descriptionId = entry.getKey().getDescriptionId();
               if (Creeper.class.isAssignableFrom(entry.getValue()) && !role.targets.contains(descriptionId)) { role.targets.add(descriptionId); }
            }
            scrollAllEntities.clearSelection();
            scrollSelected.clearSelection();
            init();
            break;
         } // all creepers
         case 11: {
            if (namesData.containsKey(scrollAllEntities.getNormalSelected())) {
               role.targets.add(namesData.get(scrollAllEntities.getNormalSelected()));
               scrollAllEntities.clearSelection();
               scrollSelected.clearSelection();
               init();
            }
            break;
         } // >
         case 12: {
            if (namesData.containsKey(scrollSelected.getNormalSelected())) {
               role.targets.remove(namesData.get(scrollSelected.getNormalSelected()));
               scrollSelected.clearSelection();
               init();
            }
            break;
         } // <
         case 13: {
            role.targets.clear();
            for (EntityType<?> ent : ForgeRegistries.ENTITY_TYPES.getValues()) {
               try {
                  Entity entity = ent.create(player.level());
                  if (entity != null) {
                     if (LivingEntity.class.isAssignableFrom(entity.getClass()) && !EntityNPCInterface.class.isAssignableFrom(entity.getClass())) {
                        role.targets.add(ent.getDescriptionId());
                     }
                     entity.discard();
                  }
               } catch (Exception ignored) {}
            }
            scrollAllEntities.clearSelection();
            scrollSelected.clearSelection();
            init();
            break;
         } // >>
         case 14: {
            role.targets.clear();
            scrollAllEntities.clearSelection();
            scrollSelected.clearSelection();
            init();
            break;
         } // <<
      }
   }

   @Override
   public void save() { Packets.sendServer(new SPacketNpcJobSave(role.save(new CompoundTag()))); }

   @Override
   public void scrollClicked(GuiCustomScrollNop scroll) {
      select = scroll.getNormalSelected();
      init();
   }

   @Override
   public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

}
