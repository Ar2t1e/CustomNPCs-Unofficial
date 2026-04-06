package nikedemos.markovnames;

import java.util.HashMap;
import java.util.Map;

import nikedemos.markovnames.generators.MarkovAncientGreek;
import nikedemos.markovnames.generators.MarkovAztec;
import nikedemos.markovnames.generators.MarkovGenerator;
import nikedemos.markovnames.generators.MarkovJapanese;
import nikedemos.markovnames.generators.MarkovOldNorse;
import nikedemos.markovnames.generators.MarkovRoman;
import nikedemos.markovnames.generators.MarkovSaami;
import nikedemos.markovnames.generators.MarkovSlavic;
import nikedemos.markovnames.generators.MarkovWelsh;
import noppes.npcs.shared.common.util.LogWriter;

public class Main {

   public static HashMap<String, MarkovGenerator> GENERATORS = new HashMap<>();

   public static void main(String[] args) {
      GENERATORS.put("ROMAN", new MarkovRoman(3));
      GENERATORS.put("JAPANESE", new MarkovJapanese(4));
      GENERATORS.put("SLAVIC", new MarkovSlavic(3));
      GENERATORS.put("WELSH", new MarkovWelsh(3));
      GENERATORS.put("SAAMI", new MarkovSaami(3));
      GENERATORS.put("OLDNORSE", new MarkovOldNorse(4));
      GENERATORS.put("ANCIENTGREEK", new MarkovAncientGreek(3));
      GENERATORS.put("AZTEC", new MarkovAztec(3));
      for (Map.Entry<String, MarkovGenerator> pair : Main.GENERATORS.entrySet()) {
         LogWriter.info("===" + pair.getKey() + "===");
         for (int i = 0; i < 16; ++i) {
            if (i == 0) { LogWriter.info("GENTLEMEN-----------"); }
            int gender = (i < 8) ? 1 : 2;
            String random_name = pair.getValue().fetch(gender);
            LogWriter.info(random_name);
            if (i == 15) { LogWriter.info("\n"); }
            else if (i == 7) { LogWriter.info("LADIES--------------"); }
         }
      }
   }
}
