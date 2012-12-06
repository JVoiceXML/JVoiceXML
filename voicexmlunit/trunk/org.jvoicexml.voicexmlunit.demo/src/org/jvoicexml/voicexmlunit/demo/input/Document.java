package org.jvoicexml.voicexmlunit.demo.input;


import java.io.File;
import java.io.IOException;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.Locale;

import javax.naming.Context;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.AssertionFailedError;

import org.apache.log4j.Logger;

import org.jvoicexml.CharacterInput;
import org.jvoicexml.Session;

import org.jvoicexml.client.text.TextListener;

import org.jvoicexml.documentserver.schemestrategy.MappedDocumentRepository;

import org.jvoicexml.event.JVoiceXMLEvent;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.plain.ConnectionDisconnectHangupEvent;

import org.jvoicexml.voicexmlunit.Call;
import org.jvoicexml.voicexmlunit.Supervisor;

import org.jvoicexml.xml.srgs.Grammar;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.ssml.Break;
import org.jvoicexml.xml.ssml.SsmlDocument;
import org.jvoicexml.xml.vxml.Block;
import org.jvoicexml.xml.vxml.Choice;
import org.jvoicexml.xml.vxml.Field;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Menu;
import org.jvoicexml.xml.vxml.Noinput;
import org.jvoicexml.xml.vxml.Nomatch;
import org.jvoicexml.xml.vxml.Prompt;
import org.jvoicexml.xml.vxml.Reprompt;
import org.jvoicexml.xml.vxml.Value;
import org.jvoicexml.xml.vxml.VoiceXmlDocument;
import org.jvoicexml.xml.vxml.Vxml;


/**
 * Document helper for DtmfDemo.
 * The methods are stolen from org.jvoicexml.demo.inputdemo.InputDemo
 * 
 * @author thesis
 *
 */
public class Document implements TextListener {
    /** Logger for this class. */
    private static final Logger LOGGER = Logger.getLogger(Document.class);
    
    private Call call;
    private boolean inputSent;
    
    /**
     * Create the VoiceXML document.
     *
     * @return Created VoiceXML document, <code>null</code> if an error
     * occurs.
     */
    public VoiceXmlDocument create() {
        final VoiceXmlDocument document;

        try {
            document = new VoiceXmlDocument();
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();

            return null;
        }

        final Vxml vxml = document.getVxml();
        vxml.setXmlLang(Locale.US);

        final Menu menu = vxml.appendChild(Menu.class);
        menu.setId("mainmenu");

        final Prompt promptMenu = menu.appendChild(Prompt.class);
        promptMenu.addText(
            "Please enter 1 to list the titles or 2 to watch a movie");
        final Choice choiceList = menu.appendChild(Choice.class);
        choiceList.setNext("#list");
        choiceList.setDtmf("1");
        final Choice choiceWatch = menu.appendChild(Choice.class);
        choiceWatch.setNext("#watch");
        choiceWatch.setDtmf("2");

        final Form formList = vxml.appendChild(Form.class);
        formList.setId("list");
        final Block blockList = formList.appendChild(Block.class);
        final Prompt promptList = blockList.appendChild(Prompt.class);

        promptList.addText("lord of the rings");
        promptList.appendChild(Break.class);
        promptList.addText("the magnificent seven");
        promptList.appendChild(Break.class);
        promptList.addText("two thousand one a space odyssey");
        promptList.appendChild(Break.class);
        promptList.addText("the matrix");
        promptList.appendChild(Break.class);
        promptList.addText("finding nemo");
        promptList.appendChild(Break.class);
        promptList.addText("spider man");
        promptList.appendChild(Break.class);
        promptList.addText("mystic river");
        promptList.appendChild(Break.class);
        promptList.addText("the italian job");
        promptList.appendChild(Break.class);
        promptList.addText("chicago");
        promptList.appendChild(Break.class);
        promptList.addText("a beautiful mind");
        promptList.appendChild(Break.class);
        promptList.addText("gladiator");
        promptList.appendChild(Break.class);
        promptList.addText("american beauty");
        promptList.appendChild(Break.class);
        promptList.addText("the magnificant seven");

        final Form formWatch = vxml.appendChild(Form.class);
        formWatch.setId("watch");

        final Field field = formWatch.appendChild(Field.class);
        final String fieldName = "movie";
        field.setName(fieldName);

        final Prompt prompt = field.appendChild(Prompt.class);
        prompt.addText("Which movie do you want to watch?");
		prompt.setTimeout("10s");

        final Grammar grammar = field.appendChild(Grammar.class);
        final File movies = new File("config/movies.gram");
        grammar.setSrc(movies.toURI());
        grammar.setType(GrammarType.JSGF);

        final Noinput noinput = field.appendChild(Noinput.class);
        noinput.addText("Please say something!");
        noinput.appendChild(Reprompt.class);

        final Noinput noinputSecond = field.appendChild(Noinput.class);
        noinputSecond.setCount("2");
        noinputSecond.addText("Please say a film title!");
        noinputSecond.appendChild(Reprompt.class);

        final Nomatch nomatch = field.appendChild(Nomatch.class);
        nomatch.addText("Please say a film title!");
        nomatch.appendChild(Reprompt.class);

        /** @todo Move this into a filled section, when the scope works. */
        final Block block = formWatch.appendChild(Block.class);
        block.addText("You can watch the film");
        final Value blockValue = block.appendChild(Value.class);
        blockValue.setExpr(fieldName);

        return document;
    }

