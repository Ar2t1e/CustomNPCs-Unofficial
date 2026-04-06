package noppes.npcs.mixin.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import noppes.npcs.CustomItems;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemModelShaper.class, priority = 498)
public class ItemModelMesherMixin {

   @Inject(
      at = {@At("HEAD")},
      method = {"getItemModel*"},
      cancellable = true
   )
   public void getModel(ItemStack itemStack, CallbackInfoReturnable<BakedModel> cir) {
      if (itemStack.getItem() == CustomItems.scripted_item) {
         ItemScriptedWrapper scriptedWrapper = (ItemScriptedWrapper) itemStack.getCapability(ItemStackWrapper.ITEMSCRIPTEDDATA_CAPABILITY, null).orElse(ItemStackWrapper.AIR);
         if (scriptedWrapper == ItemStackWrapper.AIR) { return; }
         Item item = null;
         if (scriptedWrapper.texture != null) {
            item = ForgeRegistries.ITEMS.getValue(scriptedWrapper.texture);
         }
         if (item == null) {
            item = CustomItems.scripted_item;
         }
         BakedModel model = Minecraft.getInstance().getItemRenderer().getItemModelShaper().getItemModel(item);
         if (model != null) {
            cir.setReturnValue(model);
            cir.cancel();
         }
      }
   }

}
