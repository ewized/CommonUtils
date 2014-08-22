package com.gmail.favorlock.commonutils.configuration.json;

import com.archeinteractive.ason.io.impl.JSONReader;
import com.archeinteractive.ason.io.impl.JSONWriter;
import com.archeinteractive.ason.io.objects.ObjectConstructorSet;
import com.archeinteractive.ason.io.objects.ObjectReader;
import com.archeinteractive.ason.io.objects.ObjectWriter;
import com.gmail.favorlock.commonutils.CommonUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * A centralized utility for Object - JsoN serialization and deserialization.
 */
public class JsonSerialization {

    private static final JSONReader fromJson = new JSONReader(false);
    private static final JSONWriter toJson = new JSONWriter();
    private static final ObjectReader fromObject = new ObjectReader();
    private static final ObjectWriter toObject = new ObjectWriter();

    private JsonSerialization() {}

    public static <T extends Serializable> boolean writeObject(T object, File file) {
        initFile(file);
        return toJson.compose(fromObject.parse(object), file);
    }

    public static <T extends Serializable> boolean writeObject(T object, OutputStream out) {
        return toJson.compose(fromObject.parse(object), out);
    }

    public static <T extends Serializable> boolean writeObject(T object, Writer writer) {
        return toJson.compose(fromObject.parse(object), writer);
    }

    public static <T extends Serializable> T readObject(Class<T> cls, File file) {
        initFile(file);
        return toObject.compose(fromJson.parse(file), cls);
    }

    public static <T extends Serializable> T readObject(Class<T> cls, InputStream in) {
        return toObject.compose(fromJson.parse(in), cls);
    }

    public static <T extends Serializable> T readObject(Class<T> cls, Reader reader) {
        return toObject.compose(fromJson.parse(reader), cls);
    }

    public static <T extends Serializable> void registerTypeConstructor(Class<T> cls, ObjectConstructorSet.TypeConstructor<T> constructor) {
        toObject.getConstructorSet().bind(cls, constructor);
    }

    public static <T extends Serializable> void registerTypeConstructor(Class<T> cls, ObjectConstructorSet.InnerTypeConstructor<T> constructor) {
        toObject.getConstructorSet().bind(cls, constructor);
    }

    public static <T extends Object> void copyFields(T from, T to, Class<? super T> stop) {
        Class<?> fromcls = from.getClass(), tocls = to.getClass(), currentcls;

        if (fromcls.equals(tocls) || fromcls.isAssignableFrom(tocls)) {
            currentcls = fromcls;
        } else if (tocls.isAssignableFrom(fromcls)) {
            currentcls = tocls;
        } else {
            return;
        }

        do {
            for (Field f : currentcls.getDeclaredFields()) {
                if (acceptField(f)) {
                    try {
                        f.set(to, f.get(from));
                    } catch (ReflectiveOperationException e) {
                        e.printStackTrace();
                    }
                }
            }
        } while ((currentcls = currentcls.getSuperclass()) != null && !currentcls.isAssignableFrom(stop));
    }

    public static <T extends Object> void copyFields(T from, T to) {
        copyFields(from, to, Object.class);
    }

    private static boolean acceptField(Field f) {
        int mod = f.getModifiers();

        boolean isFinal = Modifier.isFinal(mod);
        boolean isStatic = Modifier.isStatic(mod);
        boolean isTransient = Modifier.isTransient(mod);
        boolean isEnclosingScope = f.getType().equals(f.getDeclaringClass().getEnclosingClass());

        return !(isFinal || isStatic || isTransient || isEnclosingScope);
    }

    private static void initFile(File file) {
        try {
            if (file.exists() == false) {
                if (file.getParentFile().exists() == false) {
                    file.getParentFile().mkdirs();
                }

                file.createNewFile();
            }
        } catch (IOException e) {
            CommonUtils.getPlugin().getLogger().info(file.getPath() + " does not exists!");
        }
    }
}