    /**
     * Print the given VoiceXML document to <code>stdout</code>. Does nothing
     * if an error occurs.
     *
     * @param document
     * The VoiceXML document to print.
     * @return VoiceXML document as an XML string, <code>null</code> in case
     * of an error.
     */
    public String print(final VoiceXmlDocument document) {
        final String xml;
        try {
            xml = document.toXml();
        } catch (IOException ioe) {
            ioe.printStackTrace();

            return null;
        }

        System.out.println(xml);

        return xml;
    }

    /**
     * Add the given document as a single document application.
     * @param document The only document in this application.
     * @return URI of the first document.
     */
    public URI add(final Context context, final VoiceXmlDocument document) {
        MappedDocumentRepository repository;
        try {
            repository = (MappedDocumentRepository)
                         context.lookup("MappedDocumentRepository");
        } catch (javax.naming.NamingException ne) {
            LOGGER.error("error obtaining the documentrepository", ne);

            return null;
        }

        final URI uri;
        try {
            uri = repository.getUri("/root");
        } catch (URISyntaxException e) {
            LOGGER.error("error creating the URI", e);
            return null;
        }
        repository.addDocument(uri, document.toString());

        return uri;
    }

    /**
     * Call the VoiceXML interpreter context to process the given XML document.
     *
     * @param uri URI of the first document to load
     * @exception JVoiceXMLEvent
     *            Error processing the call
     */
    public void interpret(final Supervisor supervisor, final Call call) {
        this.call = call;
        call.setListener(this);
        supervisor.init(call);
    }
    
    public boolean inputSent() {
    	return inputSent;
    }

	@Override
	public void started() {
		
	}

	@Override
	public void connected(InetSocketAddress remote) {
		inputSent = false;
	}

	@Override
	public void outputSsml(SsmlDocument document) {
		
	}

	@Override
	public void expectingInput() {
		final int count = 2;
        final char dtmf = randomizeDtmf(count);
		LOGGER.info("sending DTMF '" + dtmf  + "'");

        final Session session = call.getVoice().getSession();
        if (session != null) {
			try {
				final CharacterInput input = session.getCharacterInput();
				input.addCharacter(dtmf);
				inputSent = true;
			} catch (NoresourceError | ConnectionDisconnectHangupEvent e) {
				e.printStackTrace();
				AssertionFailedError error = new AssertionFailedError(e.getMessage());
				call.fail(error);
			}
        }
	}

	private char randomizeDtmf(int count) {
		final Integer random = new Integer((int)Math.random()*count);
		return random.toString().toCharArray()[0];
	}

	@Override
	public void inputClosed() {
		
	}

	@Override
	public void disconnected() {
		
	}

}
