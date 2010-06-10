package moten.david.markup;

public class Util {
	public static boolean intersect(int aMin, int aMax, int bMin, int bMax,
			int tolerance) {
		if (aMin >= bMin - tolerance && aMin <= bMax + tolerance)
			return true;
		else if (bMin >= aMin - tolerance && bMin <= aMax + tolerance)
			return true;
		else
			return false;
	}
}
