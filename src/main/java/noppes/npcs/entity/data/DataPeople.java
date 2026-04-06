package noppes.npcs.entity.data;

import noppes.npcs.CustomNpcs;

import java.util.Random;

public enum DataPeople {

   Noppes("Noppes", "Creator", true),
   Goodbird("Goodbird", "Porter & Fixer", true),
   BetaZavr("BetaZavr", "Porter & Fixer", true),
   Dati("Dati", "Patreon", true),
   Animekin("Animekin", "Patreon", true),
   Vin0m("Vin0m", "Patreon", true),
   Birb("Birb", "Patreon", true),
   Flashback("Flashback", "Patreon", true),
   Ronan("Ronan", "Patreon", true),
   Shivaxi("Shivaxi", "Patreon", true),
   GreatOrator("GreatOrator", "Patreon", true),
   Aphmau("Aphmau", "Patreon", true),
   Kithoras("Kithoras", "Patreon", false),
   Daniel_N("Daniel N", "Patreon", true),
   G1RCraft("G1RCraft", "Patreon", true),
   Joanie_H("Joanie H", "Patreon", true),
   Jaffra("Jaffra", "Patreon", true),
   Orphie("Orphie", "Patreon", true),
   PPap("PPap", "Patreon", true),
   RED9936("RED9936", "Patreon", true),
   NekoTune("NekoTune", "Patreon", true),
   JusCallMeNico("JusCallMeNico", "Patreon", true);

   private static final Random r = new Random();
   public final String name;
   public final String title;
   public final String skin;

   DataPeople(String nameIn, String titleIn, boolean hasSkin) {
      name = nameIn;
      title = titleIn;
      if (!hasSkin) { skin = ""; }
      else { skin = CustomNpcs.MODID + ":textures/entity/importantpeople/" + nameIn.toLowerCase().replace(" ", "_") + ".png"; }
   }

   public static DataPeople get() { return values()[r.nextInt(values().length)]; }

}
