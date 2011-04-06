package org.moten.david.util.xsd.simplified;

import java.util.ArrayList;
import java.util.List;

public class BasicGroup implements Group {
	private List<Particle> particles = new ArrayList<Particle>();

	public void setParticles(List<Particle> particles) {
		this.particles = particles;
	}

	public void setMaxOccurs(MaxOccurs maxOccurs) {
		this.maxOccurs = maxOccurs;
	}

	public void setMinOccurs(int minOccurs) {
		this.minOccurs = minOccurs;
	}

	private MaxOccurs maxOccurs = new MaxOccurs();
	private int minOccurs = 1;

	public BasicGroup() {

	}

	@Override
	public MaxOccurs getMaxOccurs() {
		return maxOccurs;
	}

	@Override
	public String toString() {
		return "BasicGroup [particles=" + particles + ", maxOccurs="
				+ maxOccurs + ", minOccurs=" + minOccurs + "]";
	}

	@Override
	public int getMinOccurs() {
		return minOccurs;
	}

	@Override
	public List<Particle> getParticles() {
		return particles;
	}

	public static class Builder {
		private final BasicGroup g;

		public Builder() {
			g = new BasicGroup();
		}

		public Group build() {
			return g;
		}

		public Builder particle(Particle particle) {
			g.particles.add(particle);
			return this;
		}

		public Builder maxOccurs(MaxOccurs maxOccurs) {
			g.maxOccurs = maxOccurs;
			return this;
		}

		public Builder minOccurs(int minOccurs) {
			g.minOccurs = minOccurs;
			return this;
		}
	}
}
