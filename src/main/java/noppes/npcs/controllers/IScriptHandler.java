package noppes.npcs.controllers;

import java.util.List;
import java.util.Map;

import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.eventbus.api.Event;

public interface IScriptHandler {

   void runScript(String type, Event event);

   boolean isClient();

   boolean getEnabled();

   void setEnabled(boolean bo);

   String getLanguage();

   void setLanguage(String language);

   List<ScriptContainer> getScripts();

   MutableComponent noticeString(String type, Object event);

   Map<Long, String> getConsoleText();

   void clearConsole();

   boolean isEnabled();

   // New from Unofficial (BetaZavr)
   void clearConsoleText(Long key);

   void setLastInited(long timeMC);

   void init();

}
