package noppes.npcs.constants;

public enum EnumCompanionStage {

   BABY(0, 7, "companion.baby"),
   CHILD(72000, 0, "companion.child"),
   TEEN(180000, 0, "companion.teenager"),
   ADULT(324000, 0, "companion.adult"),
   FULLGROWN(450000, 0, "companion.fullinflaten");

   public final int matureAge;
   public final int animation;
   public final String name;

   EnumCompanionStage(int age, int animationIn, String nameIn) {
      matureAge = age;
      animation = animationIn;
      name = nameIn;
   }

}
