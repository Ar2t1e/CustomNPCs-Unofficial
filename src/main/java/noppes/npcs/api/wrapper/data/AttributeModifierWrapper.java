package noppes.npcs.api.wrapper.data;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import noppes.npcs.api.entity.data.IAttributeModifier;
import noppes.npcs.api.entity.data.INpcAttribute;
import noppes.npcs.api.mixin.world.entity.ai.attributes.IAttributeModifierMixin;

public class AttributeModifierWrapper implements IAttributeModifier {

    private final INpcAttribute parent;
    private final AttributeModifier modifer;

    public AttributeModifierWrapper(INpcAttribute attribute, AttributeModifier modifer) {
        this.modifer = modifer;
        this.parent = attribute;
    }

    @Override
    public double getAmount() {
        return this.modifer.getAmount();
    }

    @Override
    public String getId() {
        return this.modifer.getId().toString();
    }

    @Override
    public AttributeModifier getMCModifier() {
        return this.modifer;
    }

    @Override
    public String getName() {
        return this.modifer.getName();
    }

    @Override
    public int getOperation() {
        return this.modifer.getOperation().ordinal();
    }

    @Override
    public IAttributeModifier setAmount(double amount) {
        if (parent == null) {
            ((IAttributeModifierMixin) modifer).npcs$setAmount(amount);
            return this;
        }
        AttributeModifier newModifier = new AttributeModifier(modifer.getId(), modifer.getName(), amount, modifer.getOperation());
        parent.getMCAttribute().removeModifier(this.modifer);
        parent.getMCAttribute().addTransientModifier(newModifier);
        return parent.getModifier(newModifier.getName());
    }

    @Override
    public IAttributeModifier setName(String name) {
        if (this.parent == null) {
            ((IAttributeModifierMixin) modifer).npcs$setName(name);
            return this;
        }
        AttributeModifier newModifier = new AttributeModifier(this.modifer.getId(), name, this.modifer.getAmount(), this.modifer.getOperation());
        this.parent.getMCAttribute().removeModifier(this.modifer);
        this.parent.getMCAttribute().addTransientModifier(newModifier);
        return this.parent.getModifier(newModifier.getName());
    }

    @Override
    public void setOperation(int operation) { ((IAttributeModifierMixin) modifer).npcs$setOperation(operation); }

    public String toString() {
        return this.modifer.toString().replace("AttributeModifier", "AttributeModifierWrapper");
    }

}
