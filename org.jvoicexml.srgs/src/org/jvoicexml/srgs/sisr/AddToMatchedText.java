package org.jvoicexml.srgs.sisr;

import org.apache.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class AddToMatchedText implements ExecutableSI {
    private static final Logger LOGGER = Logger
            .getLogger(AddToMatchedText.class);
    private String matchedText;

    public AddToMatchedText(String text) {
        matchedText = text;
    }

    public String getMatchedText() {
        return matchedText;
    }

    public void setMatchedText(String matchedText) {
        this.matchedText = matchedText;
    }

    @Override
    public void dump(String pad) {
        LOGGER.debug(pad + "Added '" + matchedText + "' to matched text");
    }

    @Override
    public void execute(Context context, Scriptable scope) {
        String metaCurrent = (String) context.evaluateString(scope,
                "meta.current().text;", "AddToMatchedText:get meta", 0, null);

        if (metaCurrent.length() == 0) {
            context.evaluateString(scope,
                    "meta.current=function() {return {text:'" + matchedText
                            + "', score:1.0}};", "AddToMatchedText:set meta1",
                    0, null);
        } else {
            context.evaluateString(scope,
                    "meta.current=function() {return {text:'" + metaCurrent
                            + " " + matchedText + "', score:1.0}};",
                    "AddToMatchedText:set meta1", 0, null);
        }
    }
}
