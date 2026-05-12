package noppes.npcs.client.model.custom;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import noppes.npcs.shared.common.util.LogWriter;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;

public class CustomCube {

    protected static Class<?> classVertex;
    protected static Constructor<?> constructorVertex;
    protected static Constructor<?> constructorPolygon;

    public static ModelPart.Cube createBannerFlag() {
        ModelPart.Cube cube = new ModelPart.Cube(
                0, 0, // u, v
                -10.0f, 0.0f, -2.0f, // minX, minY, minZ
                20.0f, 40.0f, 1.0f, // width, height, depth
                0, 0, 0, // pivotX, pivotY, pivotZ
                false, // is mirror
                64.0f, 32.0f, // texture scale U, V
                new HashSet<>(List.of(Direction.values())) // Set of displayed faces
        );
        check();
        if (classVertex != null && constructorVertex != null && constructorPolygon != null) {
            try {
                constructorVertex.trySetAccessible();
                Object vertex7 = constructorVertex.newInstance(-10.0f, 0.0f, -2.0f, 0.0F, 0.0F);
                Object vertex = constructorVertex.newInstance(10.0f, 0.0f, -2.0f, 0.0F, 8.0F);
                Object vertex1 = constructorVertex.newInstance(10.0f, 40.0f, -2.0f, 8.0F, 8.0F);
                Object vertex2 = constructorVertex.newInstance(-10.0f, 40.0f, -2.0f, 8.0F, 0.0F);
                Object vertex3 = constructorVertex.newInstance(-10.0f, 0.0f, -1.0f, 0.0F, 0.0F);
                Object vertex4 = constructorVertex.newInstance(10.0f, 0.0f, -1.0f, 0.0F, 8.0F);
                Object vertex5 = constructorVertex.newInstance(10.0f, 40.0f, -1.0f, 8.0F, 8.0F);
                Object vertex6 = constructorVertex.newInstance(-10.0f, 40.0f, -1.0f, 8.0F, 0.0F);

                Field field;
                try { field = ModelPart.Cube.class.getDeclaredField("f_104341_"); }
                catch (Exception ignored) { field = ModelPart.Cube.class.getDeclaredField("polygons"); }
                field.trySetAccessible();
                Object[] polygons = (Object[]) field.get(cube);
                constructorPolygon.trySetAccessible();

                // DOWN
                Object vs = Array.newInstance(classVertex, 4);
                Array.set(vs, 0, vertex4);
                Array.set(vs, 1, vertex3);
                Array.set(vs, 2, vertex7);
                Array.set(vs, 3, vertex);
                polygons[0] = constructorPolygon.newInstance(vs, 1.0f, 0.0f, 11.0f, 1.0f, 64.0f, 32.0f, false, Direction.DOWN);
                // UP
                vs = Array.newInstance(classVertex, 4);
                Array.set(vs, 0, vertex1);
                Array.set(vs, 1, vertex2);
                Array.set(vs, 2, vertex6);
                Array.set(vs, 3, vertex5);
                polygons[1] = constructorPolygon.newInstance(vs, 11.0f, 1.0f, 21.0f, 0.0f, 64.0f, 32.0f, false, Direction.UP);
                // WEST
                vs = Array.newInstance(classVertex, 4);
                Array.set(vs, 0, vertex7);
                Array.set(vs, 1, vertex3);
                Array.set(vs, 2, vertex6);
                Array.set(vs, 3, vertex2);
                polygons[2] = constructorPolygon.newInstance(vs, 0.0f, 1.0f, 1.0f, 17.0f, 64.0f, 32.0f, false, Direction.WEST);
                // NORTH
                vs = Array.newInstance(classVertex, 4);
                Array.set(vs, 0, vertex);
                Array.set(vs, 1, vertex7);
                Array.set(vs, 2, vertex2);
                Array.set(vs, 3, vertex1);
                polygons[3] = constructorPolygon.newInstance(vs, 1.0f, 1.0f, 11.0f, 17.0f, 64.0f, 32.0f, false, Direction.NORTH);
                // EAST
                vs = Array.newInstance(classVertex, 4);
                Array.set(vs, 0, vertex4);
                Array.set(vs, 1, vertex);
                Array.set(vs, 2, vertex1);
                Array.set(vs, 3, vertex5);
                polygons[4] = constructorPolygon.newInstance(vs, 11.0f, 1.0f, 12.0f, 17.0f, 64.0f, 32.0f, false, Direction.EAST);
                // SOUTH
                vs = Array.newInstance(classVertex, 4);
                Array.set(vs, 0, vertex3);
                Array.set(vs, 1, vertex4);
                Array.set(vs, 2, vertex5);
                Array.set(vs, 3, vertex6);
                polygons[5] = constructorPolygon.newInstance(vs, 12.0f, 1.0f, 22.0f, 17.0f, 64.0f, 32.0f, false, Direction.SOUTH);
            }
            catch (Exception e) { LogWriter.error(e); }
        }
        return cube;
    }

