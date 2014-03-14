package com.gmail.favorlock.configuration;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class StrictModel extends ConfigModel {

	@Override
	public ConfigModel reload() throws InvalidConfigurationException {
		if (CONFIG_FILE == null)
			throw new InvalidConfigurationException(new NullPointerException());
		if (!CONFIG_FILE.exists())
			throw new InvalidConfigurationException(new IOException("File doesn't exist"));
		YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(CONFIG_FILE);
		try {
			String error = strictLoad(yamlConfig);
			yamlConfig.save(CONFIG_FILE);
			if (error != null)
				throw new InvalidConfigurationException(error);
		} catch (Exception ex) {
			throw new InvalidConfigurationException(ex);
		}
		return this;
	}

	@Override
	public ConfigModel save() throws InvalidConfigurationException {
		if (CONFIG_FILE == null)
			throw new InvalidConfigurationException(new NullPointerException());
		if (!CONFIG_FILE.exists()) {
			try {
				if (CONFIG_FILE.getParentFile() != null)
					CONFIG_FILE.getParentFile().mkdirs();
				CONFIG_FILE.createNewFile();
				if (CONFIG_HEADER != null) {
					Writer newConfig = new BufferedWriter(new FileWriter(CONFIG_FILE));
					boolean firstLine = true;
					for (String line : CONFIG_HEADER.split("\n")) {
						if (!firstLine) {
							newConfig.write("\n");
						} else {
							firstLine = false;
						}
						newConfig.write("# " + line);
					}
					newConfig.close();
				}
			} catch (Exception ex) {
				throw new InvalidConfigurationException(ex);
			}
		}
		YamlConfiguration yamlConfig = YamlConfiguration.loadConfiguration(CONFIG_FILE);
		try {
			String error = strictSave(yamlConfig);
			yamlConfig.save(CONFIG_FILE);
			if (error != null)
				throw new InvalidConfigurationException(error);
		} catch (Exception ex) {
			throw new InvalidConfigurationException(ex);
		}
		return this;
	}
	
	protected String strictLoad(ConfigurationSection cs) throws Exception {
		List<String> failedToDefine = new ArrayList<String>();
		
		for (Field field : getClass().getDeclaredFields()) {
			String path = field.getName().replaceAll("_", ".");
			
			if (doSkip(field)) {
				// Do nothing
			} else if (cs.isSet(path)) {
				field.set(this, field.getType().cast(loadObject(field, cs, path)));
			} else {
				Object value = field.get(this);
				if (value == null) {
					failedToDefine.add(
							String.format("'%s' of type %s", field.getName().replaceAll("_", "."), field.getType().getSimpleName()));
					continue;
				}
				cs.set(path, saveObject(value, field, cs, path));
			}
		}

		if (failedToDefine.size() > 0) {
			String error_out = "\nConfiguration file failed to define the following required fields:";
			for (String failed : failedToDefine) {
				error_out += "\n" + failed;
			}
			return error_out;
		} else {
			return null;
		}
	}

	protected String strictSave(ConfigurationSection cs) throws Exception {
		List<String> needsToDefine = new ArrayList<String>();
		
		for (Field field : getClass().getDeclaredFields()) {
			String path = field.getName().replaceAll("_", ".");
			
			if (doSkip(field)) {
				// Do nothing
			} else {
				Object value = field.get(this);
				if (value == null) {
					needsToDefine.add(
							String.format("'%s' of type %s", field.getName().replaceAll("_", "."), field.getType().getSimpleName()));
					continue;
				}
				cs.set(path, saveObject(value, field, cs, path));
			}
		}
		
		if (needsToDefine.size() > 0) {
			String error_out = "\nConfiguration file created; it now must define the following required fields:";
			for (String needs : needsToDefine) {
				error_out += "\n" + needs;
			}
			return error_out;
		} else {
			return null;
		}
	}
}
