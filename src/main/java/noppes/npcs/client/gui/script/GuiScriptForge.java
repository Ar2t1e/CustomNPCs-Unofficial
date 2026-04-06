package noppes.npcs.client.gui.script;

import net.minecraft.nbt.CompoundTag;
import noppes.npcs.controllers.data.ForgeScriptData;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.server.SPacketScriptGet;

public class GuiScriptForge extends GuiScriptInterface {

   protected final ForgeScriptData script = new ForgeScriptData();

   public GuiScriptForge() {
      super(3);
      handler = script;
      Packets.sendServer(new SPacketScriptGet(type));
   }

   @Override
   public void setGuiData(CompoundTag compound) {
      script.load(compound);
      super.setGuiData(compound);
   }

   @Override
   public void save() {
      super.save();
      sendToServer(script.save(new CompoundTag()));
   }

}
