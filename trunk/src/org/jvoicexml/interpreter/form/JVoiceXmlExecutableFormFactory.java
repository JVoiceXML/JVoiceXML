/*
 * File:    $RCSfile: JVoiceXmlExecutableFormFactory.java,v $
 * Version: $Revision$
 * Date:    $Date$
 * Author:  $Author$
 * State:   $State: Exp $
 *
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006 JVoiceXML group - http://jvoicexml.sourceforge.net
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

package org.jvoicexml.interpreter.form;

import java.util.Collection;

import org.jvoicexml.interpreter.ExecutableForm;
import org.jvoicexml.interpreter.ExecutableFormFactory;
import org.jvoicexml.xml.vxml.Form;
import org.jvoicexml.xml.vxml.Menu;
import org.jvoicexml.xml.vxml.Vxml;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Implementation of a <code>ExecutableFormFactory</code>.
 *
 * @author Dirk Schnelle
 * @version $Revision$
 *
 * <p>
 * Copyright &copy; 2006 JVoiceXML group -
 * <a href="http://jvoicexml.sourceforge.net">
 * http://jvoicexml.sourceforge.net/</a>
 * </p>
 *
 * @since 0.4
 */
public final class JVoiceXmlExecutableFormFactory
        implements ExecutableFormFactory {
    /**
     * Constructs a new object.
     */
    public JVoiceXmlExecutableFormFactory() {
    }

    /**
     * {@inheritDoc}
     */
    public Collection<ExecutableForm> getExecutableForms(final Vxml vxml) {
        final Collection<ExecutableForm> executableForms =
                new java.util.ArrayList<ExecutableForm>();

        // Check all child nodes, if there are eiter a form or a menu.
        // This has to be done one after the other to keep the order.
        final NodeList children = vxml.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            final Node node = children.item(i);
            if (node instanceof Form) {
                final Form form = (Form) node;
                final ExecutableForm executableForm =
                        new ExecutablePlainForm(form);

                executableForms.add(executableForm);
            } else if (node instanceof Menu) {
                final Menu menu = (Menu) node;
                final ExecutableForm executableForm =
                        new ExecutableMenuForm(menu);

                executableForms.add(executableForm);
            }
        }

        return executableForms;
    }
}
