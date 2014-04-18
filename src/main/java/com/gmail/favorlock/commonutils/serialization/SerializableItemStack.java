package com.gmail.favorlock.commonutils.serialization;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

public class SerializableItemStack implements Serializable {

    private static final long serialVersionUID = 8907563934524049437L;
    
    private int amount;
    private String material;
    private byte data;
    private Map<String, Integer> enchantments;
    private String meta_display;
    private List<String> meta_lore;
    
    private transient ItemStack item = null;
    
    /**
     * Create a new {@link SerializableItemStack}.
     * <p>
     * The data will automatically be packed into a
     * serializable form, ready to save to file.
     * 
     * @param item The ItemStack to represent.
     */
    public SerializableItemStack(ItemStack item) {
        this.item = item;
        packItem();
    }
    
    /**
     * Get the ItemStack that this class represents.
     * @return The ItemStack
     */
    public ItemStack getItemStack() {
        if (item == null)
            unpackItem();
        
        return item;
    }
    
    @SuppressWarnings("deprecation")
    private void packItem() {
        this.material = item.getData().getItemType().toString();
        this.data = item.getData().getData();
        
        this.meta_display = item.getItemMeta().getDisplayName();
        this.meta_lore = new ArrayList<>(item.getItemMeta().getLore());
        
        this.enchantments = packEnchantments(item.getEnchantments());
        
        this.amount = item.getAmount();
    }
    
    private Map<String, Integer> packEnchantments(Map<Enchantment, Integer> enchants) {
        Map<String, Integer> packed = new HashMap<>();
        
        for (Map.Entry<Enchantment, Integer> enchant : enchants.entrySet()) {
            packed.put(enchant.getKey().getName(), enchant.getValue().intValue());
        }
        
        return packed;
    }
    
    @SuppressWarnings("deprecation")
    private void unpackItem() {
        try {
            Material m = Material.valueOf(material);
            this.item = new ItemStack(m);
            
            MaterialData mdata = new MaterialData(m, data);
            item.setData(mdata);
            
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(meta_display);
            meta.setLore(meta_lore);
            item.setItemMeta(meta);
            
            Map<Enchantment, Integer> enchants = unpackEnchantments(enchantments);
            item.addEnchantments(enchants);
            
            item.setAmount(amount);
        } catch (EnumConstantNotPresentException e) {
            throw new IllegalArgumentException(String.format(
                    "A SerializableItemStack was saved with an unknown material '%s'!", material));
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "An error occurred whilst unpacking a SerializableItemStack!");
        }
    }
    
    private Map<Enchantment, Integer> unpackEnchantments(Map<String, Integer> enchants) {
        Map<Enchantment, Integer> unpacked = new HashMap<>();
        
        for (Map.Entry<String, Integer> enchant : enchants.entrySet()) {
            Enchantment enchantment = Enchantment.getByName(enchant.getKey());
            
            if (enchantment == null)
                continue;
            
            unpacked.put(enchantment, enchant.getValue().intValue());
        }
        
        return unpacked;
    }
    
    /**
     * Save this SerializableItemStack to a file.
     * 
     * @param file  The File to save to.
     * @return <b>true</b> if successful,
     *   <b>false</b> otherwise.
     */
    public boolean save(File file) {
        try {
            if (!file.exists()) {
                File dir = file.getParentFile();

                if (dir != null)
                    dir.mkdirs();

                file.createNewFile();
            }
            
            FileOutputStream file_out = new FileOutputStream(file);
            ObjectOutputStream out = new ObjectOutputStream(file_out);
            
            out.writeObject(this);
            out.flush();
            out.close();
            
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    
    /**
     * Load a SerializableItemStack from a file.
     * <p>
     * The SerializableItemStack will automatically
     * be unpacked into its ItemStack form.
     * 
     * @param file  The File to load from.
     * @return The loaded SerializableItemStack,
     *   or null if errors occurred.
     */
    public static SerializableItemStack load(File file) {
        try {
            FileInputStream file_in = new FileInputStream(file);
            ObjectInputStream in = new ObjectInputStream(file_in);
            
            SerializableItemStack loaded = (SerializableItemStack) in.readObject();
            in.close();
            loaded.unpackItem();
            
            return loaded;
        } catch (Exception e) {
            return null;
        }
    }
}
