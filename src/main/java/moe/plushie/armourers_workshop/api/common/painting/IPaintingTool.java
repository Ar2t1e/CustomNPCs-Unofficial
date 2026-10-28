package moe.plushie.armourers_workshop.api.common.painting;

import net.minecraft.item.ItemStack;

@SuppressWarnings("unused")
public interface IPaintingTool {

	int getToolColour(ItemStack stack);

	@Deprecated
    boolean getToolHasColour(ItemStack stack);

	IPaintType getToolPaintType(ItemStack stack);

	void setToolColour(ItemStack stack, int colour);

	void setToolPaintType(ItemStack stack, IPaintType paintType);

}
