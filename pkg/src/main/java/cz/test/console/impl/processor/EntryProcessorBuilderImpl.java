package cz.test.console.impl.processor;

import java.util.Arrays;
import java.util.List;

import cz.test.console.api.processor.EntryProcessor;
import cz.test.console.api.processor.EntryProcessorBuilder;
import cz.test.console.model.FeeEntry;
import cz.test.console.model.OutputEntry;
import io.reactivex.functions.Consumer;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class EntryProcessorBuilderImpl implements EntryProcessorBuilder {

	/**
	 * Default interval is supposed to be 1 minute.
	 */
	private static final int DEFAULT_INTERVAL = 60000;

	private long outputInterval = DEFAULT_INTERVAL;
	
	private Consumer<List<OutputEntry>> outputProcessor;
	
	/**
	 * Default value is 0, 0 - for all weights consider fee 0.
	 */
	private List<FeeEntry> fees = Arrays.asList(new FeeEntry(0, 0));
	

	@Override
	public EntryProcessor build() {
		return new EntryProcessorImpl(outputInterval, outputProcessor, fees);
	}

}
