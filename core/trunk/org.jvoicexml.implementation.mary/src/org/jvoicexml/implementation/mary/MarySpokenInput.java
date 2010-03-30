package org.jvoicexml.implementation.mary;



import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import org.jvoicexml.GrammarImplementation;
import org.jvoicexml.RemoteClient;
import org.jvoicexml.event.error.BadFetchError;
import org.jvoicexml.event.error.NoresourceError;
import org.jvoicexml.event.error.UnsupportedFormatError;
import org.jvoicexml.event.error.UnsupportedLanguageError;
import org.jvoicexml.implementation.SpokenInput;
import org.jvoicexml.implementation.SpokenInputListener;
import org.jvoicexml.xml.srgs.GrammarType;
import org.jvoicexml.xml.vxml.BargeInType;


public class MarySpokenInput implements SpokenInput {

    @Override
    public void activateGrammars(Collection<GrammarImplementation<?>> grammars)
            throws BadFetchError, UnsupportedLanguageError, NoresourceError {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deactivateGrammars(Collection<GrammarImplementation<?>> grammars)
            throws NoresourceError, BadFetchError {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Collection<BargeInType> getSupportedBargeInTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<GrammarType> getSupportedGrammarTypes() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URI getUriForNextSpokenInput() throws NoresourceError,
            URISyntaxException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GrammarImplementation<?> loadGrammar(Reader reader, GrammarType type)
            throws NoresourceError, BadFetchError, UnsupportedFormatError {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void record(OutputStream out) throws NoresourceError {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void activate() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getType() {
        return "maryTTS";
    }

    @Override
    public boolean isBusy() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void open() throws NoresourceError {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void passivate() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void connect(RemoteClient client) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void disconnect(RemoteClient client) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void startRecognition() throws NoresourceError, BadFetchError {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void stopRecognition() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addListener(SpokenInputListener listener) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeListener(SpokenInputListener listener) {
        // TODO Auto-generated method stub
        
    }

}

