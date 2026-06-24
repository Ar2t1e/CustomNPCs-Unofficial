package noppes.npcs.mixin.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.chat.Component;
import net.minecraft.util.text.TextFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = ItemStack.class, priority = 498)
public class ItemStackMixin {

    @Shadow private NBTTagCompound stackTagCompound;

    @Inject(at = @At("RETURN"), method = "getTooltip", cancellable = true)
    private void npcs$getTooltip(EntityPlayer playerIn, ITooltipFlag advanced,
                                 CallbackInfoReturnable<List<String>> cir) {
        if (stackTagCompound == null || !stackTagCompound.hasKey("display", 10)) {
            return;
        }
        NBTTagCompound display = stackTagCompound.getCompoundTag("display");
        if (display.getTagId("Lore") != 9) {
            return;
        }
        NBTTagList loreList = display.getTagList("Lore", 8);
        if (loreList.hasNoTags()) {
            return;
        }
        List<String> tooltip = cir.getReturnValue();
        boolean modified = false;
        for (int loreIndex = 0; loreIndex < loreList.tagCount(); loreIndex++) {
            String loreJson = loreList.getStringTagAt(loreIndex);
            String loreText = Component.jsonToComponent(loreJson).getFormattedText();
            String originalLine = TextFormatting.DARK_PURPLE + "" + TextFormatting.ITALIC + loreJson;
            String targetLine = TextFormatting.DARK_PURPLE + "" + TextFormatting.ITALIC + loreText;

            for (int i = 0; i < tooltip.size(); i++) {
                String current = tooltip.get(i);
                if (current.startsWith(TextFormatting.DARK_PURPLE + "" + TextFormatting.ITALIC)) {
                    if (current.equals(originalLine) || current.contains(loreJson)) {
                        tooltip.set(i, targetLine);
                        modified = true;
                        break;
                    }
                }
            }
        }
        if (modified) {
            cir.setReturnValue(tooltip);
        }
    }

}