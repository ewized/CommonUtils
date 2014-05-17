package com.gmail.favorlock.commonutils.scoreboard.api.panel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;

import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.ObjectiveWrapper;
import com.gmail.favorlock.commonutils.scoreboard.api.wrappers.ScoreboardWrapper;
import com.gmail.favorlock.commonutils.scoreboard.impl.CraftScoreboardWrapper;

/**
 * A Class for representing a ScoreboardAPI Scoreboard that is used for showing
 * pairs of String entries and String values on a Scoreboard.
 */
public class ScoreboardPanel {

    private static final Map<String, ScoreboardPanel> panel_registry = new HashMap<>();
    
    private final ObjectiveWrapper objective;
    private final Map<String, PanelField> panel_fields;
    private final List<PanelSpacer> spacers;
    private final String label;
    private String display;
    private String spacer;
    private int highest_bound;
    private int lowest_bound;
    private boolean registered;
    
    private ScoreboardPanel(ScoreboardWrapper scoreboard, String label) {
        this.objective = scoreboard.registerObjective("panel_sidebar");
        this.panel_fields = new HashMap<>();
        this.spacers = new ArrayList<>();
        this.label = label;
        this.display = label;
        this.spacer = ChatColor.RESET.toString();
        this.highest_bound = -1;
        this.lowest_bound = -1;
        this.registered = true;
        
        initialize();
    }
    
    private void checkstate() {
        if (!registered) {
            throw new IllegalStateException("This ScoreboardPanel has been unregistered!");
        }
    }
    
    private void initialize() {
        objective.setDisplaySlotSideBar();
        objective.setDisplay(display);
    }
    
    /**
     * Get the ScoreboardWrapper that this ScoreboardPanel is using.
     * 
     * @return The ScoreboardWrapper.
     */
    public ScoreboardWrapper getScoreboard() {
        return objective.getWrapper();
    }
    
    /**
     * Get the ObjectiveWrapper of the objective that this ScoreboardPanel is
     * using.
     * 
     * @return The ObjectiveWrapper.
     */
    public ObjectiveWrapper getObjective() {
        return objective;
    }
    
    /**
     * Get the label of this ScoreboardPanel.
     * 
     * @return The label of this ScoreboardPanel, as was defined when it was
     *         registered.
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Get the display name of this ScoreboardPanel. This is initialized as the
     * label, however this can be manually set with
     * {@link ScoreboardPanel#setDisplay(String)}.
     * 
     * @return The current display name of this ScoreboardPanel.
     */
    public String getDisplay() {
        return display;
    }
    
    /**
     * Get whether or not this ScoreboardPanel is still registered. This will be
     * false after {@link ScoreboardPanel#unregister()} has been called.
     * 
     * @return <b>true</b> if this ScoreboardPanel has not bee unregistered,
     *         <b>false</b> otherwise.
     */
    public boolean isRegistered() {
        return registered;
    }
    
