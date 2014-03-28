package com.gmail.favorlock.commonutils.math;

import java.io.Serializable;

import org.bukkit.util.Vector;

public class SerializableVector implements Serializable {

    private static final long serialVersionUID = 2502224833193065220L;
    
    private final double x;
    private final double y;
    private final double z;
    
    public SerializableVector(Vector copy) {
        this.x = copy.getX();
        this.y = copy.getY();
        this.z = copy.getZ();
    }
    
    public SerializableVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public SerializableVector() {
        this.x = .0;
        this.y = .0;
        this.z = .0;
    }
    
    public Vector toVector() {
        return new Vector(x, y, z);
    }
}
