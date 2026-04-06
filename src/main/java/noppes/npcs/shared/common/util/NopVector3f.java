package noppes.npcs.shared.common.util;

import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class NopVector3f implements Comparable<NopVector3f> {

   public static final NopVector3f ZERO = new NopVector3f(0.0F, 0.0F, 0.0F);
   public static final NopVector3f ONE = new NopVector3f(1.0F, 1.0F, 1.0F);
   public static final NopVector3f ROTATION = new NopVector3f(6.2831855F, 6.2831855F, 6.2831855F);
   public final float x;
   public final float y;
   public final float z;

   public NopVector3f(float xIn, float yIn, float zIn) {
      x = xIn;
      y = yIn;
      z = zIn;
   }

   public NopVector3f(float[] values) {
      this(values[0], values[1], values[2]);
   }

   public NopVector3f mul(float mul) {
      return new NopVector3f(x * mul, y * mul, z * mul);
   }

   public NopVector3f add(float x, float y, float z) {
      return new NopVector3f(x + x, y + y, z + z);
   }

   public NopVector3f add(NopVector3f vec) {
      return new NopVector3f(x + vec.x, y + vec.y, z + vec.z);
   }

   public NopVector3f subtract(NopVector3f vec) {
      return new NopVector3f(x - vec.x, y - vec.y, z - vec.z);
   }

   public NopVector3f modulo(NopVector3f vec) {
      return new NopVector3f(x % vec.x, y % vec.y, z % vec.z);
   }

   public NopVector3f set(float x, float y, float z) {
      return new NopVector3f(x, y, z);
   }

   public NopVector3f normalize() {
      float f = x * x + y * y + z * z;
      return f < 1.17549435E-38F ? this : mul((float) Mth.fastInvSqrt(f));
   }

   public NopVector3f lerp(NopVector3f vec, float f) {
      if (vec == this) {
         return this;
      } else {
         float dif = 1.0F - f;
         return new NopVector3f(x * dif + vec.x * f, y * dif + vec.y * f, z * dif + vec.z * f);
      }
   }

   public String toString() {
      return "[" + x + ", " + y + ", " + z + "]";
   }

   public boolean equals(Object ob) {
      if (this == ob) { return true; }
      if (ob instanceof NopVector3f o) { return x == o.x && y == o.y && z == o.z; }
      return false;
   }

   public int compareTo(@Nonnull NopVector3f o) {
      if (x == o.x && y == o.y && z == o.z) { return 0; }
      if (x != o.x) { return x < o.x ? -1 : 1; }
      if (y != o.y) { return y < o.y ? -1 : 1; }
      if (z != o.z) { return z < o.z ? -1 : 1; }
      return 0;
   }

}
