package org.jvoicexml.voicexmlunit;

/**
 * Facade for IVR.
 * Maybe some methods could behave different.
 * 
 * @author raphael
 */
public class Caller implements Adapter {
    
    private final IVR ivr;

    public Caller(final IVR ivr) {
        this.ivr = ivr;
    }
    
    @Override
    public void prompt(Comparable pattern) {
        ivr.prompt(pattern);
    }

    @Override
    public void hears(Comparable pattern) {
        ivr.hears(pattern);
    }

    @Override
    public void says(String string) {
        ivr.says(string);
    }

    @Override
    public void enters(char[] digits) {
        ivr.enters(digits);
    }

    @Override
    public void press(char digit) {
        ivr.press(digit);
    }

    @Override
    public void select(char digit) {
        ivr.select(digit);
    }

    @Override
    public void select(String string) {
        ivr.select(string);
    }

    @Override
    public void noinput() {
        ivr.noinput();
    }

    @Override
    public void nomatch() {
        ivr.nomatch();
    }

    @Override
    public void hangup() {
        ivr.hangup();
    }
    
}
