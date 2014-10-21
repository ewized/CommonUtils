package com.archeinteractive.dev.commonutils.ui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PaginatedMenu extends MenuHolder {

    private MenuItem[][] true_items;
    private Inventory[] inventories;
    private String title;
    private int rows;
    
    public PaginatedMenu(String title, int rows, int initial_pages) {
        super(9 * rows);
        
        if (initial_pages < 1) {
            throw new IllegalArgumentException("Cannot instantiate a PaginatedMenu with a nonpositive page count!");
        }
        
        this.true_items = new MenuItem[initial_pages][getMaxItems()];
        this.title = title;
        this.rows = rows;
        this.inventories = new Inventory[initial_pages];
        
        for (int i = 0; i < initial_pages; i++) {
            inventories[i] = Bukkit.createInventory(this, 9 * rows, title);
        }
    }
    
    @SuppressWarnings("deprecation")
    protected void selectMenuItem(Inventory inventory, Player player, int index) {
        if (index > -1 && index < getMaxItems()) {
            for (int i = 0; i < inventories.length; i++) {
                Inventory inv = inventories[i];
                
                if (inv == null)
                    continue;
                
                if (inv.hashCode() == inventory.hashCode()) {
                    // We have a match, and ideally no duplicate Inventories will be in the array
                    MenuItem item = true_items[i][index];
                    
                    if (item != null)
                        item.onClick(player);
                    
                    break;
                }
            }
        }
        
        player.updateInventory();
    }
    
    /**
     * Open the Inventory for the given Player, defaulting to the first (zero-index) page.
     * 
     * @param player The Player to open for.
     */
    public void openMenu(Player player) {
        openMenu(0, player);
    }
    
    /**
     * Open the Inventory for the given Player, on the given page.
     * 
     * @param page   The page to open.
     * @param player The Player to open for.
     */
    public void openMenu(int page, Player player) {
        boolean open = isViewingPage(page, player);
        
        if (!open) {
            player.openInventory(getInventory(page));
        } else throw new IllegalStateException(player.getName() + " is already viewing " + getInventory(page).getTitle());
    }
    
    /**
     * Closes the Player's open inventory, provided that they are viewing a page
     * of this Menu.
     * 
     * @param player The Player to close for.
     */
    public void closeMenu(Player player) {
        boolean open = isViewingAny(player);
        
        if (open) {
            player.closeInventory();
        }
    }
    
    /**
     * Update this PaginatedMenu, for all viewers of all pages.
     */
    @SuppressWarnings("deprecation")
    public void updateMenu() {
        for (int i = 0; i < inventories.length; i++) {
            Inventory inv = inventories[i];
            
            if (inv == null)
                continue;
            
            for (HumanEntity player : inv.getViewers()) {
                if (player instanceof Player) {
                    ((Player) player).updateInventory();
                }
            }
        }
    }
    
    /**
     * Update this PaginatedMenu for all viewers of the given page.
     * 
     * @param page The page to update viewers of.
     */
    @SuppressWarnings("deprecation")
    public void updateMenu(int page) {
        for (HumanEntity player : getInventory(page).getViewers()) {
            if (player instanceof Player) {
                ((Player) player).updateInventory();
            }
        }
    }
    
    /**
     * Get whether or not the given player is currently viewing the given page
     * on this Menu.
     * 
     * @param page   The page to check.
     * @param player The Player to check.
     * @return <b>true</b> if the given Player is viewing the given page,
     *         <b>false</b> otherwise.
     */
    public boolean isViewingPage(int page, Player player) {
        return getInventory(page).getViewers().contains(player);
    }
    
    /**
     * Get whether or not the given player is currently viewing any page on this
     * Menu.
     * 
     * @param player The Player to check.
     * @return <b>true</b> if the given Player is viewing any page of this Menu,
     *         <b>false</b> otherwise.
     */
    public boolean isViewingAny(Player player) {
        for (int i = 0; i < inventories.length; i++) {
            Inventory inv = inventories[i];
            
            if (inv == null)
                continue;
            
            if (inv.getViewers().contains(player)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get the Inventory, defaulting to the first (zero-index) page.
     */
    public Inventory getInventory() {
        return getInventory(0);
    }
    
    /**
     * Get the Inventory for the given page.
     * 
     * @param page  The page that should be returned.
     * @return An Inventory for the given page.
     */
    public Inventory getInventory(int page) {
        if (page < 0 || page >= getPageCount())
            return null;
        
        Inventory inv = inventories[page];
        
        if (inv == null) {
            inventories[page] = Bukkit.createInventory(this, 9 * rows, title);
            inv = inventories[page];
        }
        
        return inv;
    }
    
    /**
     * Get the total number of pages currently in this PaginatedMenu.
     * 
     * @return The total number of pages.
     */
    public int getPageCount() {
        return true_items.length;
    }
    
    /**
     * Get the total number of items per page in this menu; this is simply nine
     * times the amount of rows that were given in the constructor.
     * 
     * @return The number of items per page.
     */
    public int getItemsPerPage() {
        return getMaxItems();
    }
    
    /**
     * Set the given MenuItem[] to the given page, specifying whether to
     * completely overwrite the page or not. The operation will fail if the
     * given MenuItem[] is not of the appropriate length; use
     * {@link PaginatedMenu#getItemsPerPage()} to get this length.
     * 
     * @param page      The page to change.
     * @param set_to    The MenuItem[] to use to make the change.
     * @param overwrite If false, existing items on the page will not be
     *                  overwritten, only currently null items will be
     *                  replaced with those from the given MenuItem[].
     * @return <b>true</b> if the operation was successful, <b>false</b>
     *         otherwise.
     */
    public boolean setPage(int page, MenuItem[] set_to, boolean overwrite) {
        if (set_to.length != getMaxItems())
            return false;
        
        if (overwrite) {
            true_items[page] = set_to.clone();
            for (int i = 0; i < getMaxItems(); i++) {
                removeMenuItem(page, i);
                
                if (set_to[i] == null)
                    continue;
                
                addMenuItem(page, set_to[i], i);
            }
        } else {
            for (int i = 0; i < getMaxItems(); i++) {
                if (set_to[i] != null)
                    addMenuItem(page, set_to[i], i);
            }
        }
        
        return true;
    }
    
    /**
     * Add a new page and subsequently assign it items based on the given
     * MenuItem[].
     * 
     * @param new_page
     *            A MenuItem[] of an appropriate length for this PaginatedMenu;
     *            use {@link PaginatedMenu#getItemsPerPage()} to find the
     *            appropriate length.
     * @return <b>true</b> if the operation was successful, <b>false</b>
     *         otherwise.
     */
    public boolean addPage(MenuItem[] new_page) {
        if (getMaxItems() != new_page.length)
            return false;
        
        addPages(1);
        int page = getPageCount() - 1;
        
        for (int i = 0; i < getMaxItems(); i++) {
            if (new_page[i] == null)
                continue;
            
            addMenuItem(page, new_page[i], i);
        }
        
        return true;
    }
    
    /**
     * Add one page to this PaginatedMenu; the page will be initialized as
     * empty.
     */
    public void addPage() {
        addPages(1);
    }
    
    /**
     * Add the given number of pages to this PaginatedMenu; the pages will be
     * initialized as empty.
     * 
     * @param count The number of pages to add.
     */
    public void addPages(int count) {
        MenuItem[][] new_items = new MenuItem[true_items.length + count][getMaxItems()];
        
        for (int i = 0; i < getPageCount(); i++) {
            new_items[i] = true_items[i];
        }
        
        Inventory[] new_inventories = new Inventory[true_items.length + count];
        
        for (int i = 0; i < inventories.length; i++) {
            new_inventories[i] = inventories[i];
        }
        
        for (int i = inventories.length; i < new_inventories.length; i++) {
            new_inventories[i] = Bukkit.createInventory(this, 9 * rows, title);
        }
        
        this.true_items = new_items;
        this.inventories = new_inventories;
    }
    
    /**
     * Add a MenuItem to the given index; defaults to the first (zero-index) page.
     */
    public boolean addMenuItem(MenuItem item, int index) {
        return addMenuItem(0, item, index);
    }
    
    /**
     * Add a MenuItem to the given index, on the given page.
     */
    public boolean addMenuItem(int page, MenuItem item, int index) {
        if (page < 0 || page >= getPageCount())
            return false;
        
        ItemStack slot = getInventory(page).getItem(index);
        
        if (slot != null && !slot.getType().equals(Material.AIR)) {
            return false;
        } else if (index < 0 || index >= getMaxItems()) {
            return false;
        }
        
        getInventory(page).setItem(index, item.getItemStack());
        true_items[page][index] = item;
        item.addToMenu(this);
        
        return true;
    }
    
    /**
     * Remove the MenuItem at the given index; defaults to the first (zero-index) page.
     */
    public boolean removeMenuItem(int index) {
        return removeMenuItem(0, index);
    }
    
    /**
     * Remove the MenuItem at the given index, on the given page.
     */
    public boolean removeMenuItem(int page, int index) {
        if (page < 0 || page >= getPageCount())
            return false;
        
        ItemStack slot = getInventory(page).getItem(index);
        
        if (slot == null || slot.getType().equals(Material.AIR)) {
            return false;
        } else if (index < 0 || index >= getMaxItems()) {
            return false;
        }
        
        getInventory(page).clear(index);
        MenuItem remove = true_items[page][index];
        true_items[page][index] = null;
        remove.removeFromMenu(this);
        
        return true;
    }
    
    /**
     * Get a Menu that represents the given page of this PaginatedMenu.
     * 
     * @param page The page to convert to a Menu.
     * @return The page as a standalone Menu.
     */
    public Menu toMenu(int page) {
        Menu menu = new Menu(title, rows);
        menu.setExitOnClickOutside(exitOnClickOutside);
        menu.setMenuCloseBehavior(menuCloseBehavior);
        menu.items = true_items[page].clone();
        
        return menu;
    }
    
    protected PaginatedMenu clone() {
        PaginatedMenu clone = new PaginatedMenu(title, rows, getPageCount());
        clone.setExitOnClickOutside(exitOnClickOutside);
        clone.setMenuCloseBehavior(menuCloseBehavior);
        clone.true_items = true_items.clone();
        clone.inventories = inventories.clone();
        
        return clone;
    }
}
