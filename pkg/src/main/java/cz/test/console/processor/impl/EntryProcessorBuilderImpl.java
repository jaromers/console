package cz.test.console.processor.impl;

import java.util.Arrays;
import java.util.List;

import cz.test.console.model.FeeEntry;
import cz.test.console.model.OutputEntry;
import cz.test.console.processor.api.EntryProcessor;
import cz.test.console.processor.api.EntryProcessorBuilder;
import io.reactivex.functions.Consumer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EntryProcessorBuilderImpl implements EntryProcessorBuilder {

	private static final int DEFAULT_INTERVAL = 60000;

	private long outputInterval = DEFAULT_INTERVAL;
	
	private Consumer<List<OutputEntry>> outputProcessor;
	
	private List<FeeEntry> fees = Arrays.asList(new FeeEntry(0, 0));
	

	@Override
	public EntryProcessor build() {
		return new EntryProcessorImpl(outputInterval, outputProcessor, fees);
	}

}
