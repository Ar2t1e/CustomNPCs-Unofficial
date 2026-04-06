package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.client.gui.GuiAchievement;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.shared.common.PacketBasic;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.util.Util;

import java.lang.reflect.Field;
import java.util.List;

public class PacketAchievement extends PacketBasic {

   protected static int channelId;
   private final Component title;
   private final Component message;
   private final int type;

   // New from Unofficial (BetaZavr)
   // quest progress data:
   private final CompoundTag compound;

   public PacketAchievement(Component titleIn, Component messageIn, int typeIn, CompoundTag compoundIn) {
      title = titleIn;
      message = messageIn;
      type = typeIn;
      compound = compoundIn;
   }

   public static void encode(PacketAchievement msg, FriendlyByteBuf buf) {
      buf.writeComponent(msg.title);
      buf.writeComponent(msg.message);
      buf.writeInt(msg.type);
      buf.writeNbt(msg.compound);
   }

   public static PacketAchievement decode(FriendlyByteBuf buf) {
      return new PacketAchievement(buf.readComponent(), buf.readComponent(), buf.readInt(), buf.readNbt(new NbtAccounter(Long.MAX_VALUE)));
   }

   @Override
   public int getChannelId() { return channelId; }

   @SuppressWarnings("rawtypes")
   @OnlyIn(Dist.CLIENT)
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Minecraft minecraft = Minecraft.getInstance();
      MutableComponent showTitle = Component.empty().append(title);
      MutableComponent showMessage = Component.empty().append(message);
      // has quest
      if (compound.contains("questID", 3) && compound.getInt("questID") >= 0) {
         IQuest quest = QuestController.instance.get(compound.getInt("questID"));
         if (quest == null) {
            CustomNpcs.debugData.end("Packets");
            return;
         }
         showTitle = Component.translatable("quest.name")
                 .append(Component.literal(": "))
                 .append(quest.getTitle());
         int[] pr = compound.getIntArray("Progress");
         if (compound.getString("Type").equalsIgnoreCase("craft")) {
            ItemStack item = ItemStack.of(compound.getCompound("Item"));
            showMessage = (MutableComponent) item.getDisplayName();
         } else {
            showMessage = Component.translatable(compound.getString("TargetName"));
         }
         if (pr[0] >= pr[1]) { // is complete
            showMessage.append(Component.literal(" -"));
            showMessage.append(Component.translatable("quest.task." + compound.getString("Type") + ".0"));
         } else {
            showMessage.append(Component.literal(" = " + pr[0] + "/" + pr[1]));
         }
      }
      // get all toasts
      List visible = null; // List<ToastComponent.ToastInstance<?>>
      for (Field f : ToastComponent.class.getDeclaredFields()) {
         if (f.getType().getName().contains("ToastInstance")) {
            try {
               f.setAccessible(true);
               visible = (List) f.get(minecraft.getToasts());
            } catch (Exception e) {
               LogWriter.debug(e.toString());
            }
         }
      }
      if (visible == null) {
         CustomNpcs.debugData.end("Packets");
         return;
      }
      // change old or add new toast
      boolean found = false;
      for (Object obj : visible) {
         if (obj == null) { continue; }
         Field toast = obj.getClass().getDeclaredFields()[1];
         toast.setAccessible(true);
         try {
            if (toast.get(obj) instanceof GuiAchievement gui) {
               Field titleF = GuiAchievement.class.getDeclaredFields()[0];
               Field subtitleF = GuiAchievement.class.getDeclaredFields()[1];
               Field typeF = GuiAchievement.class.getDeclaredFields()[2];
               Field newDisplayF = GuiAchievement.class.getDeclaredFields()[4];
               titleF.setAccessible(true);
               typeF.setAccessible(true);

               String titleD = Util.instance.deleteColor((String) titleF.get(gui));
               int typeD = (int) typeF.get(gui);
               if (!titleD.equals(Util.instance.deleteColor(showTitle.getString())) || type != typeD) { continue; }
               titleF.set(gui, showTitle.getString());
               subtitleF.set(gui, showMessage.getString());
               newDisplayF.set(gui, true);
               found = true;
            }
         }
         catch (Exception ignored) { }
      }
      if (!found) { minecraft.getToasts().addToast(new GuiAchievement(showTitle, showMessage, type)); }
      CustomNpcs.debugData.end("Packets");
   }

}
