package noppes.npcs.api.gui;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.entity.IEntity;

public interface IEntityDisplay extends ICustomGuiComponent {

   IEntity<?> getEntity();

   IEntityDisplay setEntity(@ParamName("entity") IEntity<?> entity);

   IEntityDisplay setEntitySyncedById(@ParamName("entity") IEntity<?> entity);

   int getRotation();

   IEntityDisplay setRotation(@ParamName("rotation") int rotation);

   float getScale();

   IEntityDisplay setScale(@ParamName("scale") float scale);

   boolean getBackground();

   IEntityDisplay setBackground(@ParamName("bo") boolean bo);

   boolean isFollowingCursor();

   IEntityDisplay setFollowingCursor(@ParamName("state") boolean state);

}
