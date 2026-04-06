package noppes.npcs.api.wrapper.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;

import noppes.npcs.api.interfaces.ParamName;
import noppes.npcs.api.CustomNPCsException;
import noppes.npcs.api.handler.data.IDataElement;
import noppes.npcs.api.wrapper.DataObject;

import javax.annotation.Nonnull;

public class DataElement implements IDataElement {

    protected final @Nonnull Object parent; // parent Object
    protected final @Nonnull Object object; // this
    protected final @Nonnull Class<?> parentClazz; // parent Class
    protected final int id;
    protected final String obfuscated;
    protected final String name;

    public DataElement(@Nonnull Object objectIn, @Nonnull Object parentIn, @Nonnull Integer idIn) {
        id = idIn;
        object = objectIn;
        parent = parentIn;
        String tempName = "";
        String tempObf = "";
        if (objectIn instanceof Method method) {
            tempName = method.getName();
            tempObf = DataObject.getObfuscatedName(tempName);
            parentClazz = method.getDeclaringClass();
        }
        else if (objectIn instanceof Field field) {
            tempName = field.getName();
            tempObf = DataObject.getObfuscatedName(tempName);
            parentClazz = field.getDeclaringClass();
        }
        else if (objectIn instanceof Constructor<?> constructor) {
            parentClazz = constructor.getDeclaringClass();
        }
        else if (objectIn instanceof Class<?> clazz) {
            parentClazz = clazz;
            tempName = parentClazz.getName();
        }
        else { parentClazz = parentIn.getClass(); }
        name = tempName;
        obfuscated = tempObf;
    }

    @Override
    public boolean equals(Object objectIn) {
        if (objectIn == null) { return false; }
        if (object instanceof Method m0) {
            if (!(objectIn instanceof Method m1)) { return false; }
            Parameter[] p0 = m0.getParameters();
            Parameter[] p1 = m1.getParameters();
            if (p0.length != p1.length) { return false; }
            for (int p = 0; p < p0.length; p++) {
                if (p0[p].getType() != p1[p].getType()) { return false; }
            }
            return m0.getName().equals(m1.getName()) && m0.getReturnType() == m1.getReturnType() && m0.getDeclaringClass() == m1.getDeclaringClass();
        }
        else if (object instanceof Field f0) {
            if (!(objectIn instanceof Field f1)) { return false; }
            return f0.getName().equals(f1.getName()) && f0.getType() == f1.getType()
                    && f0.getDeclaringClass() == f1.getDeclaringClass();
        }
        else if (object instanceof Constructor<?> c0) {
            if (!(objectIn instanceof Constructor<?> c1)) { return false; }
            Parameter[] p0 = c0.getParameters();
            Parameter[] p1 = c1.getParameters();
            if (p0.length != p1.length) { return false; }
            for (int p = 0; p < p0.length; p++) {
                if (p0[p].getType() != p1[p].getType()) { return false; }
            }
            return c0.getDeclaringClass() == c1.getDeclaringClass();
        }
        return equals(objectIn);
    }

    @Override
    public int getId() { return id; }

