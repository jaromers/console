package cz.test.console.api.processor;

import java.util.List;

import cz.test.console.model.FeeEntry;
import cz.test.console.model.OutputEntry;
import io.reactivex.functions.Consumer;

/**
 * Create and configure a new instance of an EntryProcessor.
 * 
 * @author jarom
 *
 */
public interface EntryProcessorBuilder {

	/**
	 * In what intervals the processor should provide summary/aggregation data.
	 * 
	 * @param millis	Time interval specified in milliseconds.
	 * @return
	 */
	public EntryProcessorBuilder setOutputInterval(long millis);
	
	/**
	 * Consumer to be used to process EntryProcessor's output - i.e list of output entries.
	 * 
	 * @param outputProcessor
	 * @return
	 */
	public EntryProcessorBuilder setOutputProcessor(Consumer<List<OutputEntry>> outputProcessor);
	
	/**
	 * Provide fees configuration. 
	 * @see FeeEntry
	 * 
	 * @param fees
	 * @return
	 */
	public EntryProcessorBuilder setFees(List<FeeEntry> fees);
	
	/**
	 * Create a configured EntryProcessor.
	 * 
	 * @return Object to be used to provide input entries (as parsed from console).
	 */
	public EntryProcessor build();
}
