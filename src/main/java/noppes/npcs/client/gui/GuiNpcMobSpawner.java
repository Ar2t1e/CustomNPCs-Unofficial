package noppes.npcs.client.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.client.EntityUtil;
import noppes.npcs.client.controllers.ClientCloneController;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketCloneList;
import noppes.npcs.packets.server.SPacketCloneRemove;
import noppes.npcs.packets.server.SPacketGetServerCloneEntity;
import noppes.npcs.packets.server.SPacketToolMobSpawner;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.components.GuiMenuTopButton;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.common.util.LogWriter;

import javax.annotation.Nullable;

public class GuiNpcMobSpawner extends GuiNPCInterface implements IGuiData, ICustomScrollListener {

   protected static int showingClones = 0;
   protected final BlockPos pos;
   protected final List<String> list = new ArrayList<>();
   protected int activeTab = 1;
   protected GuiCustomScrollNop scroll;

   // New from Unofficial (BetaZavr)
   protected int sel = -1;
   public LivingEntity selectNpc;

   public GuiNpcMobSpawner(BlockPos posIn) {
      super();
      setBackground("menubg.png");
      imageWidth = 256;

      pos = posIn;
   }

   @Override
   public void init() {
      super.init();
      guiTop += 10;
      if (scroll == null) { scroll = addScroll(0).setSize(165, 210); }
      else { scroll.clear();}
      add(scroll.setPos(guiLeft + 4, guiTop + 4).setSelectedIndex(sel));
      // clones
      GuiMenuTopButton button = addTopButton(3, guiLeft + 4, guiTop - 17, "spawner.clones")
              .setIsEnabled(showingClones == 0);
      // entities
      button = addTopButton(4, button.getX() + button.getWidth(), button.getY(), "spawner.entities")
              .setIsEnabled(showingClones == 1);
      // server
      addTopButton(5, button.getX() + button.getWidth(), button.getY(), "gui.server")
              .setIsEnabled(showingClones == 2);
      int x = guiLeft + 171;
      int y = guiTop + 6;
      addButton(1, x, y, "gui.spawn").setSize(82, 20);
      addButton(2, x, y += 138, "spawner.mobspawner")
              .setSize(82, 20);
      addButton(66, x, y + 22, "gui.done")
              .setSize(80, 20)
              .setHoverTexts("hover.exit");
      if (showingClones != 0 && showingClones != 2) { showEntities(); }
      else {
         for (int id = 0; id < 9; id++) {
            addSideButton(21 + id, guiLeft, guiTop + 3 + id * 21, Component.translatable("gui.tab").append(" " + (id + 1)))
                    .setIsEnabled(activeTab == id + 1);
         }
         addButton(6, guiLeft + 170, guiTop + 30, "gui.remove")
                 .setSize(82, 20);
         showClones();
      }
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      if (button.id > 20 && button.id < 31) {
         activeTab = button.id - 20;
         init();
         return;
      }
      switch (button.id) {
         case 1: {
            if (showingClones == 2) {
               String sel = scroll.getSelected();
               if (sel.isEmpty()) { return; }
               Packets.sendServer(new SPacketToolMobSpawner(true, false, pos, sel, activeTab, new CompoundTag()));
               onClose();
            } else {
               CompoundTag compound = getCompound();
               if (compound == null) { return; }
               Packets.sendServer(new SPacketToolMobSpawner(false, false, pos, "", 0, compound));
               onClose();
            }
            break;
         }
         case 2: {
            if (showingClones == 2) {
               String sel = scroll.getSelected();
               if (sel.isEmpty()) { return; }
               Packets.sendServer(new SPacketToolMobSpawner(true, true, pos, sel, activeTab, new CompoundTag()));
               onClose();
            } else {
               CompoundTag compound = getCompound();
               if (compound == null) { return; }
               Packets.sendServer(new SPacketToolMobSpawner(false, true, pos, "", 0, compound));
               onClose();
            }
            break;
         }
         case 3: showingClones = 0; init(); break;
         case 4: showingClones = 1; init(); break;
         case 5: showingClones = 2; init(); break;
         case 6: {
            if (scroll.hasSelected()) {
               if (showingClones == 2) {
                  Packets.sendServer(new SPacketCloneRemove(scroll.getSelected(), activeTab));
                  return;
               }
               ClientCloneController.Instance.removeClone(scroll.getSelected(), activeTab);
               scroll.clearSelection();
               init();
            }
            break;
         }
         case 66: onClose(); break;
      }
   }

