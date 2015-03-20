package org.jvoicexml.systemtest.mobicents;
import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.message.MessageExt;

import java.io.IOException;
import java.util.Iterator;
import java.util.ListIterator;

import javax.servlet.ServletException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.Address;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.URI;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipSession;
import javax.sip.ListeningPoint;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.FromHeader;
import javax.sip.header.HeaderFactory;
import javax.sip.header.MaxForwardsHeader;
import javax.sip.header.RecordRouteHeader;
import javax.sip.header.RouteHeader;
import javax.sip.header.ToHeader;
import javax.sip.header.ViaHeader;
import javax.sip.message.Request;

import javax.servlet.sip.SipSession.State;
import javax.servlet.sip.ar.SipApplicationRoutingDirective;

import org.mobicents.javax.servlet.sip.ResponseType;
import org.mobicents.servlet.sip.JainSipUtils;
import org.mobicents.servlet.sip.core.ApplicationRoutingHeaderComposer;
import org.mobicents.servlet.sip.core.MobicentsExtendedListeningPoint;
import org.mobicents.servlet.sip.core.RoutingState;
import org.mobicents.servlet.sip.core.session.MobicentsSipApplicationSession;
import org.mobicents.servlet.sip.core.session.MobicentsSipSession;
import org.mobicents.servlet.sip.core.session.MobicentsSipSessionKey;
import org.mobicents.servlet.sip.core.session.SessionManagerUtil;
import org.mobicents.servlet.sip.message.SipFactoryImpl;
import org.mobicents.servlet.sip.message.SipServletRequestImpl;
import org.mobicents.servlet.sip.proxy.ProxyBranchImpl;
import org.mobicents.servlet.sip.proxy.ProxyBranchTimerTask;
import  org.mobicents.servlet.sip.proxy.ProxyImpl;
import org.mobicents.servlet.sip.proxy.ProxyUtils;
import org.mobicents.servlet.sip.proxy.ProxyBranchImpl.TransactionRequest;
import org.mobicents.servlet.sip.rfc5626.IncorrectFlowIdentifierException;
import org.mobicents.servlet.sip.rfc5626.RFC5626Helper;

import com.vnxtele.util.VNXLog;

public class VCallAction {
	public static ProxyImpl proxy=null;
	public static HeaderFactory headerFactory=null;
	private transient SipServletRequestImpl originalRequest;
	private transient SipServletRequestImpl outgoingRequest;
	private URI targetURI;
	
	public VCallAction(URI uri, ProxyImpl proxy)
	{
		this.targetURI = uri;
		this.proxy = proxy;
		this.originalRequest = (SipServletRequestImpl) proxy.getOriginalRequest();
	}
	public void init()
	{
		try {
			headerFactory = (javax.sip.SipFactory.getInstance()).createHeaderFactory();
			VNXLog.info("headerFactory " + headerFactory);
		} catch (Exception ex) 
		{
			VNXLog.error(ex);
                        
		}
	}
	public void dialParticipant(String fromUri,String toUri,SipFactory sipFactory) 
	{
		try {
			VNXLog.info("making a sip call fromUri " + fromUri+" toUri " + toUri);
			SipApplicationSession appSession = sipFactory.createApplicationSession();
			Address from = sipFactory.createAddress(fromUri);
			Address to = sipFactory.createAddress(toUri);
			URI requestURI = sipFactory.createURI(toUri);
			SipServletRequest request = sipFactory.createRequest(appSession, 
					"INVITE", from, to);
			SipSession sipSession = request.getSession();
			VNXLog.info("DIALCONNECTION " + sipSession.toString());
			request.setRequestURI(requestURI);
			sipSession.setAttribute("inviteRequest", request);
			request.send();
		} catch (Exception ex) 
		{
			VNXLog.error(ex);
                        
		}

	}
	
	
	public void proxyTo(final URI uri, SipServletRequestImpl originalRequest) {
		try {
			System.out.println("proxyTo uri:" + uri);
			VNXLog.info("proxyTo uri:" + uri);
			if (uri == null) {
				throw new NullPointerException("URI can't be null");
			}
			if (!JainSipUtils.checkScheme(uri.toString())) {
				// Fix for Issue
				// http://code.google.com/p/mobicents/issues/detail?id=2327,
				// checking the route header
				RouteHeader routeHeader = (RouteHeader) originalRequest
						.getMessage().getHeader(RouteHeader.NAME);
				if (routeHeader == null
						|| (routeHeader != null && !JainSipUtils
								.checkScheme(routeHeader.getAddress().getURI()
										.toString()))) {
					throw new IllegalArgumentException("Scheme "
							+ uri.getScheme() + " is not supported");
				}
			}
			ProxyBranchImpl branch = new ProxyBranchImpl(uri,
					(ProxyImpl) (originalRequest.getProxy()));
			branch.setRecordRoute(true);
			branch.setRecurse(true);
//			branch
		} catch (Exception ex) {
			VNXLog.error(ex);

		}

	}
	
