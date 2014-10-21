package com.archeinteractive.dev.commonutils.text;

public class Ticker {

    private final String original;
    private String output;
    
    /**
     * Create a new Ticker, that will process the given input, and include the
     * given number of spaces between loops of the string.
     * 
     * @param input  The String to use as input.
     * @param spaces The number of spaces to add between looping.
     */
    public Ticker(String input, int spaces) {
        for (int i = 0; i < spaces; i++) {
            input += " ";
        }
        
        this.original = input;
        this.output = input;
    }
    
    /**
     * Create a new Ticker, that will process the given input. There will be one
     * space inbetween loops of the string.
     * 
     * @param input The String to use as input.
     */
    public Ticker(String input) {
        this(input, 1);
    }
    
    /**
     * Get the original input that was used to create this Ticker.
     * 
     * @return The original String input.
     */
    public String getOriginalInput() {
        return this.original.trim();
    }
    
    /**
     * Reset this Ticker's current output to the original input;
     */
    public void reset() {
        this.output = this.original;
    }
    
    /**
     * Advance this Ticker by the given number of characters.
     * 
     * @param chars The number of characters to advance by.
     * @return The advanced String.
     */
    public String advance(int chars) {
        String beginning = output.substring(0, chars);
        String remaining = output.substring(chars);
        this.output = remaining + beginning;
        return this.output;
    }
    
    /**
     * Advance this Ticker by one character.
     * 
     * @return The advanced String.
     */
    public String advance() {
        return advance(1);
    }
}
