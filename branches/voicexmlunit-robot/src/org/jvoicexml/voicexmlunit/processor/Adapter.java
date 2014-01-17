package org.jvoicexml.voicexmlunit.processor;

/**
 * Adapter for clients.
 * http://javapapers.com/design-patterns/adapter-pattern/
 * 
 * This is the general interface to implement the API for clients:
 * http://sourceforge.net/apps/mediawiki/jvoicexml/index.php?title=UnitTest#Example_API
 * 
 * @author raphael
 */
public interface Adapter {
    
    /*
     * Prompt Verification (Output)
     */
    
    public void prompt(final Comparable pattern);
    
    public void hears(final Comparable pattern);
    

    /*
     * Input
     */
    
    public void says(final String string);
    public void enters(final char[] digits);
    public void press(final char digit);
    
    public void select(final char digit);
    public void select(final String string);
    
    public void noinput();
    public void nomatch();
    public void hangup();
}