	/**
	 * After the branch is initialized, this method proxies the initial request to the
	 * specified destination. Subsequent requests are proxied through proxySubsequentRequest
	 */
	public void cloneRequest()	
	{
		try {
			VNXLog.info("cloneRequest targetURI:" + targetURI);
			SipURI recordRoute = null;
			SipURI recordRouteURI = null;
			recordRouteURI = proxy.getRecordRouteURI();
			// If the proxy is not adding record-route header, set it to null
			// and it
			// will be ignored in the Proxying
			if (proxy.getRecordRoute()) {
				if (recordRouteURI == null) {
					recordRouteURI = proxy.getSipFactoryImpl().createSipURI(
							"proxy", "localhost");
				}
				recordRoute = recordRouteURI;
			}

			Request clonedInit = (Request) originalRequest.getMessage().clone();
			((MessageExt) clonedInit).setApplicationData(null);
			outgoingRequest = (SipServletRequestImpl) proxy
					.getSipFactoryImpl()
					.getMobicentsSipServletMessageFactory()
					.createSipServletRequest(clonedInit,
							originalRequest.getSipSession(), null, null, false);

			//
			ProxyBranchImpl branch = new ProxyBranchImpl(targetURI,
					(ProxyImpl) (originalRequest.getProxy()));
			branch.setRecordRoute(true);
			branch.setRecurse(true);
			//
			Request cloned = ProxyUtils.createProxiedRequest(outgoingRequest,
					branch, targetURI, proxy.getOutboundInterface(),
					recordRoute, proxy.getPathURI());
			// Shadowman
			VNXLog.info("remove content");
			cloned.removeContent();
			// tells the application dispatcher to stop routing the original
			// request
			// since it has been proxied
			originalRequest.setRoutingState(RoutingState.PROXIED);
			forwardRequest(cloned, false);
		} catch (Exception ex) {
			VNXLog.error(ex);

		}
	}
	
	private void forwardRequest(Request request, boolean subsequent) {
		try {
			VNXLog.info("creating cloned Request for proxybranch " + request);
			final SipServletRequestImpl clonedRequest = (SipServletRequestImpl) proxy
					.getSipFactoryImpl().getMobicentsSipServletMessageFactory()
					.createSipServletRequest(request, null, null, null, false);
			if (subsequent) {
				clonedRequest.setRoutingState(RoutingState.SUBSEQUENT);
			}
			// Initialize the sip session for the new request if initial
			final MobicentsSipSession originalSipSession = originalRequest
					.getSipSession();
			clonedRequest.setCurrentApplicationName(originalRequest
					.getCurrentApplicationName());
			if (clonedRequest.getCurrentApplicationName() == null && subsequent) {
				clonedRequest.setCurrentApplicationName(originalSipSession
						.getSipApplicationSession().getApplicationName());
			}
			clonedRequest.setSipSession(originalSipSession);
			final MobicentsSipSession newSession = (MobicentsSipSession) clonedRequest
					.getSipSession();
			try {
				newSession.setHandler(originalSipSession.getHandler());
			} catch (ServletException e) {
				VNXLog.error(
						"could not set the session handler while forwarding the request",
						e);
				throw new RuntimeException(
						"could not set the session handler while forwarding the request",
						e);
			}
			clonedRequest.send();
		} catch (Exception ex) {
			VNXLog.error(ex);

		}
	}
	
