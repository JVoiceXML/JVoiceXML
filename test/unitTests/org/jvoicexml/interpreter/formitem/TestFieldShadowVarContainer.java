package org.jvoicexml.interpreter.formitem;

import junit.framework.*;
import org.mozilla.javascript.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2005</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TestFieldShadowVarContainer
        extends TestCase {
    private Context context;
    private Scriptable scope;

    public TestFieldShadowVarContainer(String name) {
        super(name);
    }

    protected void setUp()
            throws Exception {
        super.setUp();

        context = Context.enter();
        context.setLanguageVersion(Context.VERSION_1_6);
        scope = context.initStandardObjects();

        try {
            ScriptableObject.defineClass(scope, FieldShadowVarContainer.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void tearDown()
            throws Exception {
        super.tearDown();
    }

    public void testGetUtterance() {
        Scriptable scriptable = context.newObject(scope,
                                                  "FieldShadowVarContainer");
        scope.put("test$", scope, scriptable);
        String expectedReturn = "testvalue";
        final Object resrult = context.evaluateString(scope,
                "test$.utterance='hello'", "expr", 1, null);
//        scope.put("test.utterance", scope, expectedReturn);
//        fieldShadowVarContainer.put("test.utterance", fieldShadowVarContainer, expectedReturn);

//        fieldShadowVarContainer.setUtterance("hallo");
        System.out.println("1: " + scope.get("test$", scope));

        final Object o = context.evaluateString(scope,
                                                "test$.utterance", "expr", 1, null);
        System.out.println("*** " + o);
        System.out.println("2: " + scope.get("test$.utterance", scope));
//        System.out.println("3: " + fieldShadowVarContainer.getUtterance());
    }

}