    public static ModelPart.Cube createShieldFlag() {
        ModelPart.Cube cube = new ModelPart.Cube(
                0, 0, // u, v
                -5.0f, -10.0f, -2.02f, // minX, minY, minZ
                10.0f, 20.0f, 1.04f, // width, height, depth
                0, 0, 0, // pivotX, pivotY, pivotZ
                false, // is mirror
                64.0f, 32.0f, // texture scale U, V
                new HashSet<>(List.of(Direction.values())) // Set of displayed faces
        );
        check();
        if (classVertex != null && constructorVertex != null && constructorPolygon != null) {
            try {
                constructorVertex.trySetAccessible();
                Object vertex7 = constructorVertex.newInstance(-5.0f, -10.0f, -2.02f, 0.0F, 0.0F);
                Object vertex = constructorVertex.newInstance(5.0f, -10.0f, -2.02f, 0.0F, 8.0F);
                Object vertex1 = constructorVertex.newInstance(5.0f, 10.0f, -2.02f, 8.0F, 8.0F);
                Object vertex2 = constructorVertex.newInstance(-5.0f, 10.0f, -2.02f, 8.0F, 0.0F);
                Object vertex3 = constructorVertex.newInstance(-5.0f, -10.0f, -0.98f, 0.0F, 0.0F);
                Object vertex4 = constructorVertex.newInstance(5.0f, -10.0f, -0.98f, 0.0F, 8.0F);
                Object vertex5 = constructorVertex.newInstance(5.0f, 10.0f, -0.98f, 8.0F, 8.0F);
                Object vertex6 = constructorVertex.newInstance(-5.0f, 10.0f, -0.98f, 8.0F, 0.0F);

                Field field;
                try { field = ModelPart.Cube.class.getDeclaredField("f_104341_"); }
                catch (Exception ignored) { field = ModelPart.Cube.class.getDeclaredField("polygons"); }
                field.trySetAccessible();
                Object[] polygons = (Object[]) field.get(cube);
                constructorPolygon.trySetAccessible();

                // DOWN
                Object vs = Array.newInstance(classVertex, 4);
                Array.set(vs, 0, vertex4);
                Array.set(vs, 1, vertex3);
                Array.set(vs, 2, vertex7);
                Array.set(vs, 3, vertex);
                polygons[0] = constructorPolygon.newInstance(vs, 1.0f, 0.0f, 11.0f, 1.0f, 64.0f, 32.0f, false, Direction.DOWN);
                // UP
                vs = Array.newInstance(classVertex, 4);
                Array.set(vs, 0, vertex1);
                Array.set(vs, 1, vertex2);
                Array.set(vs, 2, vertex6);
                Array.set(vs, 3, vertex5);
                polygons[1] = constructorPolygon.newInstance(vs, 11.0f, 1.0f, 21.0f, 0.0f, 64.0f, 32.0f, false, Direction.UP);
                // WEST
                vs = Array.newInstance(classVertex, 4);
                Array.set(vs, 0, vertex7);
                Array.set(vs, 1, vertex3);
                Array.set(vs, 2, vertex6);
                Array.set(vs, 3, vertex2);
                polygons[2] = constructorPolygon.newInstance(vs, 0.0f, 1.0f, 1.0f, 17.0f, 64.0f, 32.0f, false, Direction.WEST);
                // NORTH
                vs = Array.newInstance(classVertex, 4);
                Array.set(vs, 0, vertex);
                Array.set(vs, 1, vertex7);
                Array.set(vs, 2, vertex2);
                Array.set(vs, 3, vertex1);
                polygons[3] = constructorPolygon.newInstance(vs, 1.0f, 1.0f, 11.0f, 17.0f, 64.0f, 32.0f, false, Direction.NORTH);
                // EAST
                vs = Array.newInstance(classVertex, 4);
                Array.set(vs, 0, vertex4);
                Array.set(vs, 1, vertex);
                Array.set(vs, 2, vertex1);
                Array.set(vs, 3, vertex5);
                polygons[4] = constructorPolygon.newInstance(vs, 11.0f, 1.0f, 12.0f, 17.0f, 64.0f, 32.0f, false, Direction.EAST);
                // SOUTH
                vs = Array.newInstance(classVertex, 4);
                Array.set(vs, 0, vertex3);
                Array.set(vs, 1, vertex4);
                Array.set(vs, 2, vertex5);
                Array.set(vs, 3, vertex6);
                polygons[5] = constructorPolygon.newInstance(vs, 12.0f, 1.0f, 22.0f, 17.0f, 64.0f, 32.0f, false, Direction.SOUTH);
            }
            catch (Exception e) { LogWriter.error(e); }
        }
        return cube;
    }

    private static void check() {
        if (classVertex == null || constructorVertex == null || constructorPolygon == null) {
            try {
                Class<?> cp = null;
                for (Class<?> c : ModelPart.class.getDeclaredClasses()) {
                    if (c.getSimpleName().equals("Vertex")) { classVertex = c; }
                    if (c.getSimpleName().equals("Polygon")) { cp = c; }
                }
                if (classVertex != null && cp != null) {
                    constructorVertex = classVertex.getConstructor(float.class, float.class, float.class, float.class, float.class);
                    constructorPolygon = cp.getConstructors()[0];
                }
            }
            catch (Exception ignored) {}
        }
    }

}
