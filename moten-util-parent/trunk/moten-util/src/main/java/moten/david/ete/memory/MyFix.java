package moten.david.ete.memory;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;

import moten.david.ete.AbstractFix;
import moten.david.ete.Fix;
import moten.david.ete.Identifier;
import moten.david.ete.Position;
import moten.david.ete.Util;

/**
 * Note: this class has a natural ordering that is inconsistent with equals (the
 * ordering is based on time only). The equals method behaves as follows:
 * Returns true if and only if the other instance has the same time and position
 * and velocity and at least one common identifier.
 * 
 * This class should be immutable to avoid unwanted side-effects in processing.
 * 
 * @author dxm
 */
public class MyFix extends AbstractFix implements Serializable {

	private static final long serialVersionUID = -6187835231846959028L;

	private final MyPosition position;
	private final Calendar time;
	private final Map<String, String> properties = new HashMap<String, String>();
	private final Velocity velocity;
	private final SortedSet<? extends Identifier> identifiers;

	public MyFix(SortedSet<? extends Identifier> identifiers,
			MyPosition position, Velocity velocity, Calendar time) {
		this.position = position;
		this.velocity = velocity;
		this.time = time;
		this.identifiers = identifiers;
	}

	public MyFix(SortedSet<Identifier> identifiers, MyPosition position,
			Calendar time) {
		this(identifiers, position, null, time);
	}

	@Override
	public SortedSet<? extends Identifier> getIdentifiers() {
		return identifiers;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	public Velocity getVelocity() {
		return velocity;
	}

	public Map<String, String> getProperties() {
		return properties;
	}

	@Override
	public Calendar getTime() {
		return time;
	}

	@Override
	public int compareTo(Fix o) {
		return getTime().compareTo(o.getTime());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyFix other = (MyFix) obj;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		if (velocity == null) {
			if (other.velocity != null)
				return false;
		} else if (!velocity.equals(other.velocity))
			return false;
		if (identifiers == null) {
			if (other.identifiers != null)
				return false;
		}
		if (!Util.haveCommonIdentifier(identifiers, other.identifiers))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MyFix [identifiers=" + identifiers + ", position=" + position
				+ ", time=" + time + ", velocity=" + velocity + "]";
	}

}
