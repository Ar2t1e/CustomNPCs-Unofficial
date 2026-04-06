package noppes.npcs.api.wrapper;

import noppes.npcs.api.IPos;
import noppes.npcs.api.IRayTrace;
import noppes.npcs.api.block.IBlock;

public class RayTraceWrapper implements IRayTrace {

   private final IBlock block;
   private final int sideHit;
   private final IPos pos;

   public RayTraceWrapper(IBlock blockIn, int sideHitIn) {
      block = blockIn;
      sideHit = sideHitIn;
      pos = block.getPos();
   }

   public IPos getPos() { return block.getPos(); }

   public IBlock getBlock() { return block; }

   public int getSideHit() { return sideHit; }
}
