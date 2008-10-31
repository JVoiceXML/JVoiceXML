package org.jvoicexml.systemtest.log4j;

/**
 * 
 * @author lancer
 * 
 */
public class BooleanCollector extends StringCollector {

    @Override
    public Object getTrove() {
        String message = (String) super.getTrove();
        String m = message.trim();
        if (m.length() == 0) {
            return false;
        } else {
            return true;
        }
    }
}
