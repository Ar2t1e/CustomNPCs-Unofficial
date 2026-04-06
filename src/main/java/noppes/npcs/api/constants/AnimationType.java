package noppes.npcs.api.constants;

public enum AnimationType {

   NONE(0),
   SIT(1),
   SLEEP(2),
   HUG(3),
   CROUCH(4),
   DANCE(5),
   AIM(6),
   CRAWL(7),
   POINT(8),
   CRY(9),
   WAVE(10),
   BOW(11),
   NO(12),
   YES(13),
   DEATH(14),
   WALK(15),
   IDLE(16),
   FLY(17),
   FLY_IDLE(18),
   STATIC(19),
   SWIM(20),
   WAG(21);

   final int type;

   AnimationType(int t) { type = t; }

   public int get() { return type; }

   public static int get(String name) {
      for (AnimationType atEnum : AnimationType.values()) {
         if (atEnum.name().equalsIgnoreCase(name)) { return atEnum.get(); }
      }
      return NONE.get();
   }

}
