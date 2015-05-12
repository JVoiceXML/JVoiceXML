package org.jvoicexml.processor.srgs;

import org.jvoicexml.processor.srgs.grammar.RuleComponent;
import org.jvoicexml.processor.srgs.grammar.RuleCount;

class CompoundStackItem implements Cloneable {
    private final RuleComponent currentCompound;
    private int positionInSequence;
    private int iterationInCount;
    private int localMaxIterationCount;

    public CompoundStackItem(final RuleComponent component) {
        this.currentCompound = component;
        if (currentCompound instanceof RuleCount) {
            final RuleCount count = (RuleCount) component;
            localMaxIterationCount = count.getRepeatMax();
        }
    }

    public RuleComponent getCurrentCompound() {
        return currentCompound;
    }

    public int getPositionInSequence() {
        return positionInSequence;
    }

    public void incrementPositionInSequence() {
        positionInSequence++;
    }

    public int getIterationCount() {
        return iterationInCount;
    }

    public void incrementIterationInCount() {
        iterationInCount++;
    }

    public void setLocalMaxIterationCount(final int value) {
        localMaxIterationCount = value;
    }

    public int getLocalMaxIterationCount() {
        return localMaxIterationCount;
    }

    public boolean reachedMaxIterationCount() {
        return iterationInCount >= localMaxIterationCount;
    }

    @Override
    public CompoundStackItem clone() {
        try {
            return (CompoundStackItem) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e.getMessage());
        }
    }

    @Override
    public String toString() {
        final StringBuilder str = new StringBuilder();
        str.append(CompoundStackItem.class.getCanonicalName());
        str.append('[');
        str.append(iterationInCount);
        str.append(", ");
        str.append(localMaxIterationCount);
        str.append(", ");
        str.append(positionInSequence);
        str.append(", ");
        str.append(currentCompound.getClass().getSimpleName());
        str.append(": ");
        str.append(currentCompound);
        str.append(']');
        return str.toString();
    }
}
