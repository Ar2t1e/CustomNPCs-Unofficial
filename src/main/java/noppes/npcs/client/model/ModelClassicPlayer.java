package noppes.npcs.client.model;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import noppes.npcs.entity.EntityNpcClassicPlayer;
import org.jetbrains.annotations.NotNull;

public class ModelClassicPlayer<T extends EntityNpcClassicPlayer> extends PlayerModel<T> {

   public ModelClassicPlayer(ModelPart model, float ignoredScale) {
      super(model, false);
   }

   public void setupAnim(@NotNull T entity, float par1, float limbSwingAmount, float par3, float par4, float par5) {
      super.setupAnim(entity, par1, limbSwingAmount, par3, par4, par5);
      float j = 2.0F;
      if (entity.isSprinting()) {
         j = 1.0F;
      }
      rightArm.xRot += Mth.cos(par1 * 0.6662F + 3.1415927F) * j * limbSwingAmount;
      leftArm.xRot += Mth.cos(par1 * 0.6662F) * j * limbSwingAmount;
      leftArm.zRot += (Mth.cos(par1 * 0.2812F) - 1.0F) * limbSwingAmount;
      rightArm.zRot += (Mth.cos(par1 * 0.2312F) + 1.0F) * limbSwingAmount;
      this.leftSleeve.xRot = this.leftArm.xRot;
      this.leftSleeve.yRot = this.leftArm.yRot;
      this.leftSleeve.zRot = this.leftArm.zRot;
      this.rightSleeve.xRot = this.rightArm.xRot;
      this.rightSleeve.yRot = this.rightArm.yRot;
      this.rightSleeve.zRot = this.rightArm.zRot;
   }

}
