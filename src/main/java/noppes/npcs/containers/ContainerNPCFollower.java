package noppes.npcs.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleFollower;

import javax.annotation.Nonnull;

public class ContainerNPCFollower  extends ContainerNpcInterface {

    public final InventoryNPC currencyMatrix;
    public final RoleFollower role;

    public ContainerNPCFollower(EntityPlayer player, int entityId) {
        super(player);
        EntityNPCInterface npc = (EntityNPCInterface) player.world.getEntityByID(entityId);
        if (npc != null) { role = (RoleFollower) npc.role; }
        else { role = new RoleFollower(null); }
        currencyMatrix = new InventoryNPC("currency", 1, this);
        addSlotToContainer(new SlotNpcMercenaryCurrency(role, currencyMatrix, 0, 26, 9));
        for(int j1 = 0; j1 < 9; ++j1) {
            addSlotToContainer(new Slot(player.inventory, j1, 8 + j1 * 18, 142));
        }
    }

    @Override
    public void onContainerClosed(@Nonnull EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if (!playerIn.world.isRemote) {
            ItemStack itemstack = currencyMatrix.removeStackFromSlot(0);
            if (!NoppesUtilServer.isItemStackNull(itemstack)) { playerIn.dropItem(itemstack, false); }
        }
    }

}

