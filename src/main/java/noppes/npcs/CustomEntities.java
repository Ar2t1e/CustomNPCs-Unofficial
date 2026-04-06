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
        EntityEntry[] entries = { this.registerNpc(EntityNPCHumanMale.class, "npchumanmale"),
                this.registerNpc(EntityNPCVillager.class, "npcvillager"),
                this.registerNpc(EntityNpcPony.class, "npcpony"),
                this.registerNpc(EntityNPCHumanFemale.class, "npchumanfemale"),
                this.registerNpc(EntityNPCDwarfMale.class, "npcdwarfmale"),
                this.registerNpc(EntityNPCFurryMale.class, "npcfurrymale"),
                this.registerNpc(EntityNpcMonsterMale.class, "npczombiemale"),
                this.registerNpc(EntityNpcMonsterFemale.class, "npczombiefemale"),
                this.registerNpc(EntityNpcSkeleton.class, "npcskeleton"),
                this.registerNpc(EntityNPCDwarfFemale.class, "npcdwarffemale"),
                this.registerNpc(EntityNPCFurryFemale.class, "npcfurryfemale"),
                this.registerNpc(EntityNPCOrcMale.class, "npcorcfmale"),
                this.registerNpc(EntityNPCOrcFemale.class, "npcorcfemale"),
                this.registerNpc(EntityNPCElfMale.class, "npcelfmale"),
                this.registerNpc(EntityNPCElfFemale.class, "npcelffemale"),
                this.registerNpc(EntityNpcCrystal.class, "npccrystal"),
                this.registerNpc(EntityNpcEnderchibi.class, "npcenderchibi"),
                this.registerNpc(EntityNpcNagaMale.class, "npcnagamale"),
                this.registerNpc(EntityNpcNagaFemale.class, "npcnagafemale"),
                this.registerNpc(EntityNpcSlime.class, "NpcSlime"),
                this.registerNpc(EntityNpcDragon.class, "NpcDragon"),
                this.registerNpc(EntityNPCEnderman.class, "npcEnderman"),
                this.registerNpc(EntityNPCGolem.class, "npcGolem"),
                this.registerNpc(EntityCustomNpc.class, "CustomNpc"),
                this.registerNpc(EntityNPC64x32.class, "CustomNpc64x32"),
                this.registerNpc(EntityNpcAlex.class, "CustomNpcAlex"),
                this.registerNpc(EntityNpcClassicPlayer.class, "CustomNpcClassic"),
                this.registerNewEntity("CustomNpcChairMount", 10, false).entity(EntityChairMount.class).build(),
                this.registerNewEntity("CustomNpcProjectile", 3, true).entity(EntityProjectile.class).build() };
        event.getRegistry().registerAll(entries);
    }

    private <E extends Entity> EntityEntryBuilder<E> registerNewEntity(String name, int update, boolean velocity) {
        EntityEntryBuilder<E> builder = EntityEntryBuilder.create();
        ResourceLocation registryName = new ResourceLocation(CustomNpcs.MODID, name);
        return builder.id(registryName, newEntityStartId++).name(name).tracker(64, update, velocity);
    }

    private EntityEntry registerNpc(Class<? extends Entity> cl, String name) {
        return this.registerNewEntity(name, 3, true).entity(cl).build();
    }

}
