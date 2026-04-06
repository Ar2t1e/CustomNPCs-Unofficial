package noppes.npcs.api.wrapper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;
import noppes.npcs.api.IDimension;

public class DimensionWrapper implements IDimension {

   private final ResourceLocation id;
   private final DimensionType type;

   public DimensionWrapper(ResourceLocation idIn, DimensionType typeIn) {
      id = idIn;
      type = typeIn;
   }

   @Override
   public String getId() {
      return id.toString();
   }

   @Override
   public String getEffectsLocation() {
      return type.effectsLocation().toString();
   }

}
