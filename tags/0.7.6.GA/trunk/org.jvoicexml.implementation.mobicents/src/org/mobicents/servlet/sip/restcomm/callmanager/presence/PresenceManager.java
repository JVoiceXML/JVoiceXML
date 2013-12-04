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
package org.mobicents.servlet.sip.restcomm.callmanager.presence;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TooManyListenersException;
import java.util.UUID;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.Address;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.TimerService;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.mobicents.servlet.sip.restcomm.ServiceLocator;
import org.mobicents.servlet.sip.restcomm.Sid;
import org.mobicents.servlet.sip.restcomm.TimerManager;
import org.mobicents.servlet.sip.restcomm.annotations.concurrency.ThreadSafe;
import org.mobicents.servlet.sip.restcomm.entities.Client;
import org.mobicents.servlet.sip.restcomm.entities.Registration;
import org.mobicents.servlet.sip.restcomm.util.DigestAuthentication;
import org.mobicents.servlet.sip.restcomm.util.HexadecimalUtils;
import org.mobicents.servlet.sip.restcomm.util.IPUtils;
import org.mobicents.servlet.sip.restcomm.util.TimeUtils;


/**
 * @author quintana.thomas@gmail.com (Thomas Quintana)
 */
@ThreadSafe public final class PresenceManager extends SipServlet {
  private static final long serialVersionUID = 1L;
  private static final Logger logger = Logger.getLogger(PresenceManager.class);
  private TimerService clock;
  private ServletConfig config;
  private SipFactory sipFactory;
  
  public PresenceManager() {
    super();
  }
  
  public void cleanup(final Registration registration) {
//    final RegistrationsDao dao = daos.getRegistrationsDao();
//    if(dao.hasRegistration(registration)) {
//      final Registration updatedRegistration = dao.getRegistrationByLocation(registration.getLocation());
//      if(updatedRegistration != null && updatedRegistration.getDateExpires().isAfterNow()) {
//        scheduleCleanup(updatedRegistration);
//      } else {
//        dao.removeRegistration(registration.getLocation());
//      }
//    }
  }
  
  private String createNonce() {
    final byte[] uuid = UUID.randomUUID().toString().getBytes();
    final char[] hex = HexadecimalUtils.toHex(uuid);
	return new String(hex).substring(0, 31);
  }
  
  private String createAuthenticateHeader(final String nonce, final String realm, final String scheme) {
	final StringBuilder buffer = new StringBuilder();
	buffer.append(scheme).append(" ");
	buffer.append("realm=\"").append(realm).append("\", ");
	buffer.append("nonce=\"").append(nonce).append("\"");
    return buffer.toString();
  }
  
  private SipServletResponse createAuthenticateResponse(final SipServletRequest request) {
    final SipServletResponse response = request.createResponse(SipServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED);
    final String nonce = createNonce();
    final SipURI uri = (SipURI)request.getTo().getURI();
    final String realm = uri.getHost();
    final String header = createAuthenticateHeader(nonce, realm, "Digest");
    response.addHeader("Proxy-Authenticate", header);
    return response;
  }

  @Override protected void doErrorResponse(final SipServletResponse response)
      throws ServletException, IOException {
    final SipServletRequest request = response.getRequest();
    final String method = request.getMethod();
    if("OPTIONS".equals(method)) {
      final SipApplicationSession application = response.getApplicationSession();
      final Registration registration = (Registration)application.getAttribute(Registration.class.getName());
      cleanup(registration);
    }
  }

  @Override protected void doRegister(final SipServletRequest request) throws ServletException, IOException {
    final String header = request.getHeader("Proxy-Authorization");
    if(header == null) {
      createAuthenticateResponse(request).send();
    } else {
      final String method = request.getMethod();
      try {
        if(isPermitted(header, method)) {
          register(request);
          final SipServletResponse ok = request.createResponse(SipServletResponse.SC_OK);
          ok.send();
          request.getApplicationSession().invalidate();
        } else {
          createAuthenticateResponse(request).send();
        }
      } catch(final Exception exception) {
        throw new ServletException(exception);
      }
    }
  }
  
  @Override protected void doSuccessResponse(final SipServletResponse response)
      throws ServletException, IOException {
    final SipServletRequest request = response.getRequest();
    final String method = request.getMethod();
    if("OPTIONS".equals(method)) {
      final int status = response.getStatus();
      if(SipServletResponse.SC_OK == status) {
        final SipApplicationSession application = response.getApplicationSession();
        final long timeout = 30 * 1000;
        clock.createTimer(application, timeout, false, "OPTIONS_PING");
        application.setExpires(TimeUtils.millisToMinutes(timeout));
      }
    }
  }

public int getContactCount(final SipServletRequest request) throws ServletParseException {
    final ListIterator<Address> contacts = request.getAddressHeaders("Contact");
    int counter = 0;
    while(contacts.hasNext()) {
      contacts.next();
      counter++;
    }
    return counter;
  }
  
  private int getExpires(final SipServletRequest request) {
	final String header = request.getHeader("Expires");
	if(header != null) {
	  return Integer.parseInt(header);
	} else {
	  return 3600;
	}
  }
  
  private SipURI getOutboundInterface(final ServletConfig config) {
	final ServletContext context = config.getServletContext();
	SipURI result = null;
	@SuppressWarnings("unchecked")
	final List<SipURI> uris = (List<SipURI>)context.getAttribute(OUTBOUND_INTERFACES);
	for(final SipURI uri : uris) {
		final String transport = uri.getTransportParam();
		if("udp".equalsIgnoreCase(transport)) {
			result = uri;
		}
	}
	return result;
  }

