package com.gmail.favorlock.commonutils.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A simple utility for greatly simplifying the use of
 * {@link ObjectOutputStream} and {@link ObjectInputStream} for I/O.
 * 
 * @param <T> The desired type to be represented by this {@link IOHelper}.
 */
public class IOHelper<T> {

    /**
     * Attempt to read an object of the parameterized type from the specified
     * File.
     * 
     * @param file The File to read an Object from.
     * @return The loaded Object of the parameterized type, or <b>null</b> if
     *         there was an error during read.
     */
    @SuppressWarnings("unchecked")
    public T read(File file) {
        try (   FileInputStream f_in = new FileInputStream(file);
                ObjectInputStream in = new ObjectInputStream(f_in)) {
            return (T) in.readObject();
        } catch (Exception e) {
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
        try (   FileOutputStream f_out = new FileOutputStream(file);
                ObjectOutputStream out = new ObjectOutputStream(f_out);) {
            out.writeObject(object);
            out.flush();
            out.close();
            
            return true;
        } catch (IOException e) {
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
    public boolean write(T object, String file_path) {
        return write(object, new File(file_path));
    }
}