    @Override
    public String getInfo() {
        StringBuilder key = new StringBuilder();
        if (object instanceof Method method) {
            int modifiers = method.getModifiers();
            if (Modifier.isPrivate(modifiers)) { key.append("private "); }
            else if (Modifier.isProtected(modifiers)) { key.append("protected "); }
            else if (Modifier.isPublic(modifiers)) { key.append("public "); }
            else { key.append("default "); }
            if (Modifier.isStatic(modifiers)) { key.append("static "); }
            if (Modifier.isSynchronized(modifiers)) { key.append("synchronized "); }
            if (Modifier.isFinal(modifiers)) { key.append("final "); }
            key.append(DataObject.getAgrName(method.getReturnType(), method.getReturnType(), null)).append(" ");
            String obfName = DataObject.getObfuscatedName(method.getName());
            if (!obfName.isEmpty()) { key.append(" {obf_name=\"").append(obfName).append("\"}"); }
            key.append(method.getName()).append("(");
            StringBuilder body = new StringBuilder();
            for (Parameter p : method.getParameters()) {
                if (!body.isEmpty()) { body.append(", "); }
                body.append(DataObject.getAgrName(p.getType(), p.getType(), null))
                        .append(" ");
                boolean found = false;
                for (Annotation a : p.getAnnotations()) {
                    if (a instanceof ParamName pName) {
                        body.append(pName.value());
                        found = true;
                    }
                }
                if (!found) { body.append(p.getName()); }
            }
            key.append(body).append(")");
        }
        else if (object instanceof Field field) {
            int modifiers = field.getModifiers();
            if (Modifier.isPrivate(modifiers)) { key.append("private "); }
            else if (Modifier.isProtected(modifiers)) { key.append("protected "); }
            else if (Modifier.isPublic(modifiers)) { key.append("public "); }
            else { key.append("default "); }
            if (Modifier.isStatic(modifiers)) { key.append("static "); }
            if (Modifier.isFinal(modifiers)) { key.append("final "); }
            key.append(DataObject.getAgrName(field.getType(), field.getGenericType(), getValue())).append(" ");
            String obfName = DataObject.getObfuscatedName(field.getName());
            if (!obfName.isEmpty()) { key.append(obfName).append(" / "); }
            key.append(field.getName());
        }
        else if (object instanceof Constructor<?> constructor) {
            int modifiers = constructor.getModifiers();
            if (Modifier.isPrivate(modifiers)) { key.append("private "); }
            else if (Modifier.isProtected(modifiers)) { key.append("protected "); }
            else if (Modifier.isPublic(modifiers)) { key.append("public "); }
            else { key.append("default "); }
            // Arguments:
            key.append(parentClazz.getSimpleName()).append("(");
            StringBuilder body = new StringBuilder();
            for (Parameter p : constructor.getParameters()) {
                if (!body.isEmpty()) { body.append(", "); }
                body.append(DataObject.getAgrName(p.getType(), p.getType(), null))
                        .append(" ");
                boolean found = false;
                for (Annotation a : p.getAnnotations()) {
                    if (a instanceof ParamName pName) {
                        body.append(pName.value());
                        found = true;
                    }
                }
                if (!found) { body.append(p.getName()); }
            }
            key.append(body).append(");");
        }
        else if (object instanceof Class<?> clazz) {
            int modifiers = clazz.getModifiers();
            if (Modifier.isPrivate(modifiers)) { key.append("private "); }
            else if (Modifier.isProtected(modifiers)) { key.append("protected "); }
            else if (Modifier.isPublic(modifiers)) { key.append("public "); }
            else { key.append("default "); }
            key.append(DataObject.getAgrName(clazz, clazz.getGenericSuperclass(), null));
        } // subclass
        else { return parent.toString(); }
        return key.toString();
    }

    @Override
    public @Nonnull String getName() { return name; }

    @Override
    public String getObfuscatedName() { return obfuscated; }

    @Override
    public @Nonnull Object getObject() { return object; }

    @Override
    public @Nonnull Class<?> getParentClass() { return parentClazz; }

    @Override
    public int getType() {
        if (object instanceof Constructor) { return 0; }
        if (object instanceof Class) { return 1; }
        if (object instanceof Field) { return 2; }
        if (object instanceof Method) { return 3; }
        return -1;
    }

    @Override
    public Object getValue() {
        if (object instanceof Method) { return ((Method) object).getReturnType(); }
        else if (object instanceof Field field) {
            try {
                field.trySetAccessible();
                return field.get(parent);
            }
            catch (Exception e) { throw new CustomNPCsException(e, "Error get field value"); }
        }
        return object;
    }

    @Override
    public Object invoke(Object[] values) {
        if (object instanceof Method method) {
            try {
                method.trySetAccessible();
                method.invoke(parent, values);
            }
            catch (Exception e) { throw new CustomNPCsException(e, "Error invoke method"); }
        }
        return null;
    }

    @Override
    public boolean isBelong(Class<?> cz) { return parentClazz.isAssignableFrom(cz); }

    @Override
    public boolean setValue(Object value) {
        if (object instanceof Field field) {
            int mod = field.getModifiers();
            if (Modifier.isFinal(mod)) {
                try {
                    Field modifiersField = Field.class.getDeclaredField("modifiers");
                    modifiersField.trySetAccessible();
                    modifiersField.setInt(field, mod - Modifier.FINAL - (Modifier.isPrivate(mod) ? Modifier.PRIVATE : 0));
                    field.trySetAccessible();
                    field.set(Modifier.isStatic(mod) ? null : parent, value);
                    modifiersField.setInt(field, mod);
                    return true;
                }
                catch (Exception e) { throw new CustomNPCsException(e, "Error set final value"); }
            }
            try {
                field.trySetAccessible();
                field.set(parent, value);
                return true;
            }
            catch (Exception e) { throw new CustomNPCsException(e, "Error set value"); }
        }
        return false;
    }

}
