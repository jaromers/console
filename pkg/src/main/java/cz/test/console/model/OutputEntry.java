package cz.test.console.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Output entry - written to console.
 * 
 * Output entry happens to contain similar fields to input entry (zipcode and weight,
 * although the weight for input is supposed to be the total weight).
 * If needed OutputEntry can be made independent from InputEntry.
 * 
 * It also contains totalFee - the sum of fees for a given destination code.
 * 
 * @author jarom
 *
 */
@Getter
@Setter
public class OutputEntry extends InputEntry {
	
	/**
	 * The sum of fees for packages with given destination zip code.
	 */
	private double totalFee;
	
	public OutputEntry(String zipCode, double weight) {
		super(zipCode, weight);
	}
	
	public OutputEntry(String zipCode, double weight, double fee) {
		super(zipCode, weight);
		setTotalFee(fee);
	}
}
