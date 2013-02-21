package org.jvoicexml.implementation.mary;

////////////////////////////////////////////////////////////////////////////////
// Copyright (C) 2002  Scott McCrory
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
////////////////////////////////////////////////////////////////////////////////

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * <P>
 * Captures the output of an InputStream.
 * </P>
 * 
 * With acknowledgements to Michael C. Daconta, author of "Java Pitfalls, Time
 * Saving Solutions, and Workarounds to Improve Programs." and his article in
 * JavaWorld "When Runtime.exec() Won't".
 * 
 * See the ExecRunner class for a reference implementation.
 * 
 * @author <a href="mailto:smccrory@users.sourceforge.net">Scott McCrory </a>.
 * @author Dirk Schnelle-Walka
 * @version $Revision: 2645 $
 */
class StreamGobbler extends Thread {

    /** The input stream we're gobbling. */
    private InputStream in = null;

    /** The printwriter we'll send the gobbled characters to if asked. */
    private PrintWriter pwOut = null;

    /** Our flag to allow us to safely terminate the monitoring thread. */
    private boolean quit = false;

    /**
     * Basic constructor for StreamGobbler.
     */
    public StreamGobbler() {
        super();
    }

    /**
     * A simpler constructor for StreamGobbler - defaults to stdout.
     * 
     * @param stream
     *            the input stream of a process.
     */
    public StreamGobbler(final InputStream stream) {
        this();
        in = stream;
        this.pwOut = new PrintWriter(System.out, true);
    }

    /**
     * A more explicit constructor for StreamGobbler where you can tell it
     * exactly where to relay the output to. Creation date: (9/23/2001 8:48:01
     * PM)
     * 
     * @param instream
     *            the input stream of a process
     * @param outstream
     *            the output stream of a process
     */
    public StreamGobbler(final InputStream instream,
            final OutputStream outstream) {
        this();
        in = instream;
        pwOut = new PrintWriter(outstream, true);
    }

    /**
     * A more explicit constructor for StreamGobbler where you can tell it
     * exactly where to relay the output to. Creation date: (9/23/2001 8:48:01
     * PM)
     * 
     * @param instream
     *            InputStream
     * @param out
     *            PrintWriter
     */
    public StreamGobbler(final InputStream instream, final PrintWriter out) {
        this();
        in = instream;
        pwOut = out;
    }

    /**
     * We override the <code>clone</code> method here to prevent cloning of our
     * class.
     * 
     * @throws CloneNotSupportedException
     *             To indicate cloning is not allowed
     * @return Nothing ever really returned since we throw a
     *         CloneNotSupportedException
     */
    @Override
    public final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * Tells the StreamGobbler to quit it's operation. This is safer than using
     * stop() since it uses a semaphore checked in the main wait loop instead of
     * possibly forcing semaphores to untimely unlock.
     */
    public void quit() {
        quit = true;
    }

    /**
     * We override the <code>readObject</code> method here to prevent
     * deserialization of our class for security reasons.
     * 
     * @param instream
     *            java.io.ObjectInputStream
     * @throws IOException
     *             thrown if a problem occurs
     */
    private void readObject(final ObjectInputStream instream)
        throws IOException {
        throw new IOException("Object cannot be deserialized");
    }

    /**
     * Gobbles up all the stuff coming from the InputStream and sends it to the
     * OutputStream specified during object construction.
     */
    @Override
    public void run() {

        try {
            // Set up the input stream
            InputStreamReader isr = new InputStreamReader(in);
            BufferedReader br = new BufferedReader(isr);

            // Initialize the temporary results containers
            String line = null;

            // Main processing loop which captures the output
            while ((line = br.readLine()) != null) {
                if (quit) {
                    break;
                } else {
                    pwOut.println(line);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * We override the <code>writeObject</code> method here to prevent
     * serialization of our class for security reasons.
     * 
     * @param out
     *            java.io.ObjectOutputStream
     * @throws IOException
     *             thrown if a problem occurs
     */
    private void writeObject(final ObjectOutputStream out)
        throws IOException {
        throw new IOException("Object cannot be serialized");
    }

    /**
     * Stops gobbling.
     */
    public void stopGobbling() {
        interrupt();
    }
}
