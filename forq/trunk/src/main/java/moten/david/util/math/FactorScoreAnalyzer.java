package moten.david.util.math;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import moten.david.util.math.Matrix.FactorScoreCombination;
import moten.david.util.math.Matrix.FactorScoreStrategy;
import moten.david.util.math.Varimax.RotationMethod;
import au.edu.anu.delibdem.qsort.Data;

public class FactorScoreAnalyzer {

	private final Matrix scores;// can be z scores

	private static int counter = 0;

	private FactorScoreCombination factorScoreCombination;

	public FactorScoreAnalyzer(Matrix m) {
		this.scores = m;
	}

	private static synchronized int nextCounter() {
		counter++;
		return counter;
	}

	public void format(SimpleHeirarchicalFormatter f) {

		LinkedHashMap<Double, LinkedHashMap<Integer, TreeSet<Integer>>> map = getGrouped();
		f
				.item("initial significant score threshold = score mean + standard deviation");
		for (Double scoreThreshold : map.keySet()) {
			f.item("significant score threshold = " + scoreThreshold);
			StringTable table = new StringTable();
			for (Integer statement : map.get(scoreThreshold).keySet()) {
				table.addRow();
				table.addEntry("Stmt" + statement);
				for (Integer factor : map.get(scoreThreshold).get(statement)) {
					table.addEntry(factor + "");
				}
			}
			f.item(table);
		}

	}

	public LinkedHashMap<Integer, TreeSet<Integer>> getGrouped(
			double threshold, boolean[] selected) {
		double mean = this.scores.getColumnVector(1).getMean();
		Matrix matrix = this.scores.copy();
		if (selected != null) {
			for (int i = selected.length - 1; i >= 0; i--) {
				if (!selected[i])
					matrix = matrix.removeColumn(i + 1);
			}
		}
		LinkedHashMap<Integer, TreeSet<Integer>> map = new LinkedHashMap<Integer, TreeSet<Integer>>();
		for (int i = 1; i <= matrix.rowCount(); i++) {
			for (int j = 1; j <= matrix.columnCount(); j++) {
				if (matrix.getValue(i, j) >= threshold) {
					if (map.get(i) == null)
						map.put(i, new TreeSet<Integer>());
					map.get(i).add(j);
				}
				if (matrix.getValue(i, j) <= mean - (threshold - mean)) {
					if (map.get(i) == null)
						map.put(i, new TreeSet<Integer>());
					map.get(i).add(-j);
				}
			}
		}
		return map;
	}

	public LinkedHashMap<Double, LinkedHashMap<Integer, TreeSet<Integer>>> getGrouped() {
		LinkedHashMap<Double, LinkedHashMap<Integer, TreeSet<Integer>>> map = new LinkedHashMap<Double, LinkedHashMap<Integer, TreeSet<Integer>>>();
		double mean = scores.getColumnVector(1).getMean();
		double sd = scores.getColumnVector(1).getStandardDeviation();
		double upper = mean + sd;
		double max = scores.getColumnVector(1).getMaximum();
		for (double d = upper; d < max; d += sd / 3) {
			map.put(d, getGrouped(d, null));
		}
		return map;
	}

	public Map<String, String> getVennMappings(Double thresh,
			boolean selected[]) {
		Map<Integer, TreeSet<Integer>> vennData = getGrouped(thresh, selected);
		Map<String, String> map = new HashMap<String, String>();
		Set<Integer> factors = new HashSet<Integer>();
		for (Integer question : vennData.keySet()) {
			StringBuffer positives = new StringBuffer();
			StringBuffer negatives = new StringBuffer();
			for (Integer factor : vennData.get(question)) {
				factors.add(Math.abs(factor));
				if (factor > 0) {
					positives.append(factor);
				} else
					negatives.append(Math.abs(factor));
			}
			char[] b1 = positives.toString().toCharArray();
			Arrays.sort(b1);
			String pos = String.valueOf(b1);
			char[] b2 = negatives.toString().toCharArray();
			Arrays.sort(b2);
			String neg = String.valueOf(b2);
			if (pos.length() > 0) {
				String questions = map.get(pos);
				if (questions == null)
					questions = "";
				if (questions.length() > 0)
					questions += ",";
				questions += question + "";
				map.put(pos, questions);
			}
			if (neg.length() > 0) {
				String questions = map.get(neg);
				if (questions == null)
					questions = "";
				if (questions.length() > 0)
					questions += ",";
				questions += (-question) + "";
				map.put(neg, questions);
			}
		}
		return map;
	}

	public static void main(String[] args) throws IOException,
			FactorAnalysisException {
		Data data = new Data("src/New Mexico.txt");
		String participantType = "all";
		String stage = "all";
		int axis = 1;
		double threshold = 1;
		boolean doPca = true;
		boolean doCentroid = false;
		Matrix raw = data.getRawData(participantType, stage, null, null, axis);

		FactorAnalysisResults results = raw
				.analyzeFactors(
						FactorExtractionMethod.PRINCIPAL_COMPONENTS_ANALYSIS,
						1.0, null);
		double scoresThreshold = 2.58 / Math.sqrt(raw.rowCount());
		Matrix m = raw.getFactorScores(results.getRotatedLoadings().get(
				RotationMethod.VARIMAX), scoresThreshold,
				FactorScoreStrategy.ZERO_ROWS_WITH_MORE_THAN_ONE_ENTRY);

		FactorScoreAnalyzer a = new FactorScoreAnalyzer(m);
		a.format(FactorAnalysisResults.getTextFormatter(new PrintWriter(
				System.out)));
	}

	public void format2(SimpleHeirarchicalFormatter f) {
		LinkedHashMap<Double, LinkedHashMap<Integer, TreeSet<Integer>>> map = getGrouped();
		f
				.item("initial significant score threshold = score mean + standard deviation");
		for (Double scoreThreshold : map.keySet()) {
			f.item("significant score threshold = " + scoreThreshold);
			StringTable table = new StringTable();
			Map<Integer, TreeSet<Integer>> map2 = new TreeMap<Integer, TreeSet<Integer>>();
			for (Integer question : map.get(scoreThreshold).keySet()) {
				for (Integer factor : map.get(scoreThreshold).get(question)) {
					boolean positive = factor > 0;
					factor = Math.abs(factor);
					if (map2.get(factor) == null)
						map2.put(factor, new TreeSet<Integer>());
					map2.get(factor).add((positive ? question : -question));
				}
			}
			for (Integer factor : map2.keySet()) {
				table.addRow();
				table.addEntry("Factor " + factor);
				for (Integer question : map2.get(factor)) {
					table.addEntry(question + "");
				}
			}
			f.item(table);
		}
	}

	public void formatVenns(SimpleHeirarchicalFormatter f) {
		LinkedHashMap<Double, LinkedHashMap<Integer, TreeSet<Integer>>> map = getGrouped();
		for (Double scoreThreshold : map.keySet()) {
			Map<Integer, TreeSet<Integer>> m2 = map.get(scoreThreshold);
			String imageTitle = "Venn Diagram, score threshold = "
					+ new DecimalFormat("0.00").format(scoreThreshold);
			f.header(imageTitle, true);
			f.blockStart();
			f.image("Image", "fsa-venn-" + nextCounter(), m2, "venn");
			f.blockFinish();
		}
	}

	public void setFactorScoreCombination(
			FactorScoreCombination factorScoreCombination) {
		this.factorScoreCombination = factorScoreCombination;

	}

	public FactorScoreCombination getFactorScoreCombination() {
		return factorScoreCombination;
	}

	public Matrix getScores() {
		return scores;
	}
}