/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2006-2008 JVoiceXML group - http://jvoicexml.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jvoicexml.systemtest;

import java.net.URI;

/**
 *  a test case.
 *
 * @author lancer
 *
 */
public interface TestCase {

    /**
     * @return start URI of this test case.
     */
    URI getStartURI();

    /**
     * @return id or this test case.
     */
    int getId();

    /**
     * @return ignored reason of this test case. if null, this test case should
     *         not be ignore.
     */
    String getIgnoreReason();

    /**
     * do completeness check for each test case.
     * return true if all request files are exists, else false.
     */
    boolean completenessCheck();
}
