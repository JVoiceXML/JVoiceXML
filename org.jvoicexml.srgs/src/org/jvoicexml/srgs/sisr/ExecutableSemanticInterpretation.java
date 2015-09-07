package org.jvoicexml.srgs.sisr;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public interface ExecutableSemanticInterpretation {
    void dump(String pad);

    void execute(Context context, Scriptable scope);
}
