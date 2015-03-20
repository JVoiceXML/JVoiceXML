/*
 * File:    $HeadURL: https://svn.sourceforge.net/svnroot/jvoicexml/trunk/src/org/jvoicexml/Application.java$
 * Version: $LastChangedRevision: 68 $
 * Date:    $LastChangedDate $
 * Author:  $LastChangedBy: schnelle $
 *
 * JSAPI - An independent reference implementation of JSR 113.
 *
 * Copyright (C) 2007 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.processor.srgs.grammar;

//Comp 2.0.6

public class RuleCount extends RuleComponent {
    public static final int MAX_PROBABILITY = 0x7fffffff;

    public static final int REPEAT_INDEFINITELY = 0x7fffffff;

    private RuleComponent ruleComponent;

    private int repeatMin;

    private int repeatMax;

    private int repeatProbability;

    public RuleCount(RuleComponent ruleComponent, int repeatMin)
        throws IllegalArgumentException {
        if (repeatMin < 0) {
            throw new IllegalArgumentException(
                    "Repeat minimum must be greater or equal to 0!");
        }
        this.ruleComponent = ruleComponent;
        this.repeatMax = REPEAT_INDEFINITELY;
        this.repeatMin = repeatMin;
        this.repeatProbability = -1;
    }

    public RuleCount(RuleComponent ruleComponent, int repeatMin, int repeatMax)
        throws IllegalArgumentException {
        if (repeatMin < 0 || (repeatMin > repeatMax)) {
            throw new IllegalArgumentException(
                    "Repeat minimum must be greater or equal to 0 and smaller "
                            + "than or equal to repeat maximum!");
        }
        this.ruleComponent = ruleComponent;
        this.repeatMin = repeatMin;
        this.repeatMax = repeatMax;
        this.repeatProbability = -1;
    }

    public RuleCount(RuleComponent ruleComponent, int repeatMin, int repeatMax,
            int repeatProbability) throws IllegalArgumentException {
        if (repeatMin < 0 || (repeatMin > repeatMax)) {
            throw new IllegalArgumentException(
                    "Repeat minimum must be greater or equal to 0 and smaller "
                            + "than or equal to repeat maximum!");
        }

        if (repeatProbability < 0) {
            throw new IllegalArgumentException(
                    "Repeat propability must be greater or equal to 0!");
        }
        this.ruleComponent = ruleComponent;
        this.repeatMin = repeatMin;
        this.repeatMax = repeatMax;
        this.repeatProbability = repeatProbability;
    }

    public int getRepeatMax() {
        return repeatMax;
    }

    public int getRepeatMin() {
        return repeatMin;
    }

    public int getRepeatProbability() {
        if (repeatProbability < 0) {
            return REPEAT_INDEFINITELY;
        }

        return repeatProbability;
    }

    public RuleComponent getRuleComponent() {
        return ruleComponent;
    }

    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append("<item repeat=\"");
        str.append(repeatMin);
        if (repeatMin != repeatMax) {
            str.append("-");

            if (repeatMax != REPEAT_INDEFINITELY) {
                str.append(repeatMax);
            }
        }
        str.append("\"");

        if (repeatProbability >= 0) {
            // TODO we should divide by MAX_PROBABILTY but this is not
            // supported in CLDC 1.0
            str.append(" repeat-prop=\"");
            str.append(repeatProbability);
            str.append("\"");
        }

        str.append(">");

        // TODO: What to do with null rule components?
        if (ruleComponent != null) {
            str.append(ruleComponent);
        }

        str.append("</item>");

        return str.toString();
    }
}
