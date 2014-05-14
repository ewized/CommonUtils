package com.gmail.favorlock.commonutils.scoreboard.api.panel;

import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.TeamWrapper;

/**
 * A Class for representing a field within a ScoreboardPanel.
 * <p>
 * Nomenclature:
 * <li> The Field is the object, the key and value
 * <li> The Entry is the key, and is the name that players will
 *      see on a Scoreboard, above the value, if applicable.
 * <li> The value is the name that players will see below the
 *      entry on a Scoreboard, if null, it won't be displayed.
 */
public class PanelField {

    private static final int MAX_LENGTH = 48;
    
    private final ScoreboardPanel parent;
    private final int entry_score;
    private final int value_score;
    private String entry;
    private String value;
    private boolean hidden;
    
    protected PanelField(ScoreboardPanel parent, String entry, int entry_score, String value) {
        if (parent == null)
            throw new IllegalArgumentException("ScoreboardPanel cannot be null!");
        if (entry == null)
            throw new IllegalArgumentException("A PanelField's entry cannot be null!");
        
        this.parent = parent;
        this.entry = entry;
        this.entry_score = entry_score;
        this.value_score = entry_score - 1;
        this.value = value;
        
        initialize();
    }
    
    private void checkstate() {
        if (!parent.hasField(this)) {
            throw new IllegalStateException("This PanelField has been unregistered!");
        }
    }
    
    private void initialize() {
        updateEntry();
        updateValue();
    }
    
    void updateEntry() {
        if (hidden)
            return;
        if (entry == null)
            throw new IllegalStateException("The entry for this PanelField is null!");
        
        if (entry.length() > 16) {
            String main_entry;
            String entry_prefix;
            String entry_suffix;
            
            if (entry.length() > 32) {
                entry_prefix = entry.substring(0, 16);
                main_entry = entry.substring(16, 32);
                entry_suffix = entry.substring(32);
            } else {
                entry_prefix = "";
                main_entry = entry.substring(0, 16);
                entry_suffix = entry.substring(16);
            }
            
            parent.getObjective().formatExtended(entry_prefix, main_entry, entry_suffix, entry_score);
        } else {
            parent.getObjective().setScoreFor(entry, entry_score);
        }
    }
    
    void updateValue() {
        if (hidden)
            return;
        if (value == null)
            return;
        
        if (value.length() > 16) {
            String main_value;
            String value_prefix;
            String value_suffix;
            
            if (value.length() > 32) {
                value_prefix = value.substring(0, 16);
                main_value = value.substring(16, 32);
                value_suffix = value.substring(32);
                
            } else {
                value_prefix = "";
                main_value = value.substring(0, 16);
                value_suffix = value.substring(16);
            }
            
            parent.getObjective().formatExtended(value_prefix, main_value, value_suffix, value_score);
        } else {
            parent.getObjective().setScoreFor(value, value_score);
        }
    }
    
    private void changeEntry(String new_entry) {
        if (new_entry == null)
            throw new IllegalArgumentException("Cannot set a PanelField entry to null!");
        
        parent.getScoreboard().clearEntry(entry);
        TeamWrapper team = parent.getScoreboard().getTeamForEntry(entry);
        
        if (team != null)
            team.removeEntry(entry);
        
        this.entry = new_entry;
    }
    
    /**
     * Clear this PanelField's current value, if applicable.
     */
    public void clearValue() {
        checkstate();
        
        if (value != null) {
            parent.getScoreboard().clearEntry(value);
            TeamWrapper team = parent.getScoreboard().getTeamForEntry(value);
            
            if (team != null)
                team.removeEntry(value);
            
            this.value = null;
        }
    }
    
    /**
     * Get the ScoreboardPanel associated with this PanelField.
     * 
     * @return The ScoreboardPanel.
     */
    public ScoreboardPanel getPanel() {
        return parent;
    }
    
    /**
     * Get the name is being used as the entry name for this PanelField.
     * 
     * @return This PanelField's name, as defined when it was registered with
     *         the ScoreboardPanel.
     */
    public String getEntry() {
        return entry;
    }
    
    /**
     * Set the name that should be used as the entry name for this PanelField.
     * 
     * @param entry The name that should be used for this PanelField's entry.
     */
    public void setEntry(String entry) {
        checkstate();
        
        if (entry.length() > 48)
            throw new IllegalArgumentException("Cannot set the entry to a String longer than 48 characters!");
        
        parent.changeName(this, entry);
        changeEntry(entry);
        updateEntry();
    }
    
    /**
     * Get whether or not this PanelField currently has a displayed value.
     * 
     * @return <b>true</b> if there is currently a value being displayed,
     *         <b>false</b> otherwise.
     */
    public boolean hasValue() {
        checkstate();
        
        return value != null;
    }
    
    /**
     * Get this PanelField's currently displayed value.
     * 
     * @return The current value, or <b>null</b> if the current value is null.
     */
    public String getValue() {
        return value;
    }
    
    /**
     * Set this PanelField's value to a new value.
     * 
     * @param value The new value to set. If <b>null</b>, this call is
     *              synonymous with {@link PanelField#clearValue()}.
     */
    public void setValue(String value) {
        checkstate();
        
        if (value.length() > 48)
            throw new IllegalArgumentException("Cannot set the entry to a String longer than 48 characters!");
        
        clearValue();
        this.value = value;
        updateValue();
    }
    
    /**
     * Get whether or not this PanelField is currently hidden.
     * 
     * @return <b>true</b> if this PanelField is currently hidden, <b>false</b>
     *         otherwise.
     */
    public boolean isHidden() {
        return this.hidden;
    }
    
    /**
     * Set whether or not this PanelField should be hidden.
     * 
     * @param hidden If true, this PanelField will be hidden.
     */
    public void setHidden(boolean hidden) {
        if (this.hidden != hidden) {
            this.hidden = hidden;
            
            if (hidden) {
                parent.getScoreboard().clearEntry(entry);
                
                if (value != null)
                    parent.getScoreboard().clearEntry(value);
            } else {
                updateEntry();
                updateValue();
            }
        }
    }
    
    /**
     * Unregister this PanelField from the ScoreboardPanel.
     */
    public void unregister() {
        checkstate();
        clearValue();
        parent.getScoreboard().clearEntry(entry);
        parent.unregister(this);
    }
    
    /**
     * Get the maximum length of a String that this PanelField implementation
     * can utilize.
     * 
     * @return The maximum String length.
     */
    public static int getMaxStringLength() {
        return MAX_LENGTH;
    }
}
