package noppes.npcs.shared.common.util;

import java.awt.*;

public class ColorUtil {

   public static NopVector3f colorToRgb(int color) {
      return new NopVector3f(new float[]{(float)(color >> 16 & 255) / 255.0F, (float)(color >> 8 & 255) / 255.0F, (float)(color & 255) / 255.0F});
   }

   public static int rgbToColor(NopVector3f color) {
      int r = (int)(color.x * 255.0F) << 16;
      int g = (int)(color.y * 255.0F) << 8;
      int b = (int)(color.z * 255.0F);
      return r + g + b;
   }

   @SuppressWarnings("all")
   public static String colorToHex(int color) {
      StringBuilder str = new StringBuilder(Integer.toHexString(color));
      while (str.length() < 6) { str.insert(0, "0"); }
      return str.toString();
   }

   @SuppressWarnings("all")
   public static int hexToColor(String hex) {
      try {
         return Integer.parseInt(hex, 16);
      } catch (NumberFormatException var2) {
         return new Color(0xFFFFFF).getRGB();
      }
   }

}
