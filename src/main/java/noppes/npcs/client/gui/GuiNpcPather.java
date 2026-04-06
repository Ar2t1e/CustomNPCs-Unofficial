package noppes.npcs.client.gui;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.NBTTags;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataAI;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketMenuGet;
import noppes.npcs.packets.server.SPacketMenuSave;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.IGuiData;

public class GuiNpcPather
        extends GuiNPCInterface
        implements IGuiData {

   protected GuiCustomScrollNop scroll;
   protected final DataAI ai;

   public GuiNpcPather(EntityNPCInterface npc) {
      super();
      title = Component.literal("Npc Pather");
      setBackground("smallbg.png");
      drawDefaultBackground = false;
      imageWidth = 176;

      ai = npc.ais;
      Packets.sendServer(new SPacketMenuGet(EnumMenuType.MOVING_PATH));
   }

   @Override
   public void init() {
      int sel;
      if (scroll != null) { sel = scroll.getSelectedIndex(); }
      else {
         sel = 0;
         Vec3 vec3d = player.getEyePosition(1.0f);
         Vec3 vec3d1 = player.getViewVector(1.0F);
         Vec3 vec3d2 = vec3d.add(vec3d1.x * 6.0d, vec3d1.y * 6.0d, vec3d1.z * 6.0d);
         BlockHitResult result = player.level().clip(new ClipContext(vec3d, vec3d2, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
         if (result.getType() != HitResult.Type.BLOCK) {
            int x = result.getBlockPos().getX();
            int y = result.getBlockPos().getY();
            int z = result.getBlockPos().getZ();
            int i = 0;
            for (int[] arr : ai.getMovingPath()) {
               if (arr[0] == x && y == arr[1] && z == arr[2]) {
                  sel = i;
                  break;
               }
               i++;
            }
         }
      }
      super.init();
      List<Component> list = new ArrayList<>();
      for (int[] arr : ai.getMovingPath()) { list.add(Component.literal("x:" + arr[0] + " y:" + arr[1] + " z:" + arr[2])); }
      if (scroll == null) { scroll = addScroll(0).setSize(160, 177); }
      add(scroll.setUnsortedList(list).setPos(guiLeft + 7, guiTop + 16).setSelect(sel));
      int y = guiTop + 40 + scroll.height;
      addButton(0, guiLeft + 7, y, "gui.down")
              .setSize(52, 20);
      addButton(1, guiLeft + 61, y, "gui.up")
              .setSize(52, 20);
      addButton(2, guiLeft + 115, y, "selectServer.delete")
              .setSize(52, 20);
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      if (scroll.hasSelected()) {
         switch (button.id) {
            case 0 : {
               List<int[]> list = ai.getMovingPath();
               int selected = scroll.getSelectedIndex();
               if (list.size() <= selected + 1) { return; }
               int[] a = list.get(selected);
               int[] b = list.get(selected + 1);
               list.set(selected, b);
               list.set(selected + 1, a);
               ai.setMovingPath(list);
               init();
               scroll.setSelectedIndex(selected + 1);
               break;
            } // down
            case 1 : {
               if (scroll.getSelectedIndex() - 1 < 0) { return; }
               List<int[]> list = ai.getMovingPath();
               int selected = scroll.getSelectedIndex();
               int[] a = list.get(selected);
               int[] b = list.get(selected - 1);
               list.set(selected, b);
               list.set(selected - 1, a);
               ai.setMovingPath(list);
               init();
               scroll.setSelectedIndex(selected - 1);
               break;
            } // up
            case 2 : {
               List<int[]> list = ai.getMovingPath();
               if (list.size() <= 1) { return; }
               list.remove(scroll.getSelectedIndex());
               scroll.setSelect(scroll.getSelectedIndex() - 1);
               ai.setMovingPath(list);
               init();
               break;
            } // remove
         }
      }
   }

   @Override
   public void save() {
      CompoundTag compound = new CompoundTag();
      compound.put("MovingPathNew", NBTTags.nbtIntegerArraySet(ai.getMovingPath()));
      Packets.sendServer(new SPacketMenuSave(EnumMenuType.MOVING_PATH, compound));
   }

   @Override
   public void setGuiData(CompoundTag compound) {
      ai.load(compound);
      init();
   }

}
