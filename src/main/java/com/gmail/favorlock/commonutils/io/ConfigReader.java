package com.gmail.favorlock.commonutils.io;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.configuration.InvalidConfigurationException;

import com.gmail.favorlock.commonutils.configuration.ConfigModel;

/**
 * A Class for reading ConfigModel configurations from files.
 * 
 * @param <T> The Class that extends ConfigModel that will be represented.
 */
public class ConfigReader<T extends ConfigModel> {

    protected final Class<T> config_class;
    protected final Constructor<T> config_constructor;
    protected final Object[] constructor_args;
    
    public ConfigReader(Class<T> config_class) {
        if (config_class == null) {
            throw new IllegalArgumentException("The given class may not be null!");
        }
        
        this.config_class = config_class;
        this.config_constructor = null;
        this.constructor_args = null;
    }
    
    public ConfigReader(Constructor<T> config_constructor, Object... constructor_args) {
        if (config_constructor == null) {
            throw new IllegalArgumentException("The given constructor may not be null!");
        }
        
        this.config_class = config_constructor.getDeclaringClass();
        this.config_constructor = config_constructor;
        this.constructor_args = constructor_args == null ? new Object[0] : constructor_args;
    }
    
    /**
     * Attempt to instantiate the config.
     * 
     * @return The instantiated config, or null if an exception was thrown.
     */
    protected T instantiate() {
        T config = null;
        
        try {
            if (isConstructorSpecified()) {
                config = this.config_constructor.newInstance(this.constructor_args);
            } else {
                config = this.config_class.newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            config = null;
        }
        
        return config;
    }
    
    /**
     * Get an instance of the default configuration for the Class that was
     * specified for this ConfigReader.
     * 
     * @return An instance of the Class that extends ConfigModel, as it was
     *         immediately after instantiation.
     */
    public T getDefaultConfig() {
        return instantiate();
    }
    
    /**
     * Get the Class of the ConfigModel type that is being used to load from.
     * 
     * @return The Class.
     */
    public Class<T> getConfigClass() {
        return this.config_class;
    }
    
    /**
     * Get whether or not this ConfigReader was instantiated with a specified
     * Constructor for instantiating the Class that extends ConfigModel with.
     * 
     * @return <b>true</b> if a specific Constructor was given, or <b>false</b>
     *         if using the nullary Constructor was implied.
     */
    public boolean isConstructorSpecified() {
        return this.config_constructor != null && this.constructor_args != null;
    }
    
    /**
     * Get the Constructor that was specified at the creation of this
     * ConfigReader, if applicable.
     * 
     * @return The Constructor that was used, or <b>null</b> if this
     *         ConfigReader was created without specifying a Constructor.
     */
    public Constructor<T> getConstructor() {
        return config_constructor;
    }
    
    /**
     * Get the arguments for the Constructor of this ConfigReader. If this
     * ConfigReader was not instantiated with a specified Constructor, null will
     * be returned. If non-null, this Object[] may be modified to change which
     * arguments will be used for any subsequent configuration instantiations.
     * 
     * @return The Object[] containing elements that will be used to instantiate
     *         the ConfigModel class, or <b>null</b> if this ConfigReader was
     *         created without specifying a constructor (and the nullary
     *         Constructor was implied).
     */
    public Object[] getConstructorArguments() {
        return constructor_args;
    }
    
    /**
     * Set the arguments for the Constructor of this ConfigReader. If this
     * ConfigReader was not instantiated with a specified Constructor, this
     * method will have no effect.
     * 
     * @param args The new arguments to set, if applicable.
     * @return This ConfigReader instance, for chaining.
     */
    public ConfigReader<T> setConstructorArguments(Object... args) {
        if (isConstructorSpecified()) {
            for (int i = 0; i < Math.min(constructor_args.length, args.length); i++) {
                constructor_args[i] = args[i];
            }
        }
        
        return this;
    }
    
    /**
     * Read a config instance from the given file path, returning the default
     * config in any case of InvalidConfigurationException.
     * 
     * @throws RuntimeException
     *             If a config instance fails to be instantiated.
     * 
     * @param file The File to attempt to load a config from.
     * @return The loaded config, never null.
     */
    public T readConfig(String file_path) {
        return readConfig(new File(file_path));
    }
    
    /**
     * Read a config instance from the given file, returning the default config
     * in any case of InvalidConfigurationException.
     * 
     * @throws RuntimeException
     *             If a config instance fails to be instantiated.
     * 
     * @param file The File to attempt to load a config from.
     * @return The loaded config, never null.
     */
    public T readConfig(File file) {
        T config = instantiate();
        
        if (config == null) {
            throw new RuntimeException("Failed to instantiate config!");
        }
        
        try {
            config.load(file);
        } catch (InvalidConfigurationException e) {
            // If an error occurred during loading, return default.
            config = instantiate();
            
            if (config == null) {
                throw new RuntimeException("Failed to instantiate config!");
            }
        }
        
        return config;
    }
    
    /**
     * Read a config instance from the given file path, returning null in any
     * case of InvalidConfigurationException.
     * 
     * @throws RuntimeException
     *             If a config instance fails to be instantiated.
     * 
     * @param file The File to attempt to load a config from.
     * @return The loaded config, or null if any errors occurred.
     */
    public T readStrict(String file_path) {
        return readStrict(new File(file_path));
    }
    
    /**
     * Read a config instance from the given file, returning null in any case of
     * InvalidConfigurationException.
     * 
     * @throws RuntimeException
     *             If a config instance fails to be instantiated.
     * 
     * @param file The File to attempt to load a config from.
     * @return The loaded config, or null if any errors occurred.
     */
    public T readStrict(File file) {
        T config = instantiate();
        
        if (config == null) {
            throw new RuntimeException("Failed to instantiate config!");
        }
        
        try {
            config.load(file);
            return config;
        } catch (InvalidConfigurationException e) {
            return null;
        }
    }
}
