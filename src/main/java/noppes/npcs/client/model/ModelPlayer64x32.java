package noppes.npcs.client.model;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import noppes.npcs.entity.EntityNPCInterface;

public class ModelPlayer64x32<T extends EntityNPCInterface> extends PlayerModel<T> {

   public ModelPlayer64x32(ModelPart part) {
      super(part, false);
   }

}
