package noppes.npcs;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType.Builder;
import net.minecraft.world.entity.EntityType.EntityFactory;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import noppes.npcs.entity.EntityChairMount;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPC64x32;
import noppes.npcs.entity.EntityNPCGolem;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.EntityNpcAlex;
import noppes.npcs.entity.EntityNpcClassicPlayer;
import noppes.npcs.entity.EntityNpcCrystal;
import noppes.npcs.entity.EntityNpcDragon;
import noppes.npcs.entity.EntityNpcPony;
import noppes.npcs.entity.EntityNpcSlime;
import noppes.npcs.entity.EntityProjectile;

@EventBusSubscriber(bus = Bus.MOD, modid = CustomNpcs.MODID)
public class CustomEntities {

   public static EntityType<? extends EntityNPCInterface> entityNpcPony;
   public static EntityType<? extends EntityNPCInterface> entityNpcCrystal;
   public static EntityType<? extends EntityNPCInterface> entityNpcSlime;
   public static EntityType<? extends EntityNPCInterface> entityNpcDragon;
   public static EntityType<? extends EntityNPCInterface> entityNPCGolem;
   public static EntityType<? extends EntityNPCInterface> entityCustomNpc;
   public static EntityType<? extends EntityNPCInterface> entityNPC64x32;
   public static EntityType<? extends EntityNPCInterface> entityNpcAlex;
   public static EntityType<? extends EntityNPCInterface> entityNpcClassicPlayer;
   public static EntityType<? extends EntityNPCInterface> entityChairMount;
   public static EntityType<? extends ThrowableProjectile> entityProjectile;
   private static final List<EntityType<? extends LivingEntity>> types = new ArrayList<>();

   @SubscribeEvent
   public static void registerEntities(RegisterEvent event) {
      if (event.getRegistryKey() == Keys.ENTITY_TYPES && event.getForgeRegistry() != null) {
         CustomNpcs.debugData.start("Mod");
         types.clear();
         registerNpc(event.getForgeRegistry(),"npcpony", EntityNpcPony::new);
         registerNpc(event.getForgeRegistry(), "npccrystal", EntityNpcCrystal::new);
         registerNpc(event.getForgeRegistry(), "npcslime", EntityNpcSlime::new);
         registerNpc(event.getForgeRegistry(), "npcdragon", EntityNpcDragon::new);
         registerNpc(event.getForgeRegistry(), "npcgolem", EntityNPCGolem::new);
         registerNpc(event.getForgeRegistry(), "customnpc", EntityCustomNpc::new);
         registerNpc(event.getForgeRegistry(), "customnpc64x32", EntityNPC64x32::new);
         registerNpc(event.getForgeRegistry(), "customnpcalex", EntityNpcAlex::new);
         registerNpc(event.getForgeRegistry(), "customnpcclassic", EntityNpcClassicPlayer::new);
         registerNewEntity(event.getForgeRegistry(), "customnpcchairmount", EntityChairMount::new, 10, false, 0.001F, 0.001F);
         registerNewEntity(event.getForgeRegistry(), "customnpcprojectile", EntityProjectile::new, 20, true, 0.5F, 0.5F);
         CustomNpcs.debugData.end("Mod");
      }
   }

   @SubscribeEvent
   public static void attribute(EntityAttributeCreationEvent event) {
      CustomNpcs.debugData.start("Mod");
      for (EntityType<? extends LivingEntity> entityType : types) {
         event.put(entityType, EntityNPCInterface.createMobAttributes().build());
      }
      CustomNpcs.debugData.end("Mod");
   }

   @SuppressWarnings("unchecked")
   private static <T extends EntityNPCInterface> void registerNpc(IForgeRegistry<EntityType<?>> registry, String name, EntityFactory<T> factoryIn) {
      Builder<?> builder = Builder.of(factoryIn, MobCategory.CREATURE);
      builder.setTrackingRange(10);
      builder.setUpdateInterval(3);
      builder.setShouldReceiveVelocityUpdates(false);
      builder.clientTrackingRange(10);
      builder.sized(1.0F, 1.0F);
      ResourceLocation registryName = new ResourceLocation(CustomNpcs.MODID, name);
      EntityType<?> type = builder.build(registryName.toString());
      types.add((EntityType<? extends LivingEntity>) type);
      registry.register(registryName, type);
      switch (name) {
         case "npcpony": entityNpcPony = (EntityType<? extends EntityNPCInterface>) type; break;
         case "npccrystal": entityNpcCrystal = (EntityType<? extends EntityNPCInterface>) type; break;
         case "npcslime": entityNpcSlime = (EntityType<? extends EntityNPCInterface>) type; break;
         case "npcdragon": entityNpcDragon = (EntityType<? extends EntityNPCInterface>) type; break;
         case "npcgolem": entityNPCGolem = (EntityType<? extends EntityNPCInterface>) type; break;
         case "customnpc": entityCustomNpc = (EntityType<? extends EntityNPCInterface>) type; break;
         case "customnpc64x32": entityNPC64x32 = (EntityType<? extends EntityNPCInterface>) type; break;
         case "customnpcalex": entityNpcAlex = (EntityType<? extends EntityNPCInterface>) type; break;
         case "customnpcclassic": entityNpcClassicPlayer = (EntityType<? extends EntityNPCInterface>) type; break;
      }
      if (CustomNpcs.FixUpdateFromPre_1_12) {
         registryName = ResourceLocation.tryParse(CustomNpcs.MODID + "." + name);
         if (registryName != null) { registry.register(registryName, builder.build(registryName.toString())); }
      }
   }

   @SuppressWarnings("unchecked")
   private static <T extends Entity> void registerNewEntity(IForgeRegistry<EntityType<?>> registry, String name, EntityFactory<T> factoryIn, int update, boolean velocity, float width, float height) {
      Builder<?> builder = Builder.of(factoryIn, MobCategory.MISC);
      builder.setTrackingRange(64);
      builder.setUpdateInterval(update);
      builder.setShouldReceiveVelocityUpdates(velocity);
      builder.sized(width, height);
      builder.clientTrackingRange(4);
      ResourceLocation registryName = new ResourceLocation(CustomNpcs.MODID, name);
      EntityType<?> type = builder.build(registryName.toString());
      registry.register(registryName, type);
      switch (name) {
         case "customnpcchairmount": entityChairMount = (EntityType<? extends EntityNPCInterface>) type; break;
         case "customnpcprojectile": entityProjectile = (EntityType<? extends ThrowableProjectile>) type; break;
      }
   }
}
