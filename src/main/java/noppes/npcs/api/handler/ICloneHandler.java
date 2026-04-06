package noppes.npcs.api.handler;

import noppes.npcs.api.IWorld;
import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.entity.IEntity;

public interface ICloneHandler {

   IEntity<?> spawn(@ParamName("x") double x, @ParamName("y") double y, @ParamName("z") double z,
                    @ParamName("tab") int tab, @ParamName("name") String name, @ParamName("level") IWorld level);

   IEntity<?> get(@ParamName("tab") int tab, @ParamName("name") String name, @ParamName("level") IWorld level);

   void set(@ParamName("tab") int tab, @ParamName("name") String name, @ParamName("entity") IEntity<?> entity);

   void remove(@ParamName("tab") int tab, @ParamName("name") String name);

}