    /**
     * Get whether or not the given field is currently present on this
     * ScoreboardPanel
     * 
     * @param field The field to check the presence of.
     * @return <b>true</b> if the field is registered, <b>false</b>
     *         otherwise.
     */
    protected boolean hasField(PanelField field) {
        checkstate();
        
        for (PanelField registered : panel_fields.values()) {
            if (registered.equals(field)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Get whether or not a field of the given name is currently present on this
     * ScoreboardPanel.
     * 
     * @param field_name The name of the field to look for.
     * @return <b>true</b> if a field of the given name is registered,
     *         <b>false</b> otherwise.
     */
    public boolean hasField(String field_name) {
        checkstate();
        return panel_fields.get(field_name) != null;
    }
    
    /**
     * Get an existing PanelField for the given field name.
     * 
     * @param field_name The name of the existing field.
     * @return The PanelField for the given field name, or <b>null</b> if there
     *         is no field of the given name.
     */
    public PanelField getField(String field_name) {
        checkstate();
        
        if (panel_fields.get(field_name) != null) {
            return panel_fields.get(field_name);
        } else {
            return null;
        }
    }
    
    /**
     * Get whether or not there is a spacer at the given index.
     * 
     * @param index The index, which corresponds to the order in which the spacers
     *              were registered.
     * @return <b>true</b> if a field is registered for the given field name and is
     *         an extended field, <b>false</b> otherwise.
     */
    public boolean hasSpacer(int index) {
        checkstate();
        return getSpacer(index) != null;
    }
    
    /**
     * Get the spacer at the given index.
     * 
     * @param index The index, which corresponds to the order in which the spacers
     *              were registered.
     * @return The PanelField that is being used as a spacer, or <b>null</b> if no
     *         spacer is under the given index.
     */
    public PanelSpacer getSpacer(int index) {
        if (index < 0 || index > this.spacers.size()) {
            return null;
        }
        
        return this.spacers.get(index);
    }
    
    /**
     * Register a spacer following the default ordering behavior (added to
     * bottom of the scoreboard).
     * 
     * @return The PanelSpacer that has been created.
     */
    public PanelSpacer registerSpacer() {
        return registerSpacer(false);
    }
    
    /**
     * Register a spacer, following the ordering behavior specified.
     * 
     * @param add_to_top If <b>true<b>, the spacer will be added to the top of
     *                   the ScoreboardPanel, rather than the bottom.
     * @return The PanelSpacer that has been created.
     */
    public PanelSpacer registerSpacer(boolean add_to_top) {
        checkstate();
        
        int entry_score;
        
        if (add_to_top) {
            highest_bound += 1;
            entry_score = highest_bound;
        } else {
            lowest_bound -= 1;
            entry_score = lowest_bound;
        }
        
        PanelSpacer field = new PanelSpacer(this, this.spacer, entry_score);
        this.spacer += ChatColor.RESET.toString();
        this.spacers.add(field);
        return field;
    }
    
    /**
     * Register a new PanelField by the given name. If a PanelField already
     * exists by the given name, none will be created, and the existing one will
     * be returned. The PanelField will be initialized without a value, and will
     * follow the default ordering behavior (added to bottom of the Scoreboard).
     * <p>
     * <b>Note</b>: The field_name will be the exact String that players will
     * see in-game.
     * 
     * @param field_name The name of the field to register.
     * @return The PanelField that has been created or found for the given name.
     */
    public PanelField registerField(String field_name) {
        return registerField(field_name, null, false);
    }
    
    /**
     * Register a new PanelField by the given name. If a PanelField already
     * exists by the given name, none will be created, and the existing one will
     * be returned. The PanelField will be initialized with the given value, and
     * will follow the default ordering behavior (added to bottom of Scoreboard).
     * <p>
     * <b>Note</b>: The field_name will be the exact String that players will
     * see in-game.
     * 
     * @param field_name The name of the field to register.
     * @param init_value The initial value of the field, or <b>null</b> for none.
     * @return The PanelField that has been created or found for the given name.
     */
    public PanelField registerField(String field_name, String init_value) {
        return registerField(field_name, init_value, false);
    }
    
    /**
     * Register a new PanelField by the given name. If a PanelField already
     * exists by the given name, none will be created, and the existing one will
     * be returned. The PanelField will be initialized without a value, and will
     * follow the ordering behavior specified.
     * <p>
     * <b>Note</b>: The field_name will be the exact String that players will
     * see in-game.
     * 
     * @param field_name The name of the field to register.
     * @param add_to_top If <b>true<b>, the PanelField will be added to the top of
     *                   the ScoreboardPanel, rather than the bottom.
     * @return The PanelField that has been created or found for the given name.
     */
    public PanelField registerField(String field_name, boolean add_to_top) {
        return registerField(field_name, null, add_to_top);
    }
    
    /**
     * Register a new PanelField by the given name. If a PanelField already
     * exists by the given name, none will be created, and the existing one will
     * be returned. The PanelField will be initialized with the given value, and
     * will follow the ordering behavior specified.
     * <p>
     * <b>Note</b>: The field_name will be the exact String that players will
     * see in-game.
     * 
     * @param field_name The name of the field to register.
     * @param init_value The initial value of the field, or <b>null</b> for none.
     * @param add_to_top If <b>true<b>, the PanelField will be added to the top of
     *                   the ScoreboardPanel, rather than the bottom.
     * @return The PanelField that has been created or found for the given name.
     */
    public PanelField registerField(String field_name, String init_value, boolean add_to_top) {
        checkstate();
        
        if (field_name.length() > PanelField.getMaxStringLength()) {
            throw new IllegalArgumentException(String.format(
                    "Cannot utilize a String with length greater than 48! (given %s)", field_name.length()));
        }
        
        if (panel_fields.get(field_name) != null) {
            return panel_fields.get(field_name);
        } else {
            int entry_score;
            
            if (add_to_top) {
                highest_bound += 2;
                entry_score = highest_bound;
            } else {
                lowest_bound -= 2;
                entry_score = lowest_bound;
            }
            
            PanelField created = new PanelField(this, field_name, entry_score, init_value);
            panel_fields.put(field_name, created);
            return created;
        }
    }
    
    /**
     * Set this Objective display name for this ScoreboardPanel.
     * 
     * @param display The display name to set.
     */
    public void setDisplay(String display) {
        this.display = display;
        objective.setDisplay(display);
    }
    
    /**
     * Unregister a given PanelField. {@link PanelField#unregister()} calls
     * this.
     * 
     * @param field The PanelField to unregister.
     */
    protected void unregister(PanelField field) {
        checkstate();
        
        panel_fields.remove(field.getEntry());
    }
    
    /**
     * Change the name of the given PanelField to the given new entry.
     * 
     * @param field     The PanelField, ensure that {@link PanelField#getEntry()}
     *                  still returns the old entry.
     * @param new_entry The new entry that should be used.
     */
    protected void changeName(PanelField field, String new_entry) {
        panel_fields.remove(field.getEntry());
        panel_fields.put(new_entry, field);
    }
    
    public void unregister() {
        checkstate();
        
        for (Map.Entry<String, PanelField> field : panel_fields.entrySet()) {
            PanelField panel_field = field.getValue();
            panel_field.unregister();
        }
        
        this.registered = false;
        this.objective.getWrapper().unregisterWrapper();
    }
    
    
    /**
     * Get a ScoreboardPanel for the given label.
     * <p>
     * If a ScoreboardPanel for the given label already exists, the existing
     * ScoreboardPanel will be returned.
     * <p>
     * Otherwise, one will be created with the ScoreboardAPI Scoreboard of the
     * given label. If an existing Scoreboard is used, caution should be taken
     * to ensure that no other objective will be using the sidebar slot.
     * 
     * @param label The label of the ScoreboardAPI Scoreboard that should be used
     *              for this ScoreboardPanel.
     * @return A new ScoreboardPanel that runs on the ScoreboardAPI Scoreboard
     *         of the given label.
     */
    public static ScoreboardPanel getPanel(String label) {
        if (panel_registry.get(label) != null) {
            return panel_registry.get(label);
        } else {
            ScoreboardPanel created = new ScoreboardPanel(CraftScoreboardWrapper.getCustomScoreboardWrapper(label), label);
            panel_registry.put(label, created);
            return created;
        }
    }
}
