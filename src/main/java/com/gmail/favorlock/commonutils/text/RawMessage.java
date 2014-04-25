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
        private String color;
        private boolean bold;
        private boolean underline;
        private boolean italic;
        private boolean strikethrough;
        private boolean obfuscate;
        private String insertion;
        private String clickevent;
        private String hoverevent;
        
        private Builder(String text) {
            this.parent = null;
            this.text = text;
            initDefaults();
        }
        
        private Builder(Builder parent, String text) {
            this.parent = parent;
            this.text = text;
            initDefaults();
        }
        
        private void initDefaults() {
            this.color = null;
            this.bold = false;
            this.underline = false;
            this.italic = false;
            this.strikethrough = false;
            this.obfuscate = false;
            this.insertion = null;
            this.clickevent = null;
            this.hoverevent = null;
        }
        
        private String buildJSON() {
            String json = "[{";
            json += String.format("\"text\":\"%s\",", text);
            
            if (color != null)
                json += String.format("\"color\":\"%s\",", color);
            if (bold)
                json += "\"bold\":\"true\",";
            if (underline)
                json += "\"underline\":\"true\",";
            if (italic)
                json += "\"italic\":\"true\",";
            if (strikethrough)
                json += "\"strikethrough\":\"true\",";
            if (obfuscate)
                json += "\"obfuscate\":\"true\",";
            if (insertion != null)
                json += String.format("\"insertion\":\"%s\",", insertion);
            if (clickevent != null)
                json += String.format("\"clickEvent\":%s,", clickevent);
            if (hoverevent != null)
                json += String.format("\"hoverEvent\":%s", hoverevent);
            
            if (parent != null) {
                String parent_built = parent.buildJSON();
                
                json += String.format(",\"extra\":%s", parent_built);
            }
            
            json += "}]";
            return json;
        }
        
        public Builder withColor(ChatColor color) {
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
        
        public Builder withInsertion(String insert_text) {
            this.insertion = insert_text;
            return this;
        }
        
        public ClickEventBuilder withClickEvent() {
            return new ClickEventBuilder(this);
        }
        
        public HoverEventBuilder withHoverEvent() {
            return new HoverEventBuilder(this);
        }
        
        public Builder withExtra(String extra_text) {
            return new Builder(this, extra_text);
        }
        
        public RawMessage build() {
            return new RawMessage(buildJSON());
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
        
        public Builder withActionOpenURL(String url) {
            builder.clickevent = String.format(
                    "{\"action\":\"open_url\",\"value\":\"%s\"}",
                    url);
            return builder;
        }
        
        public Builder withActionRunCommand(String command) {
            builder.clickevent = String.format(
                    "{\"action\":\"run_command\",\"value\":\"%s\"}",
                    command);
            return builder;
        }
        
        public Builder withActionSuggestCommand(String command) {
            builder.clickevent = String.format(
                    "{\"action\":\"suggest_command\",\"value\":\"%s\"}",
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
        
        public Builder withActionText(String text) {
            builder.hoverevent = String.format(
                    "{\"action\":\"show_text\",\"value\":\"%s\"}",
                    text);
            return builder;
        }
        // TODO Implement the "Damage" and "tag" tags for show_item
        @SuppressWarnings("deprecation")
        public Builder withActionItem(ItemStack item) {
            builder.hoverevent = String.format(
                    "{\"action\":\"show_item\",\"value\":{\"id\":\"%s\"}}",
                    item.getType().getId());
            return builder;
        }
        
        public Builder withActionScoreboardCriterion(ScoreboardCriterion criterion) {
            builder.hoverevent = String.format(
                    "{\"action\":\"show_achievement\",\"value\":\"%s\"}",
                    criterion.getCriterionString());
            return builder;
        }
        // NYI TODO finish show_entity
//        public Builder withActionEntity(Entity entity, String name) {
//            String entitytype = entity.getType().name().toLowerCase();
//            entitytype = entitytype.substring(0, 1).toUpperCase() + entitytype.substring(1);
//            
//            builder.hoverevent = String.format(
//                    "{\"action\":\"show_entity\",\"value\":{\"type\":\"%s\",\"name\":\"%s\",\"id\":\"%s\"}}",
//                    entitytype, name, entity.getUniqueId().toString());
//            return builder;
//        }
    }
    
    /**
     * Get a builder for a new raw message.
     */
    public static RawMessage.Builder builder(String text) {
        return new RawMessage.Builder(text);
    }
}
