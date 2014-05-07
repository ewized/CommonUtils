package com.gmail.favorlock.commonutils.scoreboard.api.panel;

/**
 * A Class for representing a field within a ScoreboardPanel.
 */
public class PanelField {

    private final ScoreboardPanel parent;
    private final String entry;
    private final int entry_score;
    private final int value_score;
    private String value;
    
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
        parent.getObjective().setScoreFor(entry, entry_score);
        updateValue();
    }
    
    private void updateValue() {
        if (value != null) {
            parent.getObjective().setScoreFor(value, value_score);
        }
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
     * Get the name that was used by the ScoreboardPanel to create this
     * PanelField.
     * 
     * @return This PanelField's name, as defined when it was registered with
     *         the ScoreboardPanel.
     */
    public String getName() {
        return entry;
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
