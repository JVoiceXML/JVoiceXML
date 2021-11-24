/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2021 JVoiceXML group - http://jvoicexml.sourceforge.net
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
package org.jvoicexml.voicexmlunit;

/**
 * An instance that knows about the available {@link LogBuffer}s.
 * @author Dirk Schnelle-Walka
 * @since 0.7.9
 */
public class LogBufferProvider {
    /** The singleton. */
    private static LogBufferProvider INSTANCE;

    /** The interpreter buffer. */
    private final LogBuffer INTERPRETER_BUFFER;
    
    /** The client buffer. */
    private final LogBuffer CLIENT_BUFFER;
    
    /**
     * Prevent creation from outside
     */
    private LogBufferProvider() {
        INTERPRETER_BUFFER = new LogBuffer();
        CLIENT_BUFFER = new LogBuffer();
    }
    
    public static LogBufferProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LogBufferProvider();
        }
        return INSTANCE;
    }

    /**
     * Retrieves the interpreter buffer.
     * @return the interpreter buffer;
     */
    public LogBuffer getInterpreterBuffer() {
        return INTERPRETER_BUFFER;
    }
    
    /**
     * Retrieves the interpreter buffer.
     * @return the interpreter buffer;
     */
    public LogBuffer getClientBuffer() {
        return CLIENT_BUFFER;
    }
}
