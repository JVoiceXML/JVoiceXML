/*
 * JVoiceXML - A free VoiceXML implementation.
 *
 * Copyright (C) 2013-2015 JVoiceXML group - http://jvoicexml.sourceforge.net
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

/**
 * This package contains demos for the VoiceXMLunit testing platform.
 *
 * <p>
 * Tests must be run with the system property
 * {@code -Djava.security.policy=${config}/jvoicexml.policy} and
 * the {@code config} folder added to the classpath. JNDI within
 * JVoiceXML must be configured to share the classloader repository with the
 * text implementation platform. This is usually achieved by adapting the
 * corresponding property in {@code config/jvxml-jndi.xml}:
 * <pre>
 * &lt;repositor&gt;jndi&lt;/repository&gt;
 * </pre>
 * </p>

 */

package org.jvoicexml.voicexmlunit.demo;