   @Override
   public void setGuiData(CompoundTag compound) {
      if (compound.contains("NPCData", 10)) {
         Optional<Entity> entityO = EntityType.create(compound.getCompound("NPCData"), player.level());
         if (entityO.isPresent() && entityO.get() instanceof LivingEntity entity) { selectNpc = entity; }
         else { selectNpc = null; }
         return;
      }
      ListTag nbtList = compound.getList("List", 8);
      list.clear();
      for(int i = 0; i < nbtList.size(); ++i) { list.add(nbtList.getString(i)); }
      scroll.setList(list);
   }

   private void showEntities() {
      list.clear();
      if (minecraft == null) { minecraft = Minecraft.getInstance();}
      list.addAll(EntityUtil.getAllEntities(minecraft.level, false).keySet());
      scroll.setList(list).setSelectedIndex(sel);
   }

   private void showClones() {
      if (showingClones == 2) {
         Packets.sendServer(new SPacketCloneList(activeTab));
      } else {
         list.clear();
         list.addAll(ClientCloneController.Instance.getClones(activeTab));
         scroll.setList(list).setSelectedIndex(sel);
         resetEntity();
      }
   }

   // Client Clones or Vanilla Mobs
   private @Nullable CompoundTag getCompound() {
      String sel = scroll.getSelected();
      if (sel.isEmpty()) { return null; }
      if (showingClones == 0) { return ClientCloneController.Instance.getCloneData(player.createCommandSourceStack(), sel, activeTab); }
      // Vanilla Mobs
      if (minecraft != null && minecraft.level != null) {
         ResourceLocation loc = EntityUtil.getAllEntities(Minecraft.getInstance().level, false).get(sel);
         EntityType<?> type = ForgeRegistries.ENTITY_TYPES.getValue(loc);
         if (type != null) {
            Entity entity = type.create(minecraft.level);
            if (entity == null) { return null; }
            CompoundTag compound = new CompoundTag();
            entity.saveAsPassenger(compound);
            return compound;
         }
      }
      return null;
   }

   // New from Unofficial (BetaZavr)
   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (!hasSubGui() && player != null) {
         PoseStack matrixStack = graphics.pose();
         matrixStack.pushPose();
         if (selectNpc != null) { drawNpc(graphics, selectNpc, 210, 130, 1.0f, (int) (3 * player.level().getGameTime() % 360), 0, 0); }
         matrixStack.translate(0.0f, 0.0f, 1.0f);
         graphics.fill(guiLeft + 179, guiTop + 54, guiLeft + 242, guiTop + 142, new Color(0xFF808080).getRGB());
         graphics.fill(guiLeft + 180, guiTop + 55, guiLeft + 241, guiTop + 141, new Color(0xFF000000).getRGB());
         matrixStack.popPose();
      }
      super.render(graphics, mouseX, mouseY, partialTicks);
   }

   @Override
   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      if (!hasSubGui()) {
         if (minecraft == null) { minecraft = Minecraft.getInstance(); }
         if (keyCode == InputConstants.KEY_UP ||
                 keyCode == InputConstants.KEY_DOWN ||
                 keyCode == minecraft.options.keyUp.getKey().getValue() ||
                 keyCode == minecraft.options.keyDown.getKey().getValue()) {
            resetEntity();
         }
      }
      return super.keyPressed(keyCode, scanCode, modifiers);
   }

   private void resetEntity() {
      if (GuiNpcMobSpawner.showingClones == 0) { // client
         CompoundTag npcNbt = ClientCloneController.Instance.getCloneData(player.createCommandSourceStack(), scroll.getSelected(), activeTab);
         if (npcNbt == null) { return; }
         Optional<Entity> entityO = EntityType.create(npcNbt, player.level());
         if (entityO.isPresent() && entityO.get() instanceof LivingEntity living) { selectNpc = living; }
      }
      else if (GuiNpcMobSpawner.showingClones == 1) { // mob
         for (EntityType<?> ent : ForgeRegistries.ENTITY_TYPES.getValues()) {
            try {
               Entity e = ent.create(player.level());
               if (e != null && ent.getDescriptionId().equals(scroll.getSelected()) && e instanceof LivingEntity living) {
                  selectNpc = living;
               }
            }
            catch (Throwable var6) { LogWriter.except(var6); }
         }
      }
      else { // server
         Packets.sendServer(new SPacketGetServerCloneEntity(false, false, activeTab, scroll.getSelected()));
      }
   }

   @Override
   public void scrollClicked(GuiCustomScrollNop scroll) { resetEntity(); }

   @Override
   public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

}