  @Override public void init(final ServletConfig config) throws ServletException {
    this.config = config;
    final ServiceLocator services = ServiceLocator.getInstance();
//    daos = services.get(DaoManager.class);
    final ServletContext context = config.getServletContext();
    clock = (TimerService)context.getAttribute(TIMER_SERVICE);
    sipFactory = (SipFactory)context.getAttribute(SIP_FACTORY);
    final TimerManager timers = services.get(TimerManager.class);
    try {
      final PresenceManagerTimerListener listener = new PresenceManagerTimerListener();
      timers.register("CLEANUP", listener);
      timers.register("OPTIONS_PING", listener);
    } catch(final TooManyListenersException exception) { throw new ServletException(exception); }
  }
  
  public void ping(final Registration registration) {
    try {
//      final RegistrationsDao dao = daos.getRegistrationsDao();
//      if(dao.hasRegistration(registration)) {
//        final SipApplicationSession application = sipFactory.createApplicationSession();
//        application.setAttribute(PresenceManager.class.getName(), this);
//	    application.setAttribute(Registration.class.getName(), registration);
//	    final SipURI outboundInterface = getOutboundInterface(config);
//	    StringBuilder buffer = new StringBuilder();
//	    buffer.append("sip:ping").append("@").append(outboundInterface.getHost());
//	    final String from = buffer.toString();
//	    final String to = registration.getLocation();
//	    final SipServletRequest ping = sipFactory.createRequest(application, "OPTIONS", from, to);
//	    final SipURI uri = (SipURI)sipFactory.createURI(to);
//	    ping.pushRoute(uri);
//	    ping.setRequestURI(uri);
//	    final SipSession session = ping.getSession();
//	    session.setHandler("PresenceManager");
//	    ping.send();
//      }
    } catch(final Exception exception) {
      logger.error(exception);
    }
  }
  
  private void register(final SipServletRequest request) throws ServletParseException {
    final SipURI to = (SipURI)request.getTo().getURI();
	final String addressOfRecord = to.toString();
    final ListIterator<Address> contacts = request.getAddressHeaders("Contact");
    while(contacts.hasNext()) {
      final Address contact = contacts.next();
      final String displayName = contact.getDisplayName();
      // Do NAT resolution, only if necessary.
      SipURI location = (SipURI)contact.getURI();
	  try {
	    final InetAddress host = InetAddress.getByName(location.getHost());
		final String ip = host.getHostAddress();
		if(!IPUtils.isRoutableAddress(ip)) {
		  location.setHost(request.getInitialRemoteAddr());
		}
		location.setPort(request.getInitialRemotePort());
	  } catch(final UnknownHostException exception) {
	    logger.warn(exception);
	    continue;
	  }
	  final StringBuilder buffer = new StringBuilder();
      buffer.append("sip:").append(location.getUser()).append("@")
          .append(location.getHost()).append(":").append(location.getPort());
	  final String formattedLocation = buffer.toString();
      int timeToLive = contact.getExpires();
      if(timeToLive == -1) {
        timeToLive = getExpires(request);
      }
      final String userAgent = request.getHeader("User-Agent");
//      final RegistrationsDao dao = daos.getRegistrationsDao();
//      if(timeToLive == 0 && contact.isWildcard()) {
//        dao.removeRegistrations(addressOfRecord);
//      } else {
//        if(timeToLive == 0) {
//          dao.removeRegistration(formattedLocation);
//        } else {
//          final Registration registration = new Registration(Sid.generate(Sid.Type.REGISTRATION), DateTime.now(),
//            DateTime.now(), addressOfRecord, displayName, to.getUser(), userAgent, timeToLive, formattedLocation);
//          if(dao.hasRegistration(addressOfRecord)) {
//            dao.updateRegistration(registration);
//          } else {
//            dao.addRegistration(registration);
//            scheduleCleanup(registration);
//            ping(registration);
//          }
//        }
//      }
    }
  }
  
  private boolean isPermitted(final String header, final String method) {
  	final Map<String, String> authorization = toMap(header);
  	final String user = authorization.get("username");
    final String algorithm = authorization.get("algorithm");
    final String realm = authorization.get("realm");
    final String uri = authorization.get("uri");
    final String nonce = authorization.get("nonce");
    final String nc = authorization.get("nc");
    final String cnonce = authorization.get("cnonce");
    final String qop = authorization.get("qop");
    final String response = authorization.get("response");
//    final ClientsDao dao = daos.getClientsDao();
//    final Client client = dao.getClient(user);
//    if(client != null && Client.ENABLED == client.getStatus()) {
//      final String password = client.getPassword();
//      final String result =  DigestAuthentication.response(algorithm, user, realm, password, nonce, nc,
//          cnonce, method, uri, null, qop);
//      return result.equals(response);
//    } else {
//      return false;
//    }
    return false;
  }
  
  private void scheduleCleanup(final Registration registration)  {
    final SipApplicationSession application = sipFactory.createApplicationSession();
    application.setAttribute(PresenceManager.class.getName(), this);
    application.setAttribute(Registration.class.getName(), registration);
    long timeout = TimeUtils.SECOND_IN_MILLIS * registration.getTimeToLive();
    clock.createTimer(application, timeout, false, "CLEANUP");
    timeout += TimeUtils.SECOND_IN_MILLIS * 30;
    application.setExpires(TimeUtils.millisToMinutes(timeout));
  }
  
  private Map<String, String> toMap(final String header) {
	final Map<String, String> map = new HashMap<String, String>();
	final int endOfScheme = header.indexOf(" ");
	map.put("scheme", header.substring(0, endOfScheme).trim());
	final String[] tokens = header.substring(endOfScheme + 1).split(",");
	for(final String token : tokens) {
	  final String[] values = token.trim().split("=");
	  map.put(values[0].toLowerCase(), values[1].replace("\"", ""));
	}
    return map;
  }
}