	/**
     * Returns whether loop detected
     * @param request Request
     * @return true if request is looped, false otherwise
     */
    private boolean checkLoopDetection(Request request)
    {
        /**
         *  4. Optional Loop Detection check
         *
         * An element MAY check for forwarding loops before forwarding a
         * request.  If the request contains a Via header field with a sent-
         * by value that equals a value placed into previous requests by the
         * proxy, the request has been forwarded by this element before.  The
         * request has either looped or is legitimately spiraling through the
         * element.  To determine if the request has looped, the element MAY
         * perform the branch parameter calculation described in Step 8 of
         * Section 16.6 on this message and compare it to the parameter
         * received in that Via header field.  If the parameters match, the
         * request has looped.  If they differ, the request is spiraling, and
         * processing continues.  If a loop is detected, the element MAY
         * return a 482 (Loop Detected) response.
         */
//        ListIterator viaList = request.getHeaders(ViaHeader.NAME);
//        if (viaList != null && viaList.hasNext())
//        {
//            ViaHeader viaHeader = (ViaHeader) viaList.next();
//
//            ListeningPoint[] lps = sipProvider.getListeningPoints();
//
//            String viaHost = viaHeader.getHost();
//            int viaPort = viaHeader.getPort();
//
//            if ( (viaHost.equals(lps[0].getIPAddress()) || viaHost.equalsIgnoreCase(getHostname(sipProvider)) ) && viaPort == lps[0].getPort())
//            {
//                /**
//                 * @todo We have to check the branch-ids...
//                 */
//                return true;
//            }
//        }

        return false;
    }
    public void dialParticipant2(String fromUri,String toUri,SipFactory sipFactory,SipServletRequest orgRequest) 
	{
		try {
			VNXLog.info("making a sip call fromUri " + fromUri+" toUri " + toUri);
			SipApplicationSession appSession = sipFactory.createApplicationSession();
			Address from = sipFactory.createAddress(fromUri);
			Address to = sipFactory.createAddress(toUri);
			URI requestURI = sipFactory.createURI(toUri);
			///
			SipServletRequestImpl origRequestImpl = (SipServletRequestImpl) orgRequest;
			
			Request newRequest = (Request) origRequestImpl.clone();
			newRequest.removeContent();	
			
			//
			/**
	         * If the request does not contain a Max-Forwards header field, this
	         * check is passed.
	         *
	         * If the request contains a Max-Forwards header field with a field
	         * value greater than zero, the check is passed.
	         *
	         * If the request contains a Max-Forwards header field with a field
	         * value of zero (0), the element MUST NOT forward the request.  If
	         * the request was for OPTIONS, the element MAY act as the final
	         * recipient and respond per Section 11.  Otherwise, the element MUST
	         * return a 483 (Too many hops) response.
	         */
			MaxForwardsHeader mf = (MaxForwardsHeader) newRequest.getHeader(MaxForwardsHeader.NAME);
			
			if (mf == null)
	        {
	            mf = headerFactory.createMaxForwardsHeader(70);
	            newRequest.addHeader(mf);
	            VNXLog.debug("Max-Forwards header is missing. Created and added to the cloned request.");
	        }
	        else
	        {
	            mf.setMaxForwards(mf.getMaxForwards() - 1);
	            VNXLog.debug("Max-Forwards value decremented by one. It is now: " + mf.getMaxForwards());
	        }

	        if (mf != null && mf.getMaxForwards() <= 0)
	        {
	        	//
	        	SipServletResponse sipServletResponse = orgRequest.createResponse(SipServletResponse.SC_TOO_MANY_HOPS);
	        	sipServletResponse.send();
	            return;
	        }
	        /**
	         * An element MAY check for forwarding loops before forwarding a
	         * request.
	         */
	        if (checkLoopDetection(newRequest))
	        {
	            SipServletResponse sipServletResponse = orgRequest.createResponse(SipServletResponse.SC_LOOP_DETECTED);
	        	sipServletResponse.send();
	        	VNXLog.debug("Loop detected.  replied.");
	            return;
	        }

	        
			
			
//			SipSession sipSession = request.getSession();
//			VNXLog.info("DIALCONNECTION " + sipSession.toString());
//			request.setRequestURI(requestURI);
//			sipSession.setAttribute("inviteRequest", request);
//	        SipServletRequestImpl newServlet = new SipServletRequestImpl(newRequest,sipFactory,) 
		} catch (Exception ex) 
		{
			VNXLog.error(ex);
                        
		}

	}
    
//    public SipServletRequest createRequest(SipServletRequest origRequest,
//			boolean sameCallId,SipFactory sipFactory) {
//		 {
//			VNXLog.debug("Creating SipServletRequest from original request["
//					+ origRequest + "] with same call id[" + sameCallId + "]");
//		}
//	    
//		final SipServletRequestImpl origRequestImpl = (SipServletRequestImpl) origRequest;
//		final MobicentsSipSession originalSession = origRequestImpl.getSipSession();
//		final MobicentsSipApplicationSession originalAppSession = originalSession
//				.getSipApplicationSession();				
//		
//		
//		final Request newRequest = (Request) origRequestImpl.getMessage().clone();
//		((MessageExt)newRequest).setApplicationData(null);
//		//removing the via header from original request
//		newRequest.removeHeader(ViaHeader.NAME);	
//		
//		final FromHeader newFromHeader = (FromHeader) newRequest.getHeader(FromHeader.NAME); 
//		
//		//assign a new from tag
//		newFromHeader.removeParameter("tag");
//		//remove the to tag
//		((ToHeader) newRequest.getHeader(ToHeader.NAME))
//				.removeParameter("tag");
//		// Remove the route header ( will point to us ).
//		// commented as per issue 649
////		newRequest.removeHeader(RouteHeader.NAME);
//		
//		// Remove the record route headers. This is a new call leg.
//		newRequest.removeHeader(RecordRouteHeader.NAME);
//		
//		//For non-REGISTER requests, the Contact header field is not copied 
//		//but is populated by the container as usual
//		if(!Request.REGISTER.equalsIgnoreCase(origRequest.getMethod())) {
//			newRequest.removeHeader(ContactHeader.NAME);
//		}		
//		try {
//			
//					
//			VNXLog.debug("reusing same call id = " + origRequestImpl.getCallId());
//									
//			newFromHeader.setTag(ApplicationRoutingHeaderComposer.
//					getHash(((SipFactoryImpl)sipFactory).getSipApplicationDispatcher(), 
//							originalSession.getKey().getApplicationName(), 
//							originalAppSession.getKey().getId()));
//			
//			final MobicentsSipSessionKey key = SessionManagerUtil.getSipSessionKey(originalAppSession.getKey().getId(), originalSession.getKey().getApplicationName(), newRequest, false);
//			final MobicentsSipSession session = originalAppSession.getSipContext().getSipManager().getSipSession(key, true, this, originalAppSession);			
//			session.setHandler(originalSession.getHandler());
//			
////			final SipServletRequestImpl newSipServletRequest = (SipServletRequestImpl) 
////					mobicentsSipServletMessageFactory.createSipServletRequest(
////					newRequest,
////					session, 
////					null, 
////					null, 
////					JainSipUtils.DIALOG_CREATING_METHODS.contains(newRequest.getMethod()));			
//			//JSR 289 Section 15.1.6
//			newSipServletRequest.setRoutingDirective(SipApplicationRoutingDirective.CONTINUE, origRequest);
//			
//			 {
//				VNXLog.debug("newSipServletRequest = " + newSipServletRequest);
//			}	
//			
//			return newSipServletRequest;
//		} catch (Exception ex) {
//			VNXLog.error("Unexpected exception ", ex);
//			throw new IllegalArgumentException(
//					"Illegal arg ecnountered while creatigng b2bua", ex);
//		}			
//	}
//    
    
    
}