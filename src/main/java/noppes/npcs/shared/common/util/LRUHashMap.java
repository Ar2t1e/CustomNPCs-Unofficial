package noppes.npcs.shared.common.util;

import java.util.HashSet;
import java.util.LinkedHashMap;

public class LRUHashMap<K, V> extends LinkedHashMap<K, V> {

   private final int maxSize;

   public LRUHashMap(int size) {
      super(size, 0.75F, true);
      this.maxSize = size;
   }

   @Override
   public V put(K key, V value) {
      if (size() > maxSize) {
         for (K k : new HashSet<>(keySet())) {
            remove(k);
            if (size() < maxSize) { break; }
         }
      }
      return super.put(key, value);
   }

}
