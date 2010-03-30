package moten.david.ete;

import java.math.BigDecimal;

/**
 * The type of the entity. Includes some physical limitations of the entity to
 * be used in the calculation of merge suitability.
 * 
 * @author dave
 * 
 */
public interface EntityType {
	/**
	 * The maximum speed of the entity in metres per second.
	 * 
	 * @return
	 */
	BigDecimal getMaximumSpeedMetresPerSecond();

	/**
	 * The minimum period of measurement after which speed calculations are
	 * worth or attempting.
	 * 
	 * @return
	 */
	BigDecimal getMinimumTimeForSpeedCalculationSeconds();
}
