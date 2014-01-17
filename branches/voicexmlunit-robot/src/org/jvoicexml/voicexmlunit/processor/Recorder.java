package org.jvoicexml.voicexmlunit.processor;

import java.util.concurrent.LinkedBlockingQueue;
import org.jvoicexml.voicexmlunit.io.Statement;
import org.jvoicexml.voicexmlunit.io.Statement;
import org.jvoicexml.voicexmlunit.processor.Recording;

/**
 * A type of test execution tool where inputs are recorded during manual 
 * testing in order to generate automated test scripts that can be executed 
 * lated (i.e replayed). These tools are often used to support automated 
 * regression testing.
 * 
 * @author raphael
 */
public class Recorder {
    
    /* 
     * Recorder is used from different blocking threads.
     * https://stackoverflow.com/questions/616484/how-to-use-concurrentlinkedqueue
     */
    private LinkedBlockingQueue<Statement> queue;
    
    /*
     * Recording transaction.
     */
    private final Recording record;
    
    /*
     * Construct a new recorder.
     * 
     * @param transaction the transaction for records
     */
    public Recorder(final Recording transaction) {
        queue = new LinkedBlockingQueue<>();
        record = transaction;
    }
    
    /*
     * Capture an output to validate it later.
     * @param assertion the assertion to collect
     * @throws InterruptedException synchronization problem
     */
    public void capture(final Statement assertion) throws InterruptedException {
        queue.put(assertion);
    }
    
    /*
     * Replay an input that has already been recorded.
     * @param record the record holding valid data for input processing
     * @throws InterruptedException synchronization problem
     */
    public void replay() throws InterruptedException {
       final Statement current = queue.take();
       current.send(record);            
    }
    
    /**
     * Validate an assertion if it's already recorded.
     * @param assertion the actual assertion
     * @return true if the assertion was recorded
     */
    public boolean validate(final Statement assertion) {
        return queue.contains(assertion);
    }    
}
