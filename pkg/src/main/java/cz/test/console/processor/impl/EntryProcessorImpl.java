package cz.test.console.processor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import cz.test.console.model.FeeEntry;
import cz.test.console.model.InputEntry;
import cz.test.console.model.OutputEntry;
import cz.test.console.processor.api.EntryProcessor;
import cz.test.console.processor.api.EntryProcessorBuilder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Process input entries.
 * Input entries are provided using {@link #processInputEntry(InputEntry)}.
 * To stop the processor, call {@link #close()}.
 * The EntryProcessor is going to publish expected aggregate statistics
 * using the consumer provided during its construction using {@link EntryProcessorBuilder}
 * 
 * @author jarom
 *
 */
public class EntryProcessorImpl implements EntryProcessor {

	/**
	 * If the processor should stop processing.
	 */
	private boolean stop = false;
	
	/**
	 * Emit events (input entries) using rxjava.
	 */
	private ObservableEmitter<InputEntry> emittor;
	
	/**
	 * Create observable that is going to emit events (input entries) and start their processing.
	 */
	private Observable<InputEntry> eventProcessor = Observable.create(arg -> {emittor = arg;});

	/**
	 * Remember subscription to be able to stop processing.
	 */
	private Disposable processorSubscription;
	
	/**
	 * Collect entries provided so far from input.
	 */
	private List<InputEntry> recordedItems = new ArrayList<>();
	
	/**
	 * Configured fees. 
	 */
	private List<FeeEntry> fees;

	public EntryProcessorImpl(long outputInterval, Consumer<List<OutputEntry>> outputEntryProcessor, List<FeeEntry> fees) {
		
		// Sort fees in descending order based on their weight - to aid in searching for a fee corresponding to a weight.
		this.fees = fees.stream()
				.sorted((i, j) -> (int)Math.signum(j.getWeight() - i.getWeight()))
				.collect(Collectors.toList());
		
		processorSubscription = 
				// The statistics is supposed to be generated in regular intervals, even if no input entries arrive
				// during that interval. Because of that, additional events have to be generated in order to ensure
				// the statistics are generated timely.
				makeEventsTickEvenIfNoInput(
						eventProcessor
							// just to be sure - if inputs can be provided in parallel.
							.serialize()
							// record the item
							.map(item -> {synchronized(recordedItems) {recordedItems.add(item); return recordedItems;}}),							
						outputInterval
						)
					// Only at the end of time interval continue processing statistics.
					.throttleLast(outputInterval, TimeUnit.MILLISECONDS)				
					// If the processor was closed, provide statics no longer.
					.takeUntil((items) -> stop)
					// Perform the actual statistics collection.
					.map(this::collectStatistics)
					// Call the output processor to let it do whatever is necessary to do with the output.
					.subscribe(outputEntryProcessor);
	}

	/**
	 * Collect statistics for recorded entries:
	 * For each zip code provide
	 * 1.) sum of weights
	 * 2.) sum of fees
	 *  
	 * @param inputEntries
	 * @return
	 */
	public List<OutputEntry> collectStatistics(List<InputEntry> inputEntries) {
		synchronized(inputEntries) {
			return inputEntries.stream()
//				.collect(Collectors.groupingBy(i -> i.getZipCode(), Collectors.summingDouble(i -> i.getWeight())))
				.collect(Collectors.groupingBy(
						// group items by zip code
						i -> i.getZipCode(),
						// And then for each zip code, sum weight and fees. Find appropriate fee using findFee.
						Collector.of(
							() -> new FeeEntry(),
							(agg, inputEntry) -> {
								agg.setWeight(agg.getWeight() + inputEntry.getWeight());
								agg.setFee(agg.getFee() + findFee(inputEntry.getWeight()));
								},
							(agg1, agg2) -> new FeeEntry(agg1.getWeight() + agg2.getWeight(), agg1.getFee() + agg2.getFee())
						)))
				.entrySet().stream()
				// must remap statistics to output entries.
				.map(entry -> new OutputEntry(entry.getKey(), entry.getValue().getWeight(), entry.getValue().getFee()))
				// the output is to be sorted by weight in descending order.
				.sorted((i, j) -> (int)Math.signum(j.getWeight() - i.getWeight()))
				.collect(Collectors.toList());
		}
	}

	/**
	 * Find fee for a given weight.
	 * Since the fees are sorted in descending order, it should be the first fee item, whose 
	 * weight is less or equal to given weight.
	 * 
	 * @param weight
	 * @return
	 */
	private double findFee(double weight) {
		return this.fees.stream()
			.filter(i -> i.getWeight() <= weight)
			.findFirst()
			.map(FeeEntry::getFee)
			// if no appropriate definition is found, take fee 0 - we could have thrown exception.
			// Depends on required business logic.
			.orElse(0d);
	}

	/**
	 * Combine events from input with events from a scheduler - so that we generate statistics even if no
	 * input is provided. 
	 * 
	 * @param eventProcessor
	 * @param outputInterval
	 * @return
	 */
	private Observable<List<InputEntry>> makeEventsTickEvenIfNoInput(Observable<List<InputEntry>> eventProcessor, long outputInterval) {
		return Observable.combineLatest(
				eventProcessor,
				Observable.interval(outputInterval, TimeUnit.MILLISECONDS),
				(left, right) -> left
				);
	}
	
	@Override
	public void processInputEntry(InputEntry entry) {
		emittor.onNext(entry);
	}

	@Override
	public void close() {
		stop = true;
		processorSubscription.dispose();
	}
		
}
