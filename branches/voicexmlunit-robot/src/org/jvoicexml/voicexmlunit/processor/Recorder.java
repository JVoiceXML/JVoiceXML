package org.jvoicexml.voicexmlunit.processor;

import java.util.concurrent.LinkedBlockingQueue;
import org.jvoicexml.voicexmlunit.io.Statement;

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
     * Construct a new recorder.
     * 
     * @param transaction the transaction for records
     */
    public Recorder() {
        queue = new LinkedBlockingQueue<>();
    }
    
    /*
     * Capture a statement to validate it later.
     * @param statement to insert into queue
     * @throws InterruptedException synchronization problem
     */
    public void capture(final Statement statement) throws InterruptedException {
        queue.put(statement);
    }
    
    /*
     * Play back a statement that has already been recorded.
     * @return next statement from queue
     * @throws InterruptedException synchronization problem
     */
    public Statement playback() throws InterruptedException {
        final Statement statement = queue.take();
        return statement;
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
