package cz.test.console.processor.api;

import cz.test.console.model.InputEntry;

/**
 * Processor of input entries. 
 * An instance of this interface is built using EntityProcessorBuilder 
 * that provides means for configuration of the processor.
 * This interface is used only for providing input - entries to be processed.
 * 
 * 
 * @author jarom
 *
 */
public interface EntryProcessor {
	
	/**
	 * Provide / give new entry to be processed.
	 * @param inputEntry
	 */
	public void processInputEntry(InputEntry inputEntry);
	
	/**
	 * Stop processing.
	 */
	public void close();
}
