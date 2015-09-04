package org.jvoicexml.srgs.sisr;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class SIBlock implements ExecutableSI {
    private static final Logger LOGGER = Logger.getLogger(SIBlock.class);
    private StringBuffer tagContent = new StringBuffer();
    private String lastRulename = null;

    public SIBlock() {
    }

    public SIBlock(String text) {
        append(text);
    }

    public void append(String text) {
        if (text == null || text.trim().length() == 0) {
            if (tagContent == null || tagContent.length() == 0) {
                StringWriter sw = new StringWriter();
                new Throwable("").printStackTrace(new PrintWriter(sw));
                LOGGER.debug("I am here: " + sw.toString());
            }
            return;
        }

        String cleanedText = text.trim();
        if (cleanedText.charAt(cleanedText.length() - 1) != ';')
            cleanedText = cleanedText + ";";

        tagContent.append(cleanedText);
        tagContent.append('\n');
    }

    public String getLastRulename() {
        return lastRulename;
    }

    public void setLastRulename(String lastRulename) {
        this.lastRulename = lastRulename;
    }

    public String getCurrentText() {
        return tagContent.toString();
    }

    public ExecutableSI createMatchingInstance() {
        SIBlock newCopy = new SIBlock();
        newCopy.append(tagContent.toString());
        newCopy.lastRulename = lastRulename;
        return newCopy;
    }

    public void dump(String pad) {
        LOGGER.debug(pad + "SI: " + tagContent);
    }

    @Override
    public void execute(Context context, Scriptable scope) {
        LOGGER.debug("executing: " + tagContent);

        context.evaluateString(scope, tagContent.toString(),
                "SISR executable from TagCollection", 0, null);
    }

}
