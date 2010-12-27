package viemJava;

import java.util.Date;

import scala.collection.immutable.HashSet;
import scala.collection.immutable.Set;
import viem.Identifier;
import viem.IdentifierType;
import viem.MergeValidator;
import viem.Merger;
import viem.MetaData;
import viem.MetaSet;
import viem.TimedIdentifier;

public class Util {

	public static void test() {

		MergeValidator validator = new MergeValidator() {
			public boolean mergeIsValid(MetaData a, MetaData b) {
				return true;
			}
		};
		Merger merger = new Merger(validator, false);
		// MetaSet m1 = new MetaSet(new HashSet())
		Set<MetaSet> matches = new HashSet<MetaSet>();

		MetaSet a = create(1, id(1, 1, 1), id(2, 2, 1));
		MetaSet b = create(2, id(1, 1, 0));
		MetaSet c = create(3, id(2, 2, 0));
		Set<MetaSet> result = merger.merge(a, matches);
		System.out.println(result);
	}

	private static TimedIdentifier id(int name, int value, int time) {
		scala.math.BigDecimal d = new scala.math.BigDecimal(new java.math.BigDecimal(time));
		return new TimedIdentifier(new Identifier(
				new IdentifierType(name + ""), value + ""), d);
	}

	private static MetaSet create(int metaData, TimedIdentifier... identifiers) {
		HashSet<TimedIdentifier> set = new HashSet<TimedIdentifier>();
		for (TimedIdentifier id : identifiers)
			set = set.$plus(id);
		return new MetaSet(set, new MetaData() {
		});
	}

	private static class MyMetaData implements MetaData {
		private final int value;

		public MyMetaData(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		@Override
		public String toString() {
			return value + "";
		}
	}

	public static void main(String[] args) {
		Util.test();
	}
}
