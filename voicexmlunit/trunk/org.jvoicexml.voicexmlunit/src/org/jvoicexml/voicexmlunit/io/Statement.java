package org.jvoicexml.voicexmlunit.io;


public abstract class Statement implements Assertion {
	String message;
	
	public Statement(String message) {
		this.message = message;
	}
	
	/* (non-Javadoc)
	 * @see org.jvoicexml.voicexmlunit.io.Assertion#toString()
	 */
	@Override
	public String toString() {
		return message;
	}

	/* (non-Javadoc)
	 * @see org.jvoicexml.voicexmlunit.io.Assertion#receive(java.lang.String)
	 */
	@Override
	public abstract void receive(String actual);
	
	/* (non-Javadoc)
	 * @see org.jvoicexml.voicexmlunit.io.Assertion#send(org.jvoicexml.voicexmlunit.io.Recording)
	 */
	@Override
	public abstract void send(Recording record);
}
