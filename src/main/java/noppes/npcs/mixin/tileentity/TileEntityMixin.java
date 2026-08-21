package noppes.npcs.mixin.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import noppes.npcs.CustomNpcs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntity.class, priority = 498)
public class TileEntityMixin {

    /**
     * @author BetaZavr
     * @reason Correction of previously mod incorrectly registered blocks
     */
    @Inject(method = "create", at = @At("HEAD"))
    private static void npcs$create(World worldIn, NBTTagCompound compound, CallbackInfoReturnable<TileEntity> cir) {
        ResourceLocation rl = new ResourceLocation(compound.getString("id"));
        String p = rl.getResourcePath();
        if (rl.getResourceDomain().equals("minecraft") && (
                p.equals("tileredstoneblock") ||
                p.equals("tileblockanvil") ||
                p.equals("tilemailbox") ||
                p.equals("tilewaypoint") ||
                p.equals("tilenpcscripted") ||
                p.equals("tilenpcscripteddoor") ||
                p.equals("tilenpcbuilder") ||
                p.equals("tilenpccopy") ||
                p.equals("tilenpcborder")
        )) {
            compound.setString("id", new ResourceLocation(CustomNpcs.MODID, p).toString());
        }
    }

}
