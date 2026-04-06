package noppes.npcs.client.renderer;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import noppes.npcs.client.model.ModelNpcCrystal;
import noppes.npcs.entity.EntityNpcCrystal;

public class RenderNpcCrystal extends RenderNPCInterface
{
	ModelNpcCrystal mainmodel;
    public RenderNpcCrystal(EntityRendererProvider.Context manager, ModelNpcCrystal model)
    {
    	super(manager, model,0);
    	mainmodel = model;
    }
}
