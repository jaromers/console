package cz.test.console.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Output entry - written to console.
 * @author jarom
 *
 */
@Getter
@Setter
public class OutputEntry extends InputEntry {
	
	private double totalFee;
	
	public OutputEntry(String zipCode, double weight) {
		super(zipCode, weight);
	}
	
	public OutputEntry(String zipCode, double weight, double fee) {
		super(zipCode, weight);
		setTotalFee(fee);
	}
}
