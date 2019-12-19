/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.grammar.luis;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jvoicexml.event.error.SemanticError;
import org.jvoicexml.implementation.grammar.GrammarEvaluator;
import org.jvoicexml.interpreter.datamodel.DataModel;

/**
 * An evaluator for LUIS grammars.
 * 
 * @author Dirk Schnelle-Walka
 * @since 0.7.8
 */
public class LUISGrammarEvaluator implements GrammarEvaluator {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger
            .getLogger(LUISGrammarEvaluator.class);

    /** The default proxy port. */
    private static final int DEFAULT_PROXY_PORT = 80;

    /** The name of the proxy to use. */
    private static final String PROXY_HOST;

    /** The port of the proxy server. */
    private static final int PROXY_PORT;

    /** The LUIS subscription key. */
    private final String subscriptionKey;

    /** URI of the grammar. */
    private final URI grammarUri;
    
    static {
        PROXY_HOST = System.getProperty("http.proxyHost");
        final String port = System.getProperty("http.proxyPort");
        if (PROXY_HOST != null && port != null) {
            PROXY_PORT = Integer.parseInt(port);
        } else {
            PROXY_PORT = DEFAULT_PROXY_PORT;
        }
    }

    /**
     * Constructs a new object.
     * 
     * @param subscription
     *            the subscription key
     * @param documentURI
     *            the URI of the associated grammar
     */
    public LUISGrammarEvaluator(final String subscription,
            final URI documentURI) {
        grammarUri = documentURI;
        subscriptionKey = subscription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public URI getURI() {
        return grammarUri;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getSemanticInterpretation(final DataModel model,
            String utterance) {
        final HttpClientBuilder builder = HttpClientBuilder.create();
        if (PROXY_HOST != null) {
            HttpHost proxy = new HttpHost(PROXY_HOST, PROXY_PORT);
            builder.setProxy(proxy);
        }
        try (CloseableHttpClient client = builder.build()) {
            final URIBuilder uribuilder = new URIBuilder(grammarUri);
            uribuilder.addParameter("subscription-key", subscriptionKey);
            uribuilder.addParameter("q", utterance);
            final URI uri = uribuilder.build();
            final HttpGet request = new HttpGet(uri);
            final HttpResponse response = client.execute(request);
            final StatusLine statusLine = response.getStatusLine();
            final int status = statusLine.getStatusCode();
            if (status != HttpStatus.SC_OK) {
                final String reasonPhrase = statusLine.getReasonPhrase();
                LOGGER.error("error accessing '" + uri +"': " +
                        reasonPhrase + " (HTTP error code "
                        + status + ")");
                return null;
            }
            final HttpEntity entity = response.getEntity();
            final InputStream input = entity.getContent();
            final Object interpretation = parseLUISResponse(model, input);
            return interpretation;
        } catch (IOException | URISyntaxException | ParseException | SemanticError e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     * Parse the response from LUIS.
     * @param model the current datamodel
     * @param input the input stream to the response entity
     * @return parsed semantic interpretation
     * @throws IOException parsing error
     * @throws ParseException parsing error
     * @throws SemanticError error evaluating the result in the datamodel
     */
    private Object parseLUISResponse(final DataModel model,
            final InputStream input)
            throws IOException, ParseException, SemanticError {
        final InputStreamReader reader = new InputStreamReader(input);
        final JSONParser parser = new JSONParser();
        final JSONObject object = (JSONObject) parser.parse(reader);
        final JSONObject topScoringIntent = 
                (JSONObject) object.get("topScoringIntent");
        final DataModel interpretationModel = model.newInstance();
        interpretationModel.createScope();
        interpretationModel.createVariable("out", model.createNewObject());
        Object out =  interpretationModel.readVariable("out", Object.class);
        final String intent = (String) topScoringIntent.get("intent");
        final Double score = (Double) topScoringIntent.get("score");
        LOGGER.info("detected intent '" + intent + "' (" + score + ")");
        interpretationModel.createVariableFor(out, "nlu-intent", intent);
        interpretationModel.createVariableFor(out, intent,
                model.createNewObject());
        final Object intentObject = interpretationModel.readVariable(
                "out." + intent, Object.class);
        final JSONArray entities = (JSONArray) object.get("entities");
        if (entities.isEmpty()) {
            return intent;
        }
        for (int i = 0; i < entities.size(); i++) {
            final JSONObject currentEntity = (JSONObject) entities.get(i);
            final String type = (String) currentEntity.get("type");
            final String value = (String) currentEntity.get("entity");
            final Double entityScore = (Double) currentEntity.get("score");
            LOGGER.info("detected entity '" + type + "'='" + value + "' ("
                    + entityScore + ")");
            interpretationModel.createVariableFor(intentObject, type, value);
        }

        final Object interpretation =
                interpretationModel.readVariable("out", Object.class);
        final String log = interpretationModel.toString(interpretation);
        LOGGER.info("created semantic interpretation '" + log + "'");
        return interpretation;
    }
}
