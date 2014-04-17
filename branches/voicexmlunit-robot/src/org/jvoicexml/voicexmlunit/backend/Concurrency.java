package org.jvoicexml.voicexmlunit.backend;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.jvoicexml.voicexmlunit.io.Statement;

/**
 * Concurrency for Statement synchronization between multiple threads.
 * The Concurrency must block a read access until another thread has written.
 * @author raphael
 */
public class Concurrency {
   
    /* http://tutorials.jenkov.com/java-util-concurrent/index.html */
    final BlockingQueue<Statement> queue;
    
    public Concurrency() {
        queue = new LinkedBlockingQueue();
    }
    
    public void produce(final Statement statement) throws InterruptedException {
        queue.put(statement);
    }
    
    public Statement consume() throws InterruptedException {
        return queue.take();
    }
        
}
