package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.ITextComponent;
import noppes.npcs.CustomNpcs;
import noppes.npcs.shared.common.util.LogWriter;
import noppes.npcs.api.handler.data.IQuest;
import noppes.npcs.client.gui.GuiAchievement;
import noppes.npcs.controllers.QuestController;
import noppes.npcs.shared.common.PacketBasic;
import noppes.npcs.util.Util;

import java.lang.reflect.Field;

public class PacketAchievement extends PacketBasic {

   protected static int channelId;
   private ITextComponent title;
   private ITextComponent message;
   private NBTTagCompound compound; // quest progress data:
   private int type;

   public PacketAchievement() {}

   public PacketAchievement(Component titleIn, Component messageIn, int typeIn, NBTTagCompound compoundIn) {
      title = titleIn.getParent();
      message = messageIn.getParent();
      type = typeIn;
      compound = compoundIn;
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      title = buf.readComponent();
      message = buf.readComponent();
      type = buf.readInt();
      compound = buf.readNbt();
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeComponent(title);
      buf.writeComponent(message);
      buf.writeInt(type);
      buf.writeNbt(compound);
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Minecraft minecraft = Minecraft.getMinecraft();
      Component showTitle = Component.empty().append(title);
      Component showMessage = Component.empty().append(message);
      // has quest
      if (compound.hasKey("questID", 3) && compound.getInteger("questID") >= 0) {
         IQuest quest = QuestController.instance.get(compound.getInteger("questID"));
         if (quest == null) {
            CustomNpcs.debugData.end("Packets");
            return;
         }
         showTitle = Component.translatable("quest.name")
                 .append(": " + quest.getTitle());
         int[] pr = compound.getIntArray("Progress");
         if (compound.getString("Type").equalsIgnoreCase("craft")) {
            ItemStack item = new ItemStack(compound.getCompoundTag("Item"));
            showMessage = Component.literal(item.getDisplayName());
         }
         else { showMessage = Component.translatable(compound.getString("TargetName")); }
         if (pr[0] >= pr[1]) { // is complete
            showMessage.append(" -");
            showMessage.append(Component.translatable("quest.task." + compound.getString("Type") + ".0"));
         }
         else { showMessage.appendText(" = " + pr[0] + "/" + pr[1]); }
      }
      // get all toasts
      Object[] visible = null; // GuiToast.ToastInstance<?>[]
      for (Field f : GuiToast.class.getDeclaredFields()) {
         if (f.getType().getName().contains("ToastInstance")) {
            try {
               f.setAccessible(true);
               visible = (Object[]) f.get(minecraft.getToastGui());
            }
            catch (Exception e) { LogWriter.debug(e.toString()); }
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
         Field toast = obj.getClass().getDeclaredFields()[0];
         toast.setAccessible(true);
         try {
            if (!(toast.get(obj) instanceof GuiAchievement)) { continue; }
            GuiAchievement achn = (GuiAchievement) toast.get(obj);
            Field titleF = GuiAchievement.class.getDeclaredFields()[3];
            Field typeF = GuiAchievement.class.getDeclaredFields()[4];
            titleF.setAccessible(true);
            typeF.setAccessible(true);
            String titleD = Util.instance.deleteColor((String) titleF.get(achn));
            int typeD = (int) typeF.get(achn);
            if (!titleD.equals(Util.instance.deleteColor(title.getFormattedText())) || type != typeD) { continue; }
            achn.setDisplayedText(showTitle.getParent(), showMessage.getParent());
            found = true;
         }
         catch (Exception ignored) { }
      }
      if (!found) { minecraft.getToastGui().add(new GuiAchievement(title, message, type)); }
      CustomNpcs.debugData.end("Packets");
   }

}
