/**
 * 
 */
package org.jvoicexml.callmanager.mmi.umundo;

import org.umundo.core.Message;
import org.umundo.s11n.ITypedReceiver;

/**
 * @author Piri
 *
 */
public class DummyReceiver implements ITypedReceiver {

    @Override
    public void receiveObject(Object object, Message msg) {
        System.out.println(object);
    }

}
