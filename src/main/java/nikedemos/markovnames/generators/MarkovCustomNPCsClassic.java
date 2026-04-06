package nikedemos.markovnames.generators;

import nikedemos.markovnames.MarkovDictionary;
import noppes.npcs.CustomNpcs;

public class MarkovCustomNPCsClassic extends MarkovGenerator {

   public MarkovCustomNPCsClassic(int seqlen) {
      markov = new MarkovDictionary(CustomNpcs.MODID + "_classic.txt", seqlen);
   }

   public String fetch(int gender) { return markov.generateWord(); }

}
