package noppes.npcs.mixin.recipebook;

import net.minecraft.network.protocol.game.ClientboundPlaceGhostRecipePacket;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.item.crafting.Recipe;
import noppes.npcs.controllers.data.RecipeCarpentry;
import noppes.npcs.recipebook.CustomServerPlaceRecipe;
import noppes.npcs.recipebook.CustomStackedContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlaceRecipe.class, priority = 498)
public class ServerPlaceRecipeMixin<C extends Container> {

    @Shadow protected Inventory inventory;
    @Shadow protected RecipeBookMenu<C> menu;

    @Unique protected CustomStackedContents<C> npcs$customStackedContents = new CustomStackedContents<>();

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(at = {@At("HEAD")}, method = {"recipeClicked"}, cancellable = true)
    public void npcs$recipeClicked(ServerPlayer player, Recipe<C> recipe, boolean isShiftPress, CallbackInfo ci) {
        if (recipe instanceof RecipeCarpentry npcRecipe && player.getRecipeBook().contains(recipe)) {
            ci.cancel();
            IServerPlaceRecipeMixin mixin = (IServerPlaceRecipeMixin) this;
            inventory = player.getInventory();
            if (mixin.invokeTestClearGrid() || player.isCreative()) {
                npcs$customStackedContents.clear();
                npcs$customStackedContents.setRecipe(npcRecipe);
                inventory.fillStackedContents(npcs$customStackedContents);
                menu.fillCraftSlotsStackedContents(npcs$customStackedContents);
                if (npcRecipe.availability.isAvailable(player) &&
                        npcs$customStackedContents.canNpcCraft(npcRecipe)) {
                    new CustomServerPlaceRecipe(recipe, isShiftPress, inventory, menu, npcs$customStackedContents);
                }
                else {
                    mixin.invokeClearGrid();
                    player.connection.send(new ClientboundPlaceGhostRecipePacket(player.containerMenu.containerId, recipe));
                }
                inventory.setChanged();
            }
            npcs$customStackedContents.clear();
        }
    }

}
