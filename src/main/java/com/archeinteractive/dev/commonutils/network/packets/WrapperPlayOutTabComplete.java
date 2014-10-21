package com.archeinteractive.dev.commonutils.network.packets;

import java.lang.reflect.Constructor;
import java.util.List;

import com.archeinteractive.dev.commonutils.reflection.CommonReflection;
import com.archeinteractive.dev.commonutils.reflection.VersionHandler;

public class WrapperPlayOutTabComplete extends PacketWrapper {

    private static final Class<?> classPacketPlayOutTabComplete = VersionHandler.getNMSClass("PacketPlayOutTabComplete");
    
    private final String[] completions;
    
    public WrapperPlayOutTabComplete(String... completions) {
        super(classPacketPlayOutTabComplete);
        this.completions = completions;
    }
    
    public WrapperPlayOutTabComplete(List<String> completions) {
        this(completions.toArray(new String[completions.size()]));
    }
    
    public Object get() {
        Constructor<?> constructorPacketPlayOutTabComplete = CommonReflection.getConstructor(
                classPacketPlayOutTabComplete, new Class<?>[] { String[].class });
        return CommonReflection.constructNewInstance(constructorPacketPlayOutTabComplete,
                new Object[] { completions });
    }
}
