package com.archeinteractive.dev.commonutils.scoreboard.api.panel;

public class PanelSpacer extends PanelField {

    protected PanelSpacer(ScoreboardPanel parent, String entry, int entry_score) {
        super(parent, entry, entry_score, null);
    }
    
    public void clearValue() {
        // This is a spacer, the value should never have been set.
    }
    
    public String getValue() {
        return null;
    }
    
    public boolean hasValue() {
        return false;
    }
    
    public void setEntry(String entry) {
        // This is a spacer, and shouldn't have it's entry changed.
    }
    
    public void setValue(String value) {
        // This is a spacer, and shouldn't have a value set.
    }
}
