/**
 * 
 */
package org.jvoicexml.demo.jtapidemo;

import javax.telephony.CallEvent;
import javax.telephony.CallListener;
import javax.telephony.ConnectionEvent;
import javax.telephony.ConnectionListener;
import javax.telephony.MetaEvent;

/**
 * @author DS01191
 *
 */
final class DemoConnectionListener implements ConnectionListener {

    @Override
    public void callActive(CallEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void callEventTransmissionEnded(CallEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void callInvalid(CallEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void multiCallMetaMergeEnded(MetaEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void multiCallMetaMergeStarted(MetaEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void multiCallMetaTransferEnded(MetaEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void multiCallMetaTransferStarted(MetaEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void singleCallMetaProgressEnded(MetaEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void singleCallMetaProgressStarted(MetaEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void singleCallMetaSnapshotEnded(MetaEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void singleCallMetaSnapshotStarted(MetaEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void connectionAlerting(ConnectionEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void connectionConnected(ConnectionEvent event) {
        System.out.println("*** connected");
    }

    @Override
    public void connectionCreated(ConnectionEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void connectionDisconnected(ConnectionEvent event) {
        System.out.println("*** disconnected");
    }

    @Override
    public void connectionFailed(ConnectionEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void connectionInProgress(ConnectionEvent event) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void connectionUnknown(ConnectionEvent event) {
        // TODO Auto-generated method stub
        
    }
}
