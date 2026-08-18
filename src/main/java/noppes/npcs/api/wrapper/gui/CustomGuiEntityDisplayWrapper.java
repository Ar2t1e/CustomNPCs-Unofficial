package noppes.npcs.api.wrapper.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.api.INbt;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.entity.IEntity;
import noppes.npcs.api.gui.IEntityDisplay;
import noppes.npcs.api.wrapper.NBTWrapper;

// New Unofficial (Goodbird)
public class CustomGuiEntityDisplayWrapper
        extends CustomGuiComponentWrapper
        implements IEntityDisplay {

    protected IEntity<?> entity;
    protected INbt entityData = new NBTWrapper(new NBTTagCompound());
    protected int rotation;
    protected float scale = 1.0F;
    protected boolean showBackground = true;

    public boolean isFollowingCursor = true;
    public int entityId = -1;

    public CustomGuiEntityDisplayWrapper() { }

    public CustomGuiEntityDisplayWrapper(int id, IEntity<?> entity, int x, int y) {
        setId(id);
        setEntity(entity);
        setPos(x, y);
    }

    @Override
    public IEntity<?> getEntity() { return entity; }

    public INbt getEntityData() { return entityData; }

    @Override
    public CustomGuiEntityDisplayWrapper setEntity(IEntity<?> entityIn) {
        entity = entityIn;
        if (entityIn == null) { entityData = new NBTWrapper(new NBTTagCompound()); }
        else { entityData = entityIn.getEntityNbt(); }
        if (entityIn != null && entityIn.getMCEntity() instanceof EntityPlayer) { entityId = entityIn.getMCEntity().getEntityId(); }
        return this;
    }

    @Override
    public int getRotation() { return rotation; }

    @Override
    public CustomGuiEntityDisplayWrapper setRotation(int rotationIn) {
        rotation = rotationIn;
        return this;
    }

    @Override
    public boolean isFollowingCursor() { return isFollowingCursor; }

    @Override
    public CustomGuiEntityDisplayWrapper setFollowingCursor(boolean state) {
        isFollowingCursor = state;
        return this;
    }

    @Override
    public float getScale() { return scale; }

    @Override
    public CustomGuiEntityDisplayWrapper setScale(float scaleIn) {
        scale = scaleIn;
        return this;
    }

    @Override
    public boolean getBackground() { return showBackground; }

    @Override
    public CustomGuiEntityDisplayWrapper setBackground(boolean bo) {
        showBackground = bo;
        return this;
    }

    @Override
    public int getType() { return GuiComponentType.ENTITY_DISPLAY.get(); }

    @Override
    public NBTTagCompound toNBT(NBTTagCompound compound) {
        super.toNBT(compound);
        compound.setTag("entity", entityData.getMCNBT());
        compound.setInteger("entityId", entityId);
        compound.setInteger("rotation", rotation);
        compound.setFloat("scale", scale);
        compound.setBoolean("followCursor", isFollowingCursor);
        compound.setBoolean("background", showBackground);
        return compound;
    }

    @Override
    public CustomGuiEntityDisplayWrapper fromNBT(NBTTagCompound compound) {
        super.fromNBT(compound);
        entityData = new NBTWrapper(compound.getCompoundTag("entity"));
        entityId = compound.getInteger("entityId");
        setRotation(compound.getInteger("rotation"));
        setScale(compound.getFloat("scale"));
        setFollowingCursor(compound.getBoolean("followCursor"));
        setBackground(compound.getBoolean("background"));
        return this;
    }

    @Override
    public CustomGuiEntityDisplayWrapper setEntitySyncedById(IEntity<?> entityIn) {
        entity = entityIn;
        if (entityIn == null) {
            entityData = new NBTWrapper(new NBTTagCompound());
            entityId = -1;
        }
        else { entityId = entityIn.getMCEntity().getEntityId(); }
        return this;
    }

}
