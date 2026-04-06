package nikedemos.markovnames.generators;

import net.minecraft.network.chat.Component;
import nikedemos.markovnames.MarkovDictionary;

public class MarkovOldNorse extends MarkovGenerator {

   public MarkovOldNorse(int seqlen) {
      markov = new MarkovDictionary("old_norse_bothgenders.txt", seqlen);
      name = Component.translatable("markov.oldNorse").toString();
   }

   @Override
   public String fetch(int gender) { return markov.generateWord(); }

}
