package noppes.npcs.containers;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.wrapper.ContainerWrapper;
import org.jetbrains.annotations.NotNull;

public class ContainerNpcInterface extends AbstractContainerMenu {

   protected final int posX;
   protected final int posZ;
   protected final IContainer scriptContainer;
   public final Player player;

   public ContainerNpcInterface(MenuType type, int containerId, Inventory playerInventory) {
      super(type, containerId);
      player = playerInventory.player;
      posX = Mth.floor(player.getX());
      posZ = Mth.floor(player.getZ());
      scriptContainer = new ContainerWrapper(this);
      player.setDeltaMovement(Vec3.ZERO);
   }

   @Override
   public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) { return ItemStack.EMPTY; }

   @Override
   public boolean stillValid(Player playerIn) {
      return !playerIn.isRemoved() && posX == Mth.floor(playerIn.getX()) && posZ == Mth.floor(player.getZ());
   }

   public static IContainer getOrCreateIContainer(ContainerNpcInterface container) { return container.scriptContainer; }

}
