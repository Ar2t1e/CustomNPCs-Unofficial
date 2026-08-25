package noppes.npcs;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import noppes.npcs.entity.*;
import noppes.npcs.entity.old.*;

public class CustomEntities {

    private static int newEntityStartId = 0;

    @SubscribeEvent
    public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
        EntityEntry[] entries = {
                registerNpc(EntityNPCHumanMale.class, "npchumanmale"),
                registerNpc(EntityNPCVillager.class, "npcvillager"),
                registerNpc(EntityNPCHumanFemale.class, "npchumanfemale"),
                registerNpc(EntityNPCDwarfMale.class, "npcdwarfmale"),
                registerNpc(EntityNPCFurryMale.class, "npcfurrymale"),
                registerNpc(EntityNpcMonsterMale.class, "npczombiemale"),
                registerNpc(EntityNpcMonsterFemale.class, "npczombiefemale"),
                registerNpc(EntityNpcSkeleton.class, "npcskeleton"),
                registerNpc(EntityNPCDwarfFemale.class, "npcdwarffemale"),
                registerNpc(EntityNPCFurryFemale.class, "npcfurryfemale"),
                registerNpc(EntityNPCOrcMale.class, "npcorcfmale"),
                registerNpc(EntityNPCOrcFemale.class, "npcorcfemale"),
                registerNpc(EntityNPCElfMale.class, "npcelfmale"),
                registerNpc(EntityNPCElfFemale.class, "npcelffemale"),
                registerNpc(EntityNpcEnderchibi.class, "npcenderchibi"),
                registerNpc(EntityNpcNagaMale.class, "npcnagamale"),
                registerNpc(EntityNpcNagaFemale.class, "npcnagafemale"),
                registerNpc(EntityNPCEnderman.class, "npcenderman"),

                registerNpc(EntityNpcPony.class, "npcpony"),
                registerNpc(EntityNpcCrystal.class, "npccrystal"),
                registerNpc(EntityNpcSlime.class, "npcslime"),
                registerNpc(EntityNpcDragon.class, "npcdragon"),
                registerNpc(EntityNPCGolem.class, "npcgolem"),
                registerNpc(EntityCustomNpc.class, "customnpc"),
                registerNpc(EntityNPC64x32.class, "customnpc64x32"),
                registerNpc(EntityNpcAlex.class, "customnpcalex"),
                registerNpc(EntityNpcClassicPlayer.class, "customnpcclassic"),

                registerNewEntity("customnpcchairmount", 10, false).entity(EntityChairMount.class).build(),
                registerNewEntity("customnpcprojectile", 3, true).entity(EntityProjectile.class).build() };
        event.getRegistry().registerAll(entries);
    }

    private <E extends Entity> EntityEntryBuilder<E> registerNewEntity(String name, int update, boolean velocity) {
        EntityEntryBuilder<E> builder = EntityEntryBuilder.create();
        ResourceLocation registryName = new ResourceLocation(CustomNpcs.MODID, name);
        return builder.id(registryName, newEntityStartId++).name(name).tracker(64, update, velocity);
    }

    private EntityEntry registerNpc(Class<? extends Entity> cl, String name) {
        return registerNewEntity(name, 3, true).entity(cl).build();
    }

}
