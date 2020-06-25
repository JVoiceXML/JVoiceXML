/*
 * Zanzibar - Open source speech application server.
 *
 * Copyright (C) 2008-2009 Spencer Lord 
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * Contact: salord@users.sourceforge.net
 *
 */
package org.jvoicexml.zanzibar.asterisk;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.asteriskjava.manager.AuthenticationFailedException;
import org.asteriskjava.manager.ManagerConnection;
import org.asteriskjava.manager.ManagerConnectionFactory;
import org.asteriskjava.manager.ManagerEventListener;
import org.asteriskjava.manager.TimeoutException;
import org.asteriskjava.manager.action.HangupAction;
import org.asteriskjava.manager.action.OriginateAction;
import org.asteriskjava.manager.action.RedirectAction;
import org.asteriskjava.manager.response.ManagerResponse;

/**
 * Module that does call control on Asterisk pbx.
 * 
 * @author Spencer Lord {@literal <}<a href="mailto:salord@users.sourceforge.net">salord@users.sourceforge.net</a>{@literal >}
 */
public class AsteriskCallControl  {
    
    /**
     * The _logger.
     */
    private static Logger _logger = Logger.getLogger(AsteriskCallControl.class);
    
    /**
     * The address.
     */
    private String address;
    
    /**
     * The name.
     */
    private String name;
    
    /**
     * The password.
     */
    private String password;
    
    /**
     * The disabled.
     */
    private boolean disabled;
    
    /**
     * The connected.
     */
    private boolean connected = false;
    
    /**
     * The manager connection.
     */
    ManagerConnection managerConnection;
    
    /**
     * Startup.
     */
    public void startup() {
        _logger.debug("Starting up call control module"); 
        
        if (!disabled) {
     
            ManagerConnectionFactory factory = new ManagerConnectionFactory(address,name,password);
            managerConnection = factory.createManagerConnection();
    
    
            // connect to Asterisk and log in
            try {
                managerConnection.login();
                connected = true;
            } catch (IllegalStateException e) {
            	_logger.warn("Illegal State Excepton while loging in to asterisk manager interface.  Call control services is disabled.");
            	connected = false;
            } catch (IOException e) {
            	_logger.warn("IO Excepton while loging in to asterisk manager interface.  Call control services is disabled.");
            	connected = false;
            } catch (AuthenticationFailedException e) {
            	_logger.warn("Authentication Excepton while loging in to asterisk manager interface.  Call control services is disabled.");
            	connected = false;
            } catch (TimeoutException e) {
            	_logger.warn("Timeout Excepton while loging in to asterisk manager interface.  Call control services is disabled.");
            	connected = false;
            }
        } else {
            connected = false;
            _logger.warn("Call control services is disabled."); 
              
        }
        
    }
    
    /**
     * Shutdown.
     */
    public void shutdown() {
       _logger.debug("Shutting up call control module"); 
       
       if (managerConnection != null)
          managerConnection.logoff();
       connected = false;
    }
    
    /**
     * Adds the event listener.
     * 
     * @param listener the listener
     */
    public void addEventListener(ManagerEventListener listener) {
        
        managerConnection.addEventListener(listener);
    }
    
    /**
     * Removes the event listener.
     * 
     * @param listener the listener
     */
    public void removeEventListener(ManagerEventListener listener) {
        
        managerConnection.removeEventListener(listener);
    }
    
    /**
     * Ami originate.
     * 
     * @param channel the channel
     * @param connectContext the connect context
     * @param connectTo the connect to
     * 
     * @return true, if successful
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws TimeoutException the timeout exception
     */
    public  boolean amiOriginate(String channel, String connectContext, String connectTo) throws IOException, TimeoutException {
        
        if (connected) {
            
            OriginateAction originateAction;
            ManagerResponse originateResponse;
        
            
            _logger.info("The channel is: "+channel);
            originateAction = new OriginateAction();
            originateAction.setChannel(channel.trim());
            originateAction.setContext(connectContext.trim());
            originateAction.setExten(connectTo.trim());
            //originateAction.setActionId(channel);
            originateAction.setPriority(new Integer(1));
            originateAction.setTimeout(new Long(30000));
       
        
            // send the originate action and wait for a maximum of 30 seconds for Asterisk
            // to send a reply
            originateResponse = managerConnection.sendAction(originateAction, 30000);
        
            // print out whether the originate succeeded or not
            _logger.info(originateResponse.getResponse());
            return true;
        } else {
            _logger.warn("Could not originate call.  Call control not connected.");
            return false;
        }

    }

    /**
     * Ami redirect.
     * 
     * @param channel the channel
     * @param connectContext the connect context
     * @param connectTo the connect to
     * 
     * @return true, if successful
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws TimeoutException the timeout exception
     */
    public  boolean amiRedirect(String channel, String connectContext, String connectTo) throws  IOException, TimeoutException {

        if (connected) {
    
            RedirectAction redirectAction;
            ManagerResponse redirectResponse;
     
            _logger.info("The channel is: "+channel);
            redirectAction = new RedirectAction();
            redirectAction.setChannel(channel.trim());
            redirectAction.setContext(connectContext);
            redirectAction.setExten(connectTo);
            redirectAction.setActionId(channel.trim());
            redirectAction.setPriority(new Integer(1));
    
        
            // send the originate action and wait for a maximum of 30 seconds for Asterisk
            // to send a reply
            redirectResponse = managerConnection.sendAction(redirectAction, 30000);
        
            // print out whether the originate succeeded or not
            _logger.info(redirectResponse.getResponse()); 
            return true;
        } else {
            _logger.warn("Could not redirect call.  Call control not connected.");
            return false;
        }

    }

    /**
     * Ami hangup.
     * 
     * @param channel the channel
     * 
     * @return true, if successful
     * 
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws TimeoutException the timeout exception
     */
    public  boolean amiHangup(String channel) throws  IOException, TimeoutException {
    
        if (connected) {
    
            HangupAction hangupAction;
            ManagerResponse redirectResponse;
        
            
            _logger.info("The channel is: "+channel);
            hangupAction = new HangupAction();
            hangupAction.setChannel(channel.trim());
        
        
            // send the originate action and wait for a maximum of 30 seconds for Asterisk
            // to send a reply
        
            redirectResponse = managerConnection.sendAction(hangupAction, 30000);
        
            
            // print out whether the originate succeeded or not
            _logger.info(redirectResponse.getResponse());
            return true;
            
        } else {
            _logger.warn("Could not hangup call.  Call control not connected.");
            return false;
        }
    
    }

    /**
     * Gets the address.
     * 
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address.
     * 
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     * 
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Checks if is disabled.
     * 
     * @return the disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Sets the disabled.
     * 
     * @param disabled the disabled to set
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    
}
