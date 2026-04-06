package noppes.npcs;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import noppes.npcs.items.ItemScripted;

public class ScriptItemEventHandler {

   @SubscribeEvent
   public void cnpcEntityJoinLevel(EntityJoinLevelEvent event) {
      if (event.getLevel().isClientSide || !(event.getEntity() instanceof ItemEntity entity)) { return; }
      CustomNpcs.debugData.start(event.getEntity());
      ItemStack stack = entity.getItem();
      if (!stack.isEmpty() && stack.getItem() == CustomItems.scripted_item && EventHooks.onScriptItemSpawn(ItemScripted.GetWrapper(stack), entity)) {
         event.setCanceled(true);
      }
      CustomNpcs.debugData.end(event.getEntity());
   }

   @SubscribeEvent
   public void cnpcItemToss(ItemTossEvent event) {
      if (event.getEntity().level().isClientSide) { return; }
      CustomNpcs.debugData.start(event.getEntity());
      ItemEntity entity = event.getEntity();
      ItemStack stack = entity.getItem();
      if (!stack.isEmpty() && stack.getItem() == CustomItems.scripted_item && EventHooks.onScriptItemTossed(ItemScripted.GetWrapper(stack), event.getPlayer(), entity)) { event.setCanceled(true); }
      CustomNpcs.debugData.end(event.getEntity());
   }

   @SubscribeEvent
   public void cnpcItemPickup(EntityItemPickupEvent event) {
      if (event.getEntity().level().isClientSide) { return; }
      CustomNpcs.debugData.start(event.getEntity());
      ItemEntity entity = event.getItem();
      ItemStack stack = entity.getItem();
      if (!stack.isEmpty() && stack.getItem() == CustomItems.scripted_item) {
         EventHooks.onScriptItemPickedUp(ItemScripted.GetWrapper(stack), event.getEntity(), entity);
      }
      CustomNpcs.debugData.end(event.getEntity());
   }

}
