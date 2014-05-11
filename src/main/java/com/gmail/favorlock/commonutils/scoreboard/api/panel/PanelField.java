package com.gmail.favorlock.commonutils.scoreboard.api.panel;

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
        if (!parent.hasField(entry)) {
            throw new IllegalStateException("This PanelField has been unregistered!");
        }
    }
    
    private void initialize() {
        updateEntry();
        updateValue();
    }
    
    private void updateEntry() {
        if (hidden)
            return;
        if (entry == null)
            throw new IllegalStateException("The entry for this PanelField is null!");
        
        parent.getObjective().setScoreFor(entry, entry_score);
    }
    
    private void updateValue() {
        if (hidden)
            return;
        if (value != null) {
            parent.getObjective().setScoreFor(value, value_score);
        }
    }
    
    private void changeEntry(String new_entry) {
        if (new_entry == null)
            throw new IllegalArgumentException("Cannot set a PanelField entry to null!");
        
        parent.getScoreboard().clearEntry(entry);
        this.entry = new_entry;
    }
    
    /**
     * Clear this PanelField's current value, if applicable.
     */
    public void clearValue() {
        checkstate();
        
        if (value != null) {
            parent.getScoreboard().clearEntry(value);
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
        clearValue();
        this.value = value;
        updateValue();
    }
    
    public void setHidden(boolean hidden) {
        if (this.hidden != hidden) {
            this.hidden = hidden;
            
            if (hidden) {
                parent.getScoreboard().clearEntry(entry);
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
}
