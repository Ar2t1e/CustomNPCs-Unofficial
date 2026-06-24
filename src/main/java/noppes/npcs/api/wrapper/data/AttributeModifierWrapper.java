package noppes.npcs.api.wrapper.data;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import noppes.npcs.api.entity.data.IAttributeModifier;
import noppes.npcs.api.entity.data.INpcAttribute;
import noppes.npcs.mixin.entity.ai.attributes.IAttributeModifierMixin;

public class AttributeModifierWrapper implements IAttributeModifier {

	private final INpcAttribute parent;
	private final AttributeModifier modifer;

	public AttributeModifierWrapper(INpcAttribute attributeIn, AttributeModifier modiferIn) {
		modifer = modiferIn;
		parent = attributeIn;
	}

	@Override
	public double getAmount() { return modifer.getAmount(); }

	@Override
	public String getId() { return modifer.getID().toString(); }

	@Override
	public AttributeModifier getMCModifier() { return modifer; }

	@Override
	public String getName() { return modifer.getName(); }

	@Override
	public int getOperation() { return modifer.getOperation(); }

	@Override
	public IAttributeModifier setAmount(double amount) {
		if (parent == null) {
			((IAttributeModifierMixin) modifer).setAmount(amount);
			return this;
		}
		AttributeModifier newModifier = new AttributeModifier(modifer.getID(), modifer.getName(), amount, modifer.getOperation());
		parent.getMCAttribute().removeModifier(modifer);
		parent.getMCAttribute().applyModifier(newModifier);
		return parent.getModifier(newModifier.getName());
	}

	@Override
	public IAttributeModifier setName(String name) {
		if (parent == null) {
			((IAttributeModifierMixin) modifer).setName(name);
			return this;
		}
		AttributeModifier newModifier = new AttributeModifier(modifer.getID(), name, modifer.getAmount(), modifer.getOperation());
		parent.getMCAttribute().removeModifier(modifer);
		parent.getMCAttribute().applyModifier(newModifier);
		return parent.getModifier(newModifier.getName());
	}

	@Override
	public void setOperation(int operation) { ((IAttributeModifierMixin) modifer).setOperation(operation); }

	public String toString() { return modifer.toString().replace("AttributeModifier", "AttributeModifierWrapper"); }

}
