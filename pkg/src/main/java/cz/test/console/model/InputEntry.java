package cz.test.console.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Input entry representation. Data is supposed to be read from console.
 * @author jarom
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InputEntry {
	/**
	 * Destination zip code, where the package is supposed to be sent.
	 */
	protected String zipCode;
	
	/**
	 * Weight of the item to be sent.
	 */
	protected double weight;
}
