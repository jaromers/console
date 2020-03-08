package cz.test.console.processor.api;

import cz.test.console.model.InputEntry;

public interface EntryProcessor {
	
	public void processInputEntry(InputEntry inputEntry);
	
	public void close();
}
