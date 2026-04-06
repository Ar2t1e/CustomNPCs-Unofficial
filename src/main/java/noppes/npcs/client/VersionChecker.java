package noppes.npcs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.ClickEvent.Action;
import noppes.npcs.CustomNpcs;

public class VersionChecker extends Thread {

   public void run() {
      MutableComponent messageVersion = Component.translatable("cnpcs.version");
      MutableComponent linkVersion = Component.translatable("click.here");
      linkVersion.setStyle(linkVersion.getStyle().withClickEvent(new ClickEvent(Action.OPEN_URL, "http://www.kodevelopment.nl/minecraft/" + CustomNpcs.MODID + "/")));

      MutableComponent messageScripters = Component.translatable("cnpcs.scripters");
      MutableComponent linkScripters = Component.translatable(((char) 167) + "9" + ((char) 167) + "nDiscord");
      linkScripters.setStyle(linkScripters.getStyle().withClickEvent(new ClickEvent(Action.OPEN_URL, "https://discord.gg/rgczwNV")));

      while(true) {
         if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.sendSystemMessage(messageVersion.append(linkVersion));
            Minecraft.getInstance().player.sendSystemMessage(messageScripters.append(linkScripters));
            break;
         }
      }
   }

}
