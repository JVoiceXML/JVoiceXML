/*
 * File:    $RCSfile: XmlWriter.java,v $
 * Version: $Revision: 1.4 $
 * Date:    $Date: 2006/05/17 08:20:22 $
 * Author:  $Author: schnelle $
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2005-2006 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This class is based on work by the apache software foundation.
 *
 * Copyright (c) 2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Crimson" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 1999, Sun Microsystems, Inc.,
 * http://www.sun.com.  For more information on the Apache Software
 * Foundation, please see <http://www.apache.org/>.
 */

package org.jvoicexml.xml;

import java.io.IOException;
import java.io.Writer;

/**
 * This writer captures context used when writing XML text, such as state
 * used to "pretty print" output or to identify entities which are
 * defined.  Pretty printing is useful when displaying structure in
 * XML documents that need to be read or edited by people (rather
 * than only by machines).
 *
 * @see XmlWritable
 *
 * @author David Brownell
 * @author Dirk Schnelle
 * @version $Revision: 1.4 $
 *
 * <p>
 * Copyright &copy; 2005-2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 */
public class XmlWriter
        extends Writer {
    /**
     * The end of line terminator retrieved by evaluating the system
     * property <code>line.separator</code>.
     */
    public static final String EOL;

    /** Default numberofspaces for block indent. */
    public static final int DEFAULT_BLOCK_INDENT = 4;

    /**
     * The writer to which output should be written.
     */
    private final Writer writer;

    /** Number of indent blocks. */
    private int indentLevel;

    /**
     * Current indent level for blocks, in terms of spaces per block, for use
     * in pretty printing XML text.
     */
    private int blockSpaces = DEFAULT_BLOCK_INDENT;

    /**
     * <code>true</code>, if writes using the context should
     * <em>pretty print"</em>.
     */
    private boolean prettyOutput;

    static {
        String temp;
        try {
            temp = System.getProperty("line.separator", "\n");
        } catch (SecurityException e) {
            temp = "\n";
        }
        EOL = temp;
    }

    /**
     * Constructs an xml writethat doesn't pretty-print output.
     * @param out Writer to which output should be written.
     */
    public XmlWriter(final Writer out) {
        writer = out;
    }

    /**
     * Constructs an xml write that supports pretty-printing
     * output with the given number of spaces for block indentation.
     * @param out Writer to which output should be written.
     * @param spaces Number of spaces for blocks, for
     *              use in pretty printing XML text.
     */
    public XmlWriter(final Writer out, final int spaces) {
        writer = out;
        prettyOutput = true;
        blockSpaces = spaces;
    }

    /**
     * Returns the writer to which output should be written.
     * @return Writer to which output should be written.
     */
    public final Writer getWriter() {
        return writer;
    }

    /**
     * Returns true if the specified entity was already declared
     * in this output context, so that entity references may be
     * written rather than their expanded values.  The predefined
     * XML entities are always declared.
     * @param name Name of the entity.
     * @return <code>true</code> if the specified entity wasalready declared.
     */
    public final boolean isEntityDeclared(final String name) {
        // for contexts tied to documents with DTDs,
        // ask that DTD if it knows that entity...

        return ("amp".equals(name)
                || "lt".equals(name) || "gt".equals(name)
                || "quot".equals(name) || "apos".equals(name));
    }

    /**
     * Returns the number of spaces per indent level, for
     * use in pretty printing XML text.
     * @return Number of spaces.
     */
    public final int getBlockSpaces() {
        return blockSpaces;
    }

    /**
     * Assigns the number of spaces per indent level, for
     * use in pretty printing XML text.
     * @param spaces Number of spaces.
     */
    public final void setBlockSpaces(final int spaces) {
        blockSpaces = spaces;
    }

    /**
     * Returns the current indent level, in terms of blocks, for
     * use in pretty printing XML text.
     * @return Current indent level.
     */
    public final int getIndentLevel() {
        return indentLevel;
    }

    /**
     * Assigns the current indent level, in terms of blocks, for
     * use in pretty printing XML text.
     * @param level New indent level.
     */
    public final void setIndentLevel(final int level) {
        indentLevel = level;
    }

    /**
     * Increase the indent level by 1.
     */
    public final void incIndentLevel() {
        ++indentLevel;
    }

    /**
     * Decrease the indent level by 1, does nothing if the indent level is 0.
     */
    public final void decIndentLevel() {
        if (indentLevel == 0) {
            return;
        }

        --indentLevel;
    }

    /**
     * If pretty printing is enabled, this writes a newline followed by
     * <code>indentLevel</code> spaces.  At the beginning of a line, groups
     * of eight consecutive spaces are replaced by tab characters, for
     * storage efficiency.
     *
     * <p> Note that this method should not be used except in cases
     * where the additional whitespace is guaranteed to be semantically
     * meaningless.  This is the default, and is controlled through the
     * <code>xml:space</code> attribute, inherited from parent elements.
     * When this attribute value is <code>preserve</code>, this method should
     * not be used.  Otherwise, text normalization is expected to remove
     * excess whitespace such as that added by this call.
     * </p>
     * @exception IOException
     *            Error writing to the writer.
     */
    public final void printIndent()
            throws IOException {
        int temp = indentLevel * blockSpaces;

        if (!prettyOutput) {
            return;
        }

        writer.write(EOL);
        while (temp-- > 0) {
            writer.write(' ');
        }
    }

    /**
     * Returns true if writes using the context should "pretty print",
     * displaying structure through indentation as appropriate.
     * @return <code>true</code> if  writes should <em>pretty print</em>
     */
    public final boolean isPrettyOutput() {
        return prettyOutput;
    }

    /**
     * Close the stream, flushing it first.
     *
     * @throws IOException If an I/O error occurs
     */
    public final void close()
            throws IOException {
        if (writer == null) {
            return;
        }

        writer.close();
    }

    /**
     * Flush the stream.
     *
     * @throws IOException If an I/O error occurs
     */
    public final void flush()
            throws IOException {
        if (writer == null) {
            return;
        }

        writer.flush();
    }

    /**
     * Write a string.
     *
     * @param str String to be written
     * @throws IOException If an I/O error occurs
     */
    public final void write(final String str)
            throws IOException {
        if (writer == null) {
            return;
        }

        writer.write(str);
    }

    /**
     * Write a portion of a string.
     *
     * @param str A String
     * @param off Offset from which to start writing characters
     * @param len Number of characters to write
     * @throws IOException If an I/O error occurs
     */
    public final void write(final String str, final int off, final int len)
            throws IOException {
        if (writer == null) {
            return;
        }

        writer.write(str, off, len);
    }

    /**
     * Write an array of characters.
     *
     * @param cbuf Array of characters to be written
     * @throws IOException If an I/O error occurs
     */
    public final void write(final char[] cbuf)
            throws IOException {
        if (writer == null) {
            return;
        }

        writer.write(cbuf);
    }

    /**
     * Write a portion of an array of characters.
     *
     * @param cbuf Array of characters
     * @param off Offset from which to start writing characters
     * @param len Number of characters to write
     * @throws IOException If an I/O error occurs
     */
    public final void write(final char[] cbuf, final int off, final int len)
            throws IOException {
        if (writer == null) {
            return;
        }

        writer.write(cbuf, off, len);
    }

    /**
     * Write a single character.
     *
     * @param c int specifying a character to be written.
     * @throws IOException If an I/O error occurs
     */
    public final void write(final int c)
            throws IOException {
        if (writer == null) {
            return;
        }

        writer.write(c);
    }

    /**
     * Write a standard header for an XML document.
     *
     * @throws IOException Error writing to the writer
     * @exception IOException
     *            Error writing to the writer.
     */
    public final void writeHeader()
            throws IOException {
        write("<?xml");
        writeAttribute("version", "1.0");
        writeAttribute("encoding", "UTF-8");
        write("?>");

        printIndent();
    }

    /**
     * Writes a attribute in the form of <code> name="value"</code>. A leading
     * space is always written before the name. Does
     * nothing if either the name of the attribute or its value is
     * <code>null</code>.
     * @param name Name of the attribute.
     * @param value Value of the attribute.
     * @throws IOException
     *         Error writing to the writer.
     */
    public final void writeAttribute(final String name, final String value)
            throws IOException {
        if ((name == null) || (value == null)) {
            return;
        }

        write(' ');
        write(name);
        write("=\"");
        write(value);
        write("\"");
    }
}
