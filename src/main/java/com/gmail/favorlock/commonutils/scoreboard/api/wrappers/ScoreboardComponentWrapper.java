package com.gmail.favorlock.commonutils.scoreboard.api.wrappers;


/**
 * An interface implemented by ObjectiveProxy and TeamProxy wrappers.
 */
public interface ScoreboardComponentWrapper {

    // Full API Methods
    /**
     * Get the ScoreboardWrapper associated with this ScoreboardComponentWrapper.
     * 
     * @return The ScoreboardWrapper.
     */
    public ScoreboardWrapper getWrapper();
    
    /**
     * Get whether or not the ScoreboardComponent represented by this class is
     * still attached to a valid Scoreboard.
     * 
     * @return <b>true</b> if the Component is still valid, <b>false</b>
     *         otherwise.
     */
    public boolean isValid();
    
    // Wrapper Methods
    /**
     * Get the ScoreboardComponent's name, as it was registered with the
     * Scoreboard. This is not the name that Players see in-game.
     * 
     * @return The code name for the Component.
     */
    public String getCodeName();
    
    /**
     * Get the display name of the ScoreboardComponent represented by this object.
     * 
     * @return  The current display name of the Component.
     */
    public String getDisplay();
    
    /**
     * Set the display name of the ScoreboardComponent represented by this object.
     * 
     * @param name  The new display name to set.
     */
    public void setDisplay(String name);
    
    /**
     * Unregister the ScoreboardComponent from its Scoreboard. This
     * ScoreboardComponent's wrapper and it's Bukkit object will no longer be
     * valid after this call returns.
     */
    public void unregisterComponent();
}
