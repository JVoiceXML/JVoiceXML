package org.jvoicexml.voicexmlunit.processor;

import org.jvoicexml.voicexmlunit.backend.Concurrency;
import org.jvoicexml.voicexmlunit.io.Statement;

/**
 * A type of test execution tool where inputs are recorded during manual 
 * testing in order to generate automated test scripts that can be executed 
 * later (i.e replayed). These tools are often used to support automated 
 * regression testing.
 * 
 * @author raphael
 */
public class Tape {
    
    /* 
     * Tape is used from different blocking threads, so concurrency is needed.
     */
    private Concurrency concurrency;
    
    /*
     * Construct a new tape.
     */
    public Tape() {
        concurrency = new Concurrency();
    }
    
    /*
     * Capture a statement to validate it later.
     * @param statement to insert into queue
     * @throws InterruptedException synchronization problem
     */
    public void capture(final Statement statement) throws InterruptedException {
        concurrency.produce(statement);
    }
    
    /*
     * Play back a statement that has already been recorded.
     * @return next statement from queue
     * @throws InterruptedException synchronization problem
     */
    public Statement playback() throws InterruptedException {
        final Statement statement = concurrency.consume();
        return statement;
    }
}
