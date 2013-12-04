/*
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.mobicents.servlet.sip.restcomm;

import org.apache.log4j.Logger;
import org.mobicents.servlet.sip.restcomm.callmanager.mgcp.MgcpServerManager;
import org.mobicents.servlet.sip.restcomm.sms.SmsAggregator;

/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
public final class Janitor{
  public static final Logger logger = Logger.getLogger(Janitor.class);
  
  private Janitor() {
    super();
  }
  
  public static void cleanup() {
    final ServiceLocator services = ServiceLocator.getInstance();
    final MgcpServerManager mgcpServerManager = services.get(MgcpServerManager.class);
    mgcpServerManager.shutdown();
    final SmsAggregator smsAggregator = services.get(SmsAggregator.class);
    smsAggregator.shutdown();
  }
}
