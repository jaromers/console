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
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class EntryProcessorImpl implements EntryProcessor {

	private boolean stop = false;
	
	private ObservableEmitter<InputEntry> emittor;
	
	private Observable<InputEntry> eventProcessor = Observable.create(arg -> {emittor = arg;});

	private Disposable processorSubscription;
	
	private List<InputEntry> recordedItems = new ArrayList<>();
	
	private List<FeeEntry> fees;

	public EntryProcessorImpl(long outputInterval, Consumer<List<OutputEntry>> outputEntryProcessor, List<FeeEntry> fees) {
		this.fees = fees.stream()
				.sorted((i, j) -> (int)Math.signum(j.getWeight() - i.getWeight()))
				.collect(Collectors.toList());
		processorSubscription = 
				makeEventsTickEvenIfNoInput(
						eventProcessor
							.serialize()
							.map(item -> {synchronized(recordedItems) {recordedItems.add(item); return recordedItems;}}),							
						outputInterval
						)
					.throttleLast(outputInterval, TimeUnit.MILLISECONDS)					
					.takeUntil((items) -> stop)
					.map(this::collectStatistics)
					.subscribe(outputEntryProcessor);
	}

	public List<OutputEntry> collectStatistics(List<InputEntry> inputEntries) {
		synchronized(inputEntries) {
			return inputEntries.stream()
//				.collect(Collectors.groupingBy(i -> i.getZipCode(), Collectors.summingDouble(i -> i.getWeight())))
				.collect(Collectors.groupingBy(i -> i.getZipCode(), Collector.of(
							() -> new FeeEntry(),
							(agg, inputEntry) -> {
								agg.setWeight(agg.getWeight() + inputEntry.getWeight());
								agg.setFee(agg.getFee() + findFee(inputEntry.getWeight()));
								},
							(agg1, agg2) -> new FeeEntry(agg1.getWeight() + agg2.getWeight(), agg1.getFee() + agg2.getFee())
						)))
				.entrySet().stream()
				.map(entry -> new OutputEntry(entry.getKey(), entry.getValue().getWeight(), entry.getValue().getFee()))
				.sorted((i, j) -> (int)Math.signum(j.getWeight() - i.getWeight()))
				.collect(Collectors.toList());
		}
	}

	private double findFee(double weight) {
		return this.fees.stream()
			.filter(i -> i.getWeight() <= weight)
			.findFirst()
			.get()
			.getFee();
	}

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
