package moten.david.ete.memory;

import java.io.Serializable;
import java.math.BigDecimal;

import moten.david.ete.Position;

public class MyPosition implements Position, Serializable {

	private final BigDecimal latitude;
	private final BigDecimal longitude;
	private final BigDecimal altitudeMetres;

	public MyPosition(BigDecimal latitude, BigDecimal longitude,
			BigDecimal altitudeMetres) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitudeMetres = altitudeMetres;
	}

	public MyPosition(BigDecimal latitude, BigDecimal longitude) {
		this(latitude, longitude, BigDecimal.ZERO);
	}

	@Override
	public BigDecimal getAltitudeMetres() {
		return altitudeMetres;
	}

	@Override
	public BigDecimal getLatitude() {
		return latitude;
	}

	@Override
	public BigDecimal getLongitude() {
		return longitude;
	}

}
