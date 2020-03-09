package cz.test.console.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entry specifying fees for different weight ranges.
 * Ranges are specified as an array of FeeEntry.
 * The entries are to be sorted. For every item with a weight w the fee is given as follows:
 * 
 * if arr[i].weight <= w and w < arr[i+1] or there is no item arr[i+1] (i.e. arr[i] is the last item in arr)
 *  then the applicable fee is arr[i].fee.
 * 
 * @author jarom
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeeEntry {
	private double weight;
	private double fee;
}
