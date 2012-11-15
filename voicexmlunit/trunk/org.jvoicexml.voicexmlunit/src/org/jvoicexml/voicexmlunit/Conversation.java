package org.jvoicexml.voicexmlunit;

import java.util.LinkedList;
import java.util.ListIterator;

public final class Conversation {
	private LinkedList<Statement> history;
	private ListIterator<Statement> iterator;
	
	public Conversation() {
		history = new LinkedList<Statement>();
		iterator = null;
	}

	public void addOutput(String message) {
		Output output = new Output(message);
		history.add(output);
	}

	public void addInput(String message) {
		Input input = new Input(message);
		history.add(input);
	}
	
	public Statement begin() {
		iterator = history.listIterator(0);
		return iterator.next();
	}
	
	public Statement next() {
		if (iterator.hasNext()) {
			return iterator.next();
		}
		else {
			iterator = null;
			return null;
		}
	}
}
