package noppes.npcs.mixin.world.entity.ai.attributes;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import noppes.npcs.api.mixin.world.entity.ai.attributes.IAttributeModifierMixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.UUID;

@Mixin(value = AttributeModifier.class, priority = 499)
public class AttributeModifierMixin implements IAttributeModifierMixin {

    @Final
    @Mutable
    @Shadow
    private double amount;

    @Final
    @Mutable
    @Shadow
    private AttributeModifier.Operation operation;

    @Final
    @Mutable
    @Shadow
    private UUID id;

    @Override
    public void npcs$setAmount(double amountIn) { amount = amountIn; }

    @Override
    public void npcs$setName(String name) {
        try { id = UUID.fromString(name); } catch (Exception ignored) {}
    }

    @Override
    public void npcs$setOperation(int operationIn) {
        if (operationIn < 0) { operationIn *= -1; }
        operation = AttributeModifier.Operation.values()[operationIn % AttributeModifier.Operation.values().length];
    }

}