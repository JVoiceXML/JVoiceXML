package org.jvoicexml.processor.srgs.grammar;

import java.util.Locale;

//Comp. 2.0.6

public class RuleLocale extends RuleComponent {
    private final RuleComponent ruleComponent;

    private final Locale locale;

    public RuleLocale(RuleComponent ruleComponent, Locale locale) {
        this.ruleComponent = ruleComponent;
        this.locale = locale;
    }

    public RuleComponent getRuleComponent() {
        return ruleComponent;
    }

    public Locale getSpeechLocale() {
        return locale;
    }

    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append("xml:lang=\"");
        str.append(locale.toString());
        str.append("\"");

        return str.toString();
    }
}