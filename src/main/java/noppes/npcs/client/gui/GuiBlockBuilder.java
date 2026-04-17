package noppes.npcs.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import java.util.*;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.client.ClientEventHandler;
import noppes.npcs.client.NoppesUtil;
import noppes.npcs.client.gui.availability.SubGuiNpcAvailability;
import noppes.npcs.client.gui.util.GuiNPCInterface;
import noppes.npcs.controllers.SchematicController;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketSchematicsTileBuild;
import noppes.npcs.packets.server.SPacketSchematicsTileGet;
import noppes.npcs.packets.server.SPacketSchematicsTileSave;
import noppes.npcs.packets.server.SPacketSchematicsTileSet;
import noppes.npcs.schematics.SchematicWrapper;
import noppes.npcs.shared.client.gui.components.GuiButtonNop;
import noppes.npcs.shared.client.gui.components.GuiButtonYesNo;
import noppes.npcs.shared.client.gui.components.GuiCustomScrollNop;
import noppes.npcs.shared.client.gui.listeners.ICustomScrollListener;
import noppes.npcs.shared.client.gui.listeners.IGuiData;
import noppes.npcs.shared.client.gui.listeners.IScrollData;

public class GuiBlockBuilder extends GuiNPCInterface
        implements IGuiData, ICustomScrollListener, IScrollData {

   protected final BlockPos pos;
   protected final TileBuilder tile;
   protected GuiCustomScrollNop scroll;
   protected SchematicWrapper wrapper = null;

   public GuiBlockBuilder(BlockPos posIn) {
      super();
      setBackground("menubg.png");
      imageWidth = 256;
      imageHeight = 216;

      pos = posIn;
      tile = (TileBuilder) player.level().getBlockEntity(pos);
      Packets.sendServer(new SPacketSchematicsTileGet(pos));
   }

   @Override
   public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
      if (minecraft == null) { minecraft = Minecraft.getInstance(); }
      super.render(graphics, mouseX, mouseY, partialTicks);
      if (wrapper == null || minecraft.level == null) { return; }
      PoseStack matrixStack = graphics.pose();
      matrixStack.pushPose();
      matrixStack.translate(guiLeft + imageWidth, guiTop + 26.0f, 0.0f);
      // background
      graphics.blit(background, 0, 0, 172, 0, 84, 80);
      graphics.blit(background, 0, 80, 172, 213, 84, 4);
      // schem
      int w = wrapper.schema.getWidth();
      int l = wrapper.schema.getLength();
      int h = wrapper.schema.getHeight();
      float sW = (float) (w + l) * (float) Math.cos(Math.toRadians(30));
      float sH = (float) (w + l) * (float) Math.sin(Math.toRadians(30)) + (float) h;
      float scale;
      if (sW > sH) { scale = 84.0f / sW; } else { scale = 84.0f / sH; }

      graphics.enableScissor(guiLeft + imageWidth + 2, guiTop + 30, guiLeft + imageWidth + 78, guiTop + 106);
      matrixStack.translate(42.0f - (w / 2.0f) * scale, 41.0f + (h / 2.0f) * scale, 150.0f);
      matrixStack.scale(scale, -scale, -scale);

      matrixStack.pushPose();
      matrixStack.translate(w / 2.0f, h / 2.0f, l / 2.0f);
      float f0 = (minecraft.level.getGameTime() % 360.0f) * 2.0f;
      matrixStack.mulPose(Axis.XP.rotationDegrees(30));
      matrixStack.mulPose(Axis.YP.rotationDegrees(f0));
      matrixStack.translate(-w / 2.0f, -h / 2.0f, -l / 2.0f);

      ClientEventHandler.renderSchem(matrixStack, graphics.bufferSource(), partialTicks, wrapper, 0, 0, 0, 0);
      matrixStack.popPose();

      graphics.disableScissor();
      matrixStack.popPose();
   }

   @Override
   public void init() {
      super.init();
      if (scroll == null) { scroll = addScroll(0).setSize(125, 208); }
      add(scroll.setPos(guiLeft + 4, guiTop + 4));
      if (wrapper != null) {
         int x0 = guiLeft + 132;
         int x1 = x0 + 69;
         int y = guiTop + 4;
         addYesNo(3, x1, y, tile.getSchematic() != null && tile.getShow());
         addLabel(3, x0, y + 5, "schematic.preview").setSize(66, 12);

         addLabel(0, x0, y += 21, Component.translatable("schematic.width").append(": ").append("" + wrapper.schema.getWidth())).setSize(120, 12);
         addLabel(1, x0, y += 11, Component.translatable("schematic.length").append(": ").append("" + wrapper.schema.getLength())).setSize(120, 12);
         addLabel(2, x0, y += 11, Component.translatable("schematic.height").append(": ").append("" + wrapper.schema.getHeight())).setSize(120, 12);

         addYesNo(4, x1, y += 14, tile.enabled);
         addLabel(4, x0, y + 5, "gui.enabled").setSize(66, 12);

         addYesNo(7, x1, y += 22, tile.finished);
         addLabel(7, x0, y + 5, "gui.finished").setSize(66, 12);

         addYesNo(8, x1, y += 22, tile.started);
         addLabel(8, x0, y + 5, "gui.started").setSize(66, 12);

         addTextField(9, x1, y += 22, 50, 20, "" + tile.yOffset);
         addLabel(9, x0, y + 5, "gui.yoffset").setSize(66, 12);
         getTextField(9).setMinMaxDefault(-10, 10, 0);

         addButton(5, x1, y += 22, false, tile.rotation, 0, 90, 180, 270)
                 .setSize(50, 20);
         addLabel(5, x0, y + 5, "movement.rotation").setSize(66, 12);

         addButton(6, x0 - 1, y += 22, "availability.options")
                 .setSize(120, 20)
                 .setHoverTexts("builder.hover.availability");
         addButton(10, x0 - 1, y + 22, "schematic.instantBuild")
                 .setSize(120, 20);
      }
   }

   @Override
   public void buttonEvent(GuiButtonNop button) {
      switch (button.id) {
         case 3: tile.setDrawSchematic(wrapper, ((GuiButtonYesNo) button).getBoolean()); break;
         case 4: tile.enabled = ((GuiButtonYesNo) button).getBoolean(); break;
         case 5: tile.rotation = button.getValue();break;
         case 6: setSubGui(new SubGuiNpcAvailability(tile.availability, this)); break;
         case 7: {
            tile.finished = ((GuiButtonYesNo) button).getBoolean();
            Packets.sendServer(new SPacketSchematicsTileSet(pos, scroll.getSelected()));
            break;
         }
         case 8: tile.started = ((GuiButtonYesNo) button).getBoolean(); break;
         case 10: {
            save();
            ConfirmScreen guiYesNo = new ConfirmScreen((agree) -> {
               if (agree) {
                  Packets.sendServer(new SPacketSchematicsTileBuild(pos, 0, new CompoundTag()));
                  onClose();
                  wrapper = null;
                  tile.setDrawSchematic(null, false);
               }
               else { NoppesUtil.openGUI(player, this); }
            },
                    Component.empty(),
                    Component.translatable("schematic.instantBuildText"));
            setScreen(guiYesNo);
            break;
         }
      }
   }

   @Override
   public void save() {
      if (tile != null) {
         if (getTextField(9) != null) { tile.yOffset = getTextField(9).getInteger(); }
         Packets.sendServer(new SPacketSchematicsTileSave(pos, tile.savePartNBT(new CompoundTag())));
      }
   }

   @Override
   public void setGuiData(final CompoundTag compound) {
      if (compound.contains("Width")) {
         wrapper = SchematicController.Instance.load(compound.getString("SchematicName"));
         wrapper.rotation = tile.rotation;
         scroll.setSelected(wrapper.schema.getName());
         if (getButton(3) != null) { tile.setDrawSchematic(wrapper, ((GuiButtonYesNo) getButton(3)).getBoolean()); }
         else { tile.setDrawSchematic(wrapper, tile.getShow()); }
      }
      else { tile.loadPartNBT(compound);}
      init();
   }

   @Override
   public void scrollClicked(GuiCustomScrollNop scroll) {
      if (scroll.hasSelected()) { Packets.sendServer(new SPacketSchematicsTileSet(pos, scroll.getSelected())); }
   }

   @Override
   public void scrollDoubleClicked(GuiCustomScrollNop scroll) { }

   @Override
   public void setData(Vector<String> dataList, Map<String, Integer> dataMap) {
      scroll.setList(dataList);
      if (wrapper != null) { scroll.setSelected(wrapper.schema.getName()); }
      init();
   }

   @Override
   public void setSelected(String selected) { }

}
