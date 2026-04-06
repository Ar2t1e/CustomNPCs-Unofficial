package noppes.npcs.mixin.client.model.geom;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import noppes.npcs.client.ClientProxy;
import noppes.npcs.client.parts.ModelPartConfig;
import noppes.npcs.constants.EnumParts;
import noppes.npcs.mixin.client.renderer.entity.layers.IHumanoidArmorLayerMixin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModelPart.class, priority = 498)
public class ModelPartMixin {

   @Unique
   public ModelPartConfig cnpcs$config;

   @Inject(
           at = {@At("HEAD")},
           method = {"translateAndRotate"}
   )
   private void translateAndRotatePre(PoseStack mStack, CallbackInfo callbackInfo) {
      cnpcs$config = cnpcs$getConfig();
      if (cnpcs$config != null) {
         mStack.translate(cnpcs$config.transX, cnpcs$config.transY, cnpcs$config.transZ);
      }
   }

   @Inject(
           at = {@At("TAIL")},
           method = {"translateAndRotate"}
   )
   private void translateAndRotatePost(PoseStack mStack, CallbackInfo callbackInfo) {
      cnpcs$config = cnpcs$getConfig();
      if (cnpcs$config != null) {
         mStack.scale(cnpcs$config.scaleX, cnpcs$config.scaleY, cnpcs$config.scaleZ);
      }
   }

   @Unique
   private ModelPartConfig cnpcs$getConfig() {
      if (ClientProxy.data == null) {
         return null;
      }
      HumanoidModel<?> inner = ((IHumanoidArmorLayerMixin<?, ?>) ClientProxy.armorLayer).getInnerModel();
      HumanoidModel<?> outer = ((IHumanoidArmorLayerMixin<?, ?>) ClientProxy.armorLayer).getOuterModel();
      ModelPart model = (ModelPart) (Object) this;
      if (model != ClientProxy.playerModel.body && model != ClientProxy.playerModel.jacket && model != outer.body && model != inner.body) {
         if (model != ClientProxy.playerModel.head && model != ClientProxy.playerModel.hat && model != outer.head) {
            if (model != ClientProxy.playerModel.leftLeg && model != ClientProxy.playerModel.leftPants && model != outer.leftLeg && model != inner.leftLeg) {
               if (model != ClientProxy.playerModel.rightLeg && model != ClientProxy.playerModel.rightPants && model != outer.rightLeg && model != inner.rightLeg) {
                  if (model != ClientProxy.playerModel.leftArm && model != ClientProxy.playerModel.leftSleeve && model != outer.leftArm) {
                     return model != ClientProxy.playerModel.rightArm && model != ClientProxy.playerModel.rightSleeve && model != outer.rightArm ? null : ClientProxy.data.getPartConfig(EnumParts.ARM_RIGHT);
                  } else {
                     return ClientProxy.data.getPartConfig(EnumParts.ARM_LEFT);
                  }
               } else {
                  return ClientProxy.data.getPartConfig(EnumParts.LEG_RIGHT);
               }
            } else {
               return ClientProxy.data.getPartConfig(EnumParts.LEG_LEFT);
            }
         } else {
            return ClientProxy.data.getPartConfig(EnumParts.HEAD);
         }
      } else {
         return ClientProxy.data.getPartConfig(EnumParts.BODY);
      }
   }

}
