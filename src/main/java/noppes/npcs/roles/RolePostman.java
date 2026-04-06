package noppes.npcs.roles;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.containers.inventories.NpcMiscInventory;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.controllers.data.Line;
import noppes.npcs.controllers.data.PlayerData;
import noppes.npcs.entity.EntityNPCInterface;

public class RolePostman extends RoleInterface {

    public NpcMiscInventory inventory = new NpcMiscInventory(1);
    private final List<Player> recentlyChecked = new ArrayList<>();

    public RolePostman(EntityNPCInterface npc) {
        super(npc);
        type = RoleType.MAILMAN;
    }

    @Override
    public boolean aiShouldExecute() {
        if (npc.tickCount % 20 == 0) {
            List<Player> toCheck = npc.level().getEntitiesOfClass(Player.class, npc.getBoundingBox().inflate(10.0D, 10.0D, 10.0D));
            toCheck.removeAll(recentlyChecked);
            List<Player> listMax = npc.level().getEntitiesOfClass(Player.class, npc.getBoundingBox().inflate(20.0D, 20.0D, 20.0D));
            recentlyChecked.retainAll(listMax);
            recentlyChecked.addAll(toCheck);
            for (Player player : toCheck) {
                if (PlayerData.get(player).mailData.hasMail()) { npc.say(player, new Line("mail.player.has.letter")); }
            }
        }
        return false;
    }

    @Override
    public CompoundTag save(CompoundTag compound) {
        super.save(compound);
        compound.put("PostInv", inventory.save());
        return compound;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        type = RoleType.MAILMAN;
        inventory.load(compound.getCompound("PostInv"));
    }

    @Override
    public void interact(Player player) {
        NoppesUtilServer.openContainerGui((ServerPlayer) player, EnumGuiType.PlayerMailOpen, (buf) -> {
            buf.writeBoolean(true);
            buf.writeBoolean(true);
        });
    }

}
