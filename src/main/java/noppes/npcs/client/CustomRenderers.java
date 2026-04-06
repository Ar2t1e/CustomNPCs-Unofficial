package noppes.npcs.client;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import noppes.npcs.CustomBlocks;
import noppes.npcs.CustomEntities;
import noppes.npcs.CustomNpcs;
import noppes.npcs.client.model.*;
import noppes.npcs.client.renderer.RenderCustomNpc;
import noppes.npcs.client.renderer.RenderNPCInterface;
import noppes.npcs.client.renderer.RenderNPCPony;
import noppes.npcs.client.renderer.RenderNpcCrystal;
import noppes.npcs.client.renderer.RenderNpcDragon;
import noppes.npcs.client.renderer.RenderNpcSlime;
import noppes.npcs.client.renderer.RenderProjectile;
import noppes.npcs.client.renderer.blocks.*;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
   bus = Bus.MOD,
   modid = CustomNpcs.MODID,
   value = {Dist.CLIENT}
)
public class CustomRenderers {

   @OnlyIn(Dist.CLIENT)
   @SubscribeEvent
   @SuppressWarnings({"unchecked", "rawtypes"})
   public static void registerEntityRenderer(RegisterRenderers event) {
      CustomNpcs.debugData.start("Mod");
      // mod entities
      event.registerEntityRenderer(CustomEntities.entityNpcPony, manager -> new RenderNPCPony(manager, new ModelPony<>()));
      event.registerEntityRenderer(CustomEntities.entityNpcCrystal, manager -> new RenderNpcCrystal(manager, new ModelNpcCrystal<>()));
      event.registerEntityRenderer(CustomEntities.entityNpcDragon, manager -> new RenderNpcDragon(manager, new ModelNpcDragon<>(), 0.5F));
      event.registerEntityRenderer(CustomEntities.entityNpcSlime, manager -> new RenderNpcSlime(manager, new ModelNpcSlime<>(16), new ModelNpcSlime<>(0), 0.25F));
      event.registerEntityRenderer(CustomEntities.entityProjectile, manager -> new RenderProjectile(manager));
      event.registerEntityRenderer(CustomEntities.entityCustomNpc, manager -> new RenderCustomNpc(manager, new ModelNpcAlt(manager.getModelSet().bakeLayer(ModelLayers.PLAYER), false)));
      event.registerEntityRenderer(CustomEntities.entityNPC64x32, manager -> new RenderCustomNpc(manager, new ModelPlayer64x32(manager.getModelSet().bakeLayer(ModelLayers.PLAYER))));
      event.registerEntityRenderer(CustomEntities.entityNPCGolem, manager -> new RenderNPCInterface(manager, new ModelNPCGolem(), 0.0F));
      event.registerEntityRenderer(CustomEntities.entityNpcAlex, manager -> new RenderCustomNpc(manager, new ModelNpcAlt(manager.getModelSet().bakeLayer(ModelLayers.PLAYER_SLIM), true)));
      event.registerEntityRenderer(CustomEntities.entityNpcClassicPlayer, manager -> new RenderCustomNpc(manager, new ModelClassicPlayer<>(manager.getModelSet().bakeLayer(ModelLayers.PLAYER), 0.0F)));
      // mod blocks
      event.registerBlockEntityRenderer(CustomBlocks.tile_anvil, BlockCarpentryBenchRenderer::new);
      event.registerBlockEntityRenderer(CustomBlocks.tile_mailbox, BlockMailboxRenderer::new);
      event.registerBlockEntityRenderer(CustomBlocks.tile_scripted, BlockScriptedRenderer::new);
      event.registerBlockEntityRenderer(CustomBlocks.tile_scripteddoor, BlockDoorRenderer::new);
      event.registerBlockEntityRenderer(CustomBlocks.tile_copy, BlockCopyRenderer::new);
      event.registerBlockEntityRenderer(CustomBlocks.tile_builder, BlockBuilderRenderer::new);
      // custom blocks
      if (CustomBlocks.tile_custom_portal != null) { event.registerBlockEntityRenderer(CustomBlocks.tile_custom_portal, BlockPortalRenderer::new); }
      if (CustomBlocks.tile_custom_chest != null) { event.registerBlockEntityRenderer(CustomBlocks.tile_custom_chest, BlockChestRenderer::new); }
      CustomNpcs.debugData.end("Mod");
   }

}
