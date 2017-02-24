package org.jvoicexml.interpreter.grammar.luis;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;
import org.jvoicexml.interpreter.datamodel.DataModel;
import org.jvoicexml.interpreter.datamodel.ecmascript.EcmaScriptDataModel;
import org.jvoicexml.mock.TestProperties;

public class LUISGrammarEvaluatorTest {

    @Test
    public void testGetSemanticInterpretation()
            throws URISyntaxException, IOException {
        final TestProperties properties = new TestProperties();
        final String subscription = properties.get("luis.subscription-key");
        final String applicationId = properties
                .get("luis.pizza-application-id");

        final URI uri = new URI(
                "https://api.projectoxford.ai/luis/v1/application?id="
                        + applicationId);
        LUISGrammarEvaluator evaluator = new LUISGrammarEvaluator(subscription,
                uri);
        DataModel model = new EcmaScriptDataModel();
        model.createScope();
        final Object result = evaluator.getSemanticInterpretation(model,
                "I want a large pizza with salami");
        Assert.assertNotNull("interpretation must no be null", result);
    }
}
