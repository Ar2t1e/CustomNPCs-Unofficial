package noppes.npcs.api.block;

import noppes.npcs.api.ILayerBlockModel;
import noppes.npcs.api.ITimers;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.item.IItemStack;

import java.util.List;

public interface IBlockScripted extends IBlock {

   void setModel(@ParamName("item") IItemStack item);

   void setModel(@ParamName("name") String name);

   IItemStack getModel();

   ITimers getTimers();

   @SuppressWarnings("unused")
   void setRedstonePower(@ParamName("strength") int strength);

   @SuppressWarnings("unused")
   int getRedstonePower();

   @SuppressWarnings("unused")
   void setIsLadder(@ParamName("bo") boolean bo);

   @SuppressWarnings("unused")
   boolean getIsLadder();

   @SuppressWarnings("unused")
   void setIsWaterlogged(@ParamName("bo") boolean bo);

   @SuppressWarnings("unused")
   boolean getIsWaterlogged();

   void setLight(@ParamName("value") int value);

   int getLight();

   void setScale(@ParamName("x") float x, @ParamName("y") float y, @ParamName("z") float z);

   float getScaleX();

   float getScaleY();

   float getScaleZ();

   void setRotation(@ParamName("x") int x, @ParamName("y") int y, @ParamName("z") int z);

   @SuppressWarnings("unused")
   float getRotationX();

   @SuppressWarnings("unused")
   float getRotationY();

   @SuppressWarnings("unused")
   float getRotationZ();

   String executeCommand(@ParamName("command") String command);

   @SuppressWarnings("unused")
   boolean getIsPassible();

   @SuppressWarnings("unused")
   void setIsPassible(@ParamName("bo") boolean bo);

   @SuppressWarnings("unused")
   float getHardness();

   @SuppressWarnings("unused")
   void setHardness(@ParamName("hardness") float hardness);

   float getResistance();

   void setResistance(@ParamName("resistance") float resistance);

   @SuppressWarnings("unused")
   ITextPlane getTextPlane();

   @SuppressWarnings("unused")
   ITextPlane getTextPlane2();

   @SuppressWarnings("unused")
   ITextPlane getTextPlane3();

   @SuppressWarnings("unused")
   ITextPlane getTextPlane4();

   @SuppressWarnings("unused")
   ITextPlane getTextPlane5();

   @SuppressWarnings("unused")
   ITextPlane getTextPlane6();

   void trigger(@ParamName("id") int id, @ParamName("arguments") Object... arguments);

   // New from Unofficial (BetaZavr)
   @SuppressWarnings("unused")
   ITextPlane getTextPlane(@ParamName("id") int id);

   void setModel(@ParamName("block") IBlock block);

   void setObjModel(@ParamName("path") String path);

   @SuppressWarnings("unused")
   void setObjModel(@ParamName("path") String path,
                    @ParamName("objVisibleMeshes") String[] objVisibleMeshes,
                    @ParamName("objMaterialsReplase") String[][] objMaterialsReplase);

   ILayerBlockModel getModel(@ParamName("id") int id);

   @SuppressWarnings("unused")
   ILayerBlockModel createLayerModel();

   @SuppressWarnings("unused")
   List<ILayerBlockModel> getLayerModels();

   @SuppressWarnings("unused")
   boolean removeLayerModel(@ParamName("layer") ILayerBlockModel layer);

   @SuppressWarnings("unused")
   boolean removeLayerModel(@ParamName("id") int id);

   @SuppressWarnings("unused")
   void updateModel();

}
