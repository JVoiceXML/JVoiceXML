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
        final String subscription = properties.get("JVOICEXML_INTERPRETER_GRAMMAR_LUIS_SUBSCRIPTIONID");
        final String applicationId = properties
                .get("JVOICEXML_DEMO_LUIS_APPLICATIONID");

        final URI uri = new URI(
                "https://westus.api.cognitive.microsoft.com/luis/prediction/v3.0/apps/"
                        + applicationId + "/slots/production/predict");
        final LUISGrammarEvaluator evaluator = new LUISGrammarEvaluator(subscription,
                uri);
        final DataModel model = new EcmaScriptDataModel();
        model.createScope();
//        final Object result = evaluator.getSemanticInterpretation(model,
//                "I want a large pizza with salami");
        final Object result = evaluator.getSemanticInterpretation(model,
                "yes please");
        Assert.assertNotNull("interpretation must no be null", result);
    }
}
