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
	protected String zipCode;
	protected double weight;
}
