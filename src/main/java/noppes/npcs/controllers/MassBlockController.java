package noppes.npcs.controllers;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import noppes.npcs.controllers.data.BlockData;
import noppes.npcs.entity.EntityNPCInterface;

public class MassBlockController {

   public interface IMassBlock {

      EntityNPCInterface getNpc();

      int getRange();

      void processed(List<BlockData> blocks);

      void setRange(int range);
   }

   private static final Queue<MassBlockController.IMassBlock> queue = new LinkedList<>();

   public static void Queue(MassBlockController.IMassBlock imb) { queue.add(imb); }

   public static void Update() {
      if (!queue.isEmpty()) {
         MassBlockController.IMassBlock imb = queue.remove();
         Level level = imb.getNpc().level();
         BlockPos pos = imb.getNpc().blockPosition();
         int range = imb.getRange();
         List<BlockData> list = new ArrayList<>();
         for(int x = -range; x < range; ++x) {
            for(int z = -range; z < range; ++z) {
               if (level.isLoaded(new BlockPos(x + pos.getX(), 64, z + pos.getZ()))) {
                  for(int y = 0; y < range; ++y) {
                     BlockPos blockPos = pos.offset(x, y - range / 2, z);
                     list.add(new BlockData(blockPos, level.getBlockState(blockPos), null));
                  }
               }
            }
         }
         imb.processed(list);
      }
   }

}
