package noppes.npcs.api.entity.data;

import noppes.npcs.api.INbt;
import noppes.npcs.api.interfaces.ParamName;

public interface IData {

   void put(@ParamName("key") String key, @ParamName("value") Object value);

   Object get(@ParamName("key") String key);

   void remove(@ParamName("key") String key);

    INbt getNbt();

    boolean has(@ParamName("key") String key);

   String[] getKeys();

   void clear();

    void setNbt(@ParamName("nbt") INbt nbt);

}
