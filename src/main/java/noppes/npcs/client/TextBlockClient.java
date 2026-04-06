package noppes.npcs.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import noppes.npcs.entity.data.TextBlock;
import noppes.npcs.shared.client.gui.util.NoppesStringUtils;
import noppes.npcs.util.Util;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TextBlockClient extends TextBlock {

   public int color;
   private String name;
   private CommandSource sender;

   private final Style style;
   private final int size;
   public Entity entity;
   public String text;

   // New from Unofficial BetaZavr
   public TextBlockClient(CommandSource senderIn, String text, int lineWidth, int colorIn, Entity entityIn, Object... obs) {
      this(text, lineWidth, false, obs);
      color = colorIn;
      sender = senderIn;
      entity = entityIn;
   }

   public TextBlockClient(String textIn, int lineWidth, boolean mcFont, Object... obs) {
      color = new Color(0xE0E0E0).getRGB();
      style = Style.EMPTY;
      text = NoppesStringUtils.formatText(textIn, obs);
      text = text.replace("\n", " \n ");
      text = text.replace("\r", " \r ");
      size = Util.instance.deleteColor(text).length();
      resetWidth(lineWidth, mcFont);
   }

   private void addLine(String text) {
      lines.add(Component.literal(text).setStyle(style));
   }

   public String getName() {
      if (sender instanceof Entity e) { return e.getDisplayName().getString(); }
      return name;
   }

   public void resetWidth(int lineWidth, boolean mcFont) {
      String line = "";
      final java.util.List<String> tempList = getStrings();
      String[] words = tempList.toArray(new String[0]);
      Font font = Minecraft.getInstance().font;

      String language = Minecraft.getInstance().getLanguageManager().getSelected();
      if (language.startsWith("zh_") || language.startsWith("ja_")) {
         for(int i = 0; i < text.length(); ++i) {
            line = line + text.charAt(i);
            if ((mcFont ? font.width(line) : ClientProxy.Font.width(line)) > lineWidth) {
               addLine(line);
               line = "";
            }
         }
      }
      else {
         String color = ChatFormatting.RESET.toString();
         for (String word : words) {
            if (word.isEmpty()) { continue; }
            if (word.length() == 1) {
               char c = word.charAt(0);
               if (c == '\r' || c == '\n') {
                  addLine(color + line);
                  color = Util.instance.getLastColor(color, line);
                  line = "";
                  continue;
               }
            }
            String newLine = line + word;
            int widthLine = (mcFont ? font.width(newLine) : ClientProxy.Font.width(newLine));
            if (widthLine > lineWidth && !line.isEmpty()) {
               addLine(color + line);
               color = Util.instance.getLastColor(color, line);
               line = word;
            }
            else { line = newLine; }
         }
         if (!line.isEmpty()) {
            addLine(color + line);
         }
      }
   }

   private java.util.List<String> getStrings() {
      String tempText = text;
      List<String> tempList = new ArrayList<>();
      int fm;
      while (true) {
         fm = -1;
         String corr = "" + ((char) 9) + ((char) 10) + " ()[]{}.,<>:;+-*\\/\"";
         for (int i = 0; i < corr.length(); i++) {
            int found = tempText.indexOf("" + corr.charAt(i));
            if (found != -1 && (found < fm || fm == -1)) { fm = found; }
         }
         if (fm >= 0) {
            String subWorld = tempText.substring(0, fm + 1);
            tempList.add(subWorld);
            tempText = tempText.substring(fm + 1);
         }
         else { break; }
      }
      tempList.add(tempText);
      return tempList;
   }

   public int size() { return size; }

}
