package cz.test.console.processor.api;

import java.util.List;

import cz.test.console.model.FeeEntry;
import cz.test.console.model.OutputEntry;
import io.reactivex.functions.Consumer;

public interface EntryProcessorBuilder {

	public EntryProcessorBuilder setOutputInterval(long millis);
	
	public EntryProcessorBuilder setOutputProcessor(Consumer<List<OutputEntry>> outputProcessor);
	
	public EntryProcessorBuilder setFees(List<FeeEntry> fees);
	
	public EntryProcessor build();
}
