package com.gmail.favorlock.commonutils.text;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.gmail.favorlock.commonutils.scoreboard.api.criteria.ScoreboardCriterion;

/**
 * A class that provides utilities for creating JSON text elements that can be
 * displayed to players, with notable features like click and hover actions.
 */
public class RawMessage {

    private final String message;
    
    protected RawMessage(String message) {
        this.message = message;
    }
    
    /**
     * Get the message represented by this object.
     * 
     * @return The message.
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Send the raw text message represented by this object to the given
     * Player(s).
     * 
     * @param players The Player(s) to send the message to.
     */
    public void send(Player... players) {
        for (Player player : players) {
            send(player);
        }
    }
    
    /**
     * Send the raw text message represented by this object to all of the
     * players in the given collection.
     * 
     * @param players The Players to send the message to.
     */
    public void send(Collection<? extends Player> players) {
        for (Player player : players) {
            send(player);
        }
    }
    
    // There has to be a better way! This works, though.
    private void send(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                String.format("tellraw %s %s", player.getName(), getMessage()));
    }
    
    
    /**
     * Represents a builder for raw JSON text.
     */
    public static class Builder {
    
        private final Builder parent;
        private final String text;
        private Builder child;
        private String color;
        private boolean bold;
        private boolean underline;
        private boolean italic;
        private boolean strikethrough;
        private boolean obfuscate;
        private String insertion;
        private String clickevent;
        private String hoverevent;
        private boolean stopclickbleed;
        private boolean stophoverbleed;
        
        private Builder(String text) {
            this.parent = null;
            this.text = text;
            initDefaults();
        }
        
        private Builder(Builder parent, String text, boolean initClick, boolean initHover) {
            this.parent = parent;
            this.text = text;
            initDefaults();
            
            if (initClick)
                this.clickevent = "{action:suggest_command,value:\"\"}";
            
            if (initHover)
                this.hoverevent = "{action:show_text,value:\"\"}";
        }
        
        private void initDefaults() {
            this.child = null;
            this.color = null;
            this.bold = false;
            this.underline = false;
            this.italic = false;
            this.strikethrough = false;
            this.obfuscate = false;
            this.insertion = null;
            this.clickevent = null;
            this.hoverevent = null;
            this.stopclickbleed = true;
            this.stophoverbleed = true;
        }
        
        private String buildJSON() {
            String json = "[{";
            json += String.format("text:\"%s\"", text);
            
            if (color != null)
                json += String.format(",color:%s", color);
            if (bold)
                json += ",bold:true";
            if (underline)
                json += ",underline:true";
            if (italic)
                json += ",italic:true";
            if (strikethrough)
                json += ",strikethrough:true";
            if (obfuscate)
                json += ",obfuscate:true";
            if (insertion != null)
                json += String.format(",insertion:\"%s\"", insertion);
            if (clickevent != null)
                json += String.format(",clickEvent:%s", clickevent);
            if (hoverevent != null)
                json += String.format(",hoverEvent:%s", hoverevent);
            
            if (child != null) {
                String parent_built = child.buildJSON();
                
                json += String.format(",extra:%s", parent_built);
            }
            
            json += "}]";
            return json;
        }
        
        /**
         * Add ChatColors to the JSON, for this scope and deeper. It is not
         * required to go through this method, ChatColors will function as
         * expected if they are in any of the Strings used.
         * <p>
         * The formatting specified here will carry into any extra elements
         * within this one, unless ChatColor.RESET is passed at a later point,
         * or the color is overrided.
         * <p>
         * Colors cancel out any previous colors, while formatting (bold,
         * italic, underline, strikethrough, magic) can all be active at once,
         * if desired.
         * 
         * @param colors    The ChatColors to add to this RawMessage
         */
        public Builder withColor(ChatColor... colors) {
            for (ChatColor color : colors) {
                if (color.isColor() || color.equals(ChatColor.RESET)) {
                    this.color = color.name().toLowerCase();
                } else switch (color) {
                    case BOLD:
                        bold();
                        break;
                    case UNDERLINE:
                        underline();
                        break;
                    case ITALIC:
                        italic();
                        break;
                    case STRIKETHROUGH:
                        strikethrough();
                        break;
                    case MAGIC:
                        obfuscate();
                        break;
                    default:
                        break;
                }
            }
            
            return this;
        }
        
        private void bold() {
            this.bold = true;
        }
        
        private void underline() {
            this.underline = true;
        }
        
        private void italic() {
            this.italic = true;
        }
        
        private void strikethrough() {
            this.strikethrough = true;
        }
        
        private void obfuscate() {
            this.obfuscate = true;
        }
        
        /**
         * Add text to be inserted when the user shift-clicks this element.
         * 
         * @deprecated This won't work until 1.8. Currently, shift-clicking any
         *             tellraw text will insert the clicked word into the chat.
         * 
         * @param insert_text
         *            The text that should be inserted.
         */
        public Builder withInsertion(String insert_text) {
            this.insertion = insert_text;
            return this;
        }
        
        /**
         * Add a click event to this element; returns a builder for the click
         * event with appropriate options.
         * <p>
         * Any subsequent scopes will implement a click event of suggest_command
         * with the empty string to cancel the click event of this section, call
         * {@link ClickEventBuilder#allowBleed()} on the ClickEventBuilder to
         * prevent this behavior.
         */
        public ClickEventBuilder withClickEvent() {
            return new ClickEventBuilder(this);
        }
        
        /**
         * Add a hover event to this element; returns a builder for the hover
         * event with appropriate options.
         * <p>
         * Any subsequent scopes will implement a hover event of show_text with
         * the empty string to cancel the hover event of this section, call
         * {@link HoverEventBuilder#allowBleed()} on the HoverEventBuilder to
         * stop this behavior.
         */
        public HoverEventBuilder withHoverEvent() {
            return new HoverEventBuilder(this);
        }
        
        /**
         * Add an inner scope to this JSON, returns a new builder for that
         * scope.
         * 
         * @param extra_text    The text for the nested element.
         */
        public Builder withExtra(String extra_text) {
            this.child = new Builder(this, extra_text,
                    stopclickbleed && (clickevent != null), stophoverbleed && (hoverevent != null));
            return child;
        }
        
        /**
         * Build this RawMessage into JSON. This call will build any outer
         * scopes, if applicable, and then return a RawMessage with the
         * completed message.
         * 
         * @return A RawMessage object with the completed message.
         */
        public RawMessage build() {
            if (parent == null) {
                return new RawMessage(buildJSON());
            } else {
                return parent.build();
            }
        }
    }
    
    /**
     * Represents a builder for a raw JSON text click event.
     */
    public static class ClickEventBuilder {
        private final Builder builder;
        
        private ClickEventBuilder(Builder builder) {
            this.builder = builder;
        }
        
        public ClickEventBuilder allowBleed() {
            builder.stopclickbleed = false;
            return this;
        }
        
        public Builder withActionOpenURL(String url) {
            builder.clickevent = String.format(
                    "{action:open_url,value:\"%s\"}",
                    url);
            return builder;
        }
        
        public Builder withActionRunCommand(String command) {
            builder.clickevent = String.format(
                    "{action:run_command,value:\"%s\"}",
                    command);
            return builder;
        }
        
        public Builder withActionSuggestCommand(String command) {
            builder.clickevent = String.format(
                    "{action:suggest_command,value:\"%s\"}",
                    command);
            return builder;
        }
    }
    
    /**
     * Represents a builder for a raw JSON text hover event.
     */
    public static class HoverEventBuilder {
        private final Builder builder;
        
        private HoverEventBuilder(Builder builder) {
            this.builder = builder;
        }
        
        public HoverEventBuilder allowBleed() {
            builder.stophoverbleed = false;
            return this;
        }
        
        public Builder withActionText(String text) {
            builder.hoverevent = String.format(
                    "{action:show_text,value:\"%s\"}",
                    text);
            return builder;
        }
        
        public Builder withActionSimulateItem(int id, int data, String name, String... lore) {
            String lore_string = "";
            
            for (String l : lore) {
                lore_string += String.format("\\\"%s\\\",", l);
            }
            
            lore_string = lore_string.substring(0, lore_string.length() - 1);
            
            builder.hoverevent = String.format(
                    "{action:show_item,value:\"{id:%s,Damage:%s,tag:{display:{Name:\\\"%s\\\",Lore:[%s]}}}\"}",
                    id, data, name, lore_string);
            return builder;
        }
        
        @SuppressWarnings("deprecation")
        public Builder withActionItem(ItemStack item) {
            String lore_string = "";
            
            for (String lore : item.getItemMeta().getLore()) {
                lore_string += String.format("\\\"%s\\\",", lore);
            }
            
            lore_string = lore_string.substring(0, lore_string.length() - 1);
            
            builder.hoverevent = String.format(
                    "{action:show_item,value:\"{id:%s,Damage:%s,tag:{display:{Name:\\\"%s\\\",Lore:[%s]}}}\"}",
                    item.getType().getId(), item.getData().getData(), item.getItemMeta().getDisplayName(), lore_string);
            return builder;
        }
        
        public Builder withActionScoreboardCriterion(ScoreboardCriterion criterion) {
            builder.hoverevent = String.format(
                    "{action:show_achievement,value:\\\"%s\\\"}",
                    criterion.getCriterionString());
            return builder;
        }
    }
    
    /**
     * Get a builder for a new raw message.
     */
    public static RawMessage.Builder builder(String text) {
        return new RawMessage.Builder(text);
    }
}
