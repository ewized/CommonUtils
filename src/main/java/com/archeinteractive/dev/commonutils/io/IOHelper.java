package com.archeinteractive.dev.commonutils.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A simple utility for greatly simplifying the use of
 * {@link ObjectOutputStream} and {@link ObjectInputStream} for I/O.
 * 
 * @param <T> The desired type to be represented by this {@link IOHelper}.
 */
public class IOHelper<T extends Serializable> {

    // FILE I/O
    /**
     * Attempt to read an object of the parameterized type from the specified
     * File.
     * 
     * @param file The File to read an Object from.
     * @return The loaded Object of the parameterized type, or <b>null</b> if
     *         there was an error during read.
     */
    public T read(File file) {
        return read(file, false);
    }
    
    /**
     * Attempt to read an object of the parameterized type from the specified
     * File.
     * 
     * @param file The File to read an Object from.
     * @return The loaded Object of the parameterized type, or <b>null</b> if
     *         there was an error during read.
     */
    @SuppressWarnings("unchecked")
    public T read(File file, boolean verbose) {
        try (   FileInputStream f_in = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(f_in)) {
            Object read = in.readObject();
            in.close();
            
            return (T) read;
        } catch (Exception e) {
            if (verbose) {
                e.printStackTrace();
            }
            
            return null;
        }
    }
    
    /**
     * Attempt to read an object of the parameterized type from the specified
     * File.
     * 
     * @param file_path The path to the File to read an Object from.
     * @return The loaded Object of the parameterized type, or <b>null</b> if
     *         there was an error during read.
     */
    public T read(String file_path, boolean verbose) {
        return read(new File(file_path), verbose);
    }
    
    /**
     * Attempt to read an object of the parameterized type from the specified
     * File.
     * 
     * @param file_path The path to the File to read an Object from.
     * @return The loaded Object of the parameterized type, or <b>null</b> if
     *         there was an error during read.
     */
    public T read(String file_path) {
        return read(new File(file_path));
    }
    
    /**
     * Write the specified Object to the specified File, returning the success.
     * 
     * @param object The Object of the parameterized type to write to File.
     * @param file   The File to write an Object to.
     * @return <b>true</b> if the Object was written successfully, <b>false</b>
     *         if errors occurred during write.
     */
    public boolean write(T object, File file) {
        return write(object, file, false);
    }
    
    /**
     * Write the specified Object to the specified File, returning the success.
     * 
     * @param object The Object of the parameterized type to write to File.
     * @param file   The File to write an Object to.
     * @return <b>true</b> if the Object was written successfully, <b>false</b>
     *         if errors occurred during write.
     */
    public boolean write(T object, File file, boolean verbose) {
        try (   FileOutputStream f_out = new FileOutputStream(file);
                ObjectOutputStream out = new ObjectOutputStream(f_out);) {
            out.writeObject(object);
            out.flush();
            out.close();
            
            return true;
        } catch (IOException e) {
            if (verbose) {
                e.printStackTrace();
            }
            
            return false;
        }
    }
    
    /**
     * Write the specified Object to the specified File, returning the success.
     * 
     * @param object    The Object of the parameterized type to write to File.
     * @param file_path The path to the File to write an Object to.
     * @return <b>true</b> if the Object was written successfully, <b>false</b>
     *         if errors occurred during write.
     */
    public boolean write(T object, String file_path, boolean verbose) {
        return write(object, new File(file_path), verbose);
    }
    
    /**
     * Write the specified Object to the specified File, returning the success.
     * 
     * @param object    The Object of the parameterized type to write to File.
     * @param file_path The path to the File to write an Object to.
     * @return <b>true</b> if the Object was written successfully, <b>false</b>
     *         if errors occurred during write.
     */
    public boolean write(T object, String file_path) {
        return write(object, new File(file_path));
    }
    
    // BYTE ARRAY I/O
    @SuppressWarnings("unchecked")
    public T readBytes(byte[] bytes, boolean verbose) {
        try (   ByteArrayInputStream bar_in = new ByteArrayInputStream(bytes);
                ObjectInputStream object_in = new ObjectInputStream(bar_in)) {
            Object read = object_in.readObject();
            object_in.close();
            
            return (T) read;
        } catch (Exception e) {
            if (verbose) {
                e.printStackTrace();
            }
            
            return null;
        }
    }
    
    public T readBytes(byte[] bytes) {
        return readBytes(bytes, false);
    }
    
    public byte[] writeBytes(T object, boolean verbose) {
        try (   ByteArrayOutputStream bar_out = new ByteArrayOutputStream();
                ObjectOutputStream object_out = new ObjectOutputStream(bar_out)) {
            object_out.writeObject(object);
            object_out.flush();
            object_out.close();
            bar_out.flush();
            bar_out.close();
            
            return bar_out.toByteArray();
        } catch (IOException e) {
            if (verbose) {
                e.printStackTrace();
            }
            
            return null;
        }
    }
    
    public byte[] writeBytes(T object) {
        return writeBytes(object, false);
    }
}
