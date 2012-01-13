/*
 * File:    $HeadURL$
 * Version: $LastChangedRevision$
 * Date:    $Date$
 * Author:  $LastChangedBy$
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2007-2011 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Library General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Library General Public License for more details.
 *
 *  You should have received a copy of the GNU Library General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.jvoicexml.test.interpreter.grammar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.jvoicexml.GrammarDocument;
import org.jvoicexml.documentserver.JVoiceXmlGrammarDocument;

/**
 * This class provides utility methods to test grammars.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 * @since 0.6
 *
 * <p>
 * Copyright &copy; 2007 JVoiceXML group - <a
 * href="http://jvoicexml.sourceforge.net"> http://jvoicexml.sourceforge.net/
 * </a>
 * </p>
 */
public final class GrammarUtil {
    /**
     * Defines the base directory to SRGS 1.0 IRP the test grammars.
     */
    public static final String BASE_SRGS_10 = "test/config/irp_srgs10";

    /**
     * Defines the base directory to VXML 2.1 IRP the test grammars.
     */
    public static final String BASE21 = "test/config/irp_vxml21/";

    /**
     * Do not create.
     */
    private GrammarUtil() {
    }

    /**
     * Convenience method to create a grammar document from a file.
     * @param filename the name of the file.
     * @return grammar document.
     * @throws IOException
     *         Error reading the file.
     */
    public static GrammarDocument getGrammarFromFile(final String filename)
        throws IOException {
        final StringBuilder buffer = new StringBuilder();
        File testFile = new File(filename);
        // If the file does not exist locally, try to find it in the core.
        if (!testFile.exists()) {
            testFile = new File("../org.jvoicexml/" + filename);
        }
        final FileReader fileReader = new FileReader(testFile);
        final BufferedReader bufferedReader = new BufferedReader(fileReader);

        while (bufferedReader.ready()) {
            buffer.append(bufferedReader.readLine());
        }

        bufferedReader.close();
        fileReader.close();

        return getGrammarFromString(buffer.toString());
    }

    /**
     * Convenience method to create a grammar document from a named resource.
     * @param resourcename name of the resource to laod
     * @return grammar document.
     * @throws IOException
     *         Error reading the resource.
     */
    public static GrammarDocument getGrammarFromResource(
            final String resourcename)
        throws IOException {
        final StringBuilder buffer = new StringBuilder();
        final InputStream in =
            GrammarUtil.class.getResourceAsStream(resourcename);
        final Reader reader = new InputStreamReader(in);
        final BufferedReader bufferedReader = new BufferedReader(reader);
        while (bufferedReader.ready()) {
            buffer.append(bufferedReader.readLine());
        }

        bufferedReader.close();
        reader.close();
        in.close();
        return getGrammarFromString(buffer.toString());
    }
    
    /**
     * Convenience method to create a grammar document from a string.
     * @param content content of the document.
     * @return grammar document.
     */
    public static GrammarDocument getGrammarFromString(final String content) {
        return new JVoiceXmlGrammarDocument(null, content.getBytes(),
                System.getProperty("file.encoding"), true);
    }
}
