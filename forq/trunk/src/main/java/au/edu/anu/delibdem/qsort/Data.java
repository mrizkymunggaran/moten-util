package au.edu.anu.delibdem.qsort;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import moten.david.util.math.FactorAnalysisException;
import moten.david.util.math.FactorAnalysisResults;
import moten.david.util.math.FactorExtractionMethod;
import moten.david.util.math.Function;
import moten.david.util.math.Matrix;
import moten.david.util.math.RegressionIntervalFunction;
import moten.david.util.math.SimpleHeirarchicalFormatter;
import moten.david.util.math.Vector;
import moten.david.util.math.Varimax.RotationMethod;
import moten.david.util.math.gui.GraphPanel;

import org.apache.commons.math.stat.regression.SimpleRegression;

public class Data implements Serializable {

	public static final String participantPrefix = "P";
	private static final long serialVersionUID = -8216642174736641063L;
	private static final String TAB = "\t";
	public static final String[] COLUMN_PREFIX_RANKING_RESULTS_FORCED = new String[] {
			"Ranking results::F", "RF" };
	public static final String[] COLUMN_PREFIX_RANKING_RESULTS_UNFORCED = new String[] {
			"Ranking results::U", "RU" };
	public static final String[] COLUMN_PREFIX_Q_RESULTS_FORCED = new String[] {
			"Q results agreement::F", "QF" };
	public static final String[] COLUMN_PREFIX_Q_RESULTS_UNFORCED = new String[] {
			"Q results agreement::U", "QU" };
	public static final String[] COLUMN_PREFIX_METACONSENSUS_UNFORCED = new String[] {
			"Metaconsensus::U", "MU" };
	public static final String[] COLUMN_PARTICIPANT_NO = new String[] { "Participant No" };
	public static final String[] COLUMN_PARTICIPANT_TYPE = new String[] {
			"Participant Info::Participant Type",
			"Pariticipant Info::Participant Type", "Participant Type" };
	public static final String[] COLUMN_STAGE = new String[] { "Stage" };

	private final Map<Integer, String> statements = new HashMap<Integer, String>();

	public static String[] getSignificantColumnNames() {
		// return new String[] { COLUMN_PARTICIPANT_NO, COLUMN_PARTICIPANT_TYPE,
		// COLUMN_PARTICIPANT_TYPE_ALTERNATE,
		// COLUMN_PREFIX_Q_RESULTS_FORCED,
		// COLUMN_PREFIX_Q_RESULTS_UNFORCED,
		// COLUMN_PREFIX_RANKING_RESULTS_FORCED,
		// COLUMN_PREFIX_RANKING_RESULTS_UNFORCED };
		return new String[] { "not implemented" };
	}

	public static final String CONFIDENCE_BANDS_95 = "Confidence_Bands_95";
	public static final String PREDICTION_INTERVAL_95 = "Prediction_Interval_95";

	private final Set<Integer> excludeParticipants = new TreeSet<Integer>();

	public List<Integer> getParticipants() {
		TreeSet<Integer> set = new TreeSet<Integer>();
		for (QSort q : qSorts) {
			set.add(q.getParticipantId());
		}
		return new ArrayList<Integer>(set);
	}

	private List<QSort> qSorts;
	private String title = "Untitled";

	public Data(String name) throws IOException {
		System.out.println("loading " + name);
		InputStream is = new FileInputStream(name);
		load(is);
		is.close();
	}

	public Data(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		load(is);
		is.close();
	}

	public Data(InputStream is) throws IOException {
		load(is);
	}

	public Data() throws IOException {
	}

	public Set<String> getStageTypes() {
		Set<String> set = new TreeSet<String>();
		for (QSort q : qSorts) {
			set.add(q.getStage());
		}
		return set;
	}

	public Set<String> getParticipantTypes() {
		Set<String> set = new TreeSet<String>();
		for (QSort q : qSorts) {
			set.add(q.getParticipantType());
		}
		return set;
	}

	private boolean startsWith(String[] options, String s) {
		for (String option : options) {
			if (s.startsWith(option))
				return true;
		}
		return false;
	}

	private boolean equalsIgnoreCase(String[] options, String s) {
		for (String option : options) {
			if (s.equalsIgnoreCase(option))
				return true;
		}
		return false;
	}

	public void load(InputStream is) throws IOException {
		System.out.println("loading data..");
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		this.qSorts = new ArrayList<QSort>();
		String[] columns = null;
		int lineNumber = 0;
		//input file is organized as 
		//data then statements
		boolean readingData = true;
		boolean readingStatements = false;
		Set<String> unrecognizedColumns = new HashSet<String>();
		while ((line = br.readLine()) != null) {
			System.out.println(line);
			lineNumber++;
			if (line.trim().equals("#Statements")) {
				readingStatements = true;
				readingData = false;
				line = br.readLine();
				if (line == null)
					break;
			}
			if (line.startsWith("#Title ")) {
				title = line.substring("#Title ".length());
				line = br.readLine();
			}
			if (line.trim().equals("#Data")) {
				// read header
				line = br.readLine();
				//participantId, participantVariables, Q1, .., Qn,[P1,..,Pm]
				//participantId is a string value
				//participantVariables are strings without tabs, delimited by colon ':'. 
				//Sample input
				//----------------------
				//StatementCount\t3
				//PreferenceStatementCount\t2
				//ParticipantId\tParticipantVariables\tQ1\tQ2\tQ3\tP1\tP2
				//Dave Moten\tRegion_Canberra:Type_Lay_Participant\t5\t4\t1\t3\t-3
				columns = line.split(TAB);
				System.out.println(line);
				line = br.readLine();
				System.out.println(line);
			}
			
			String[] items = line.split(TAB);

			if (readingData) {
				QSort q = new QSort();
				for (int i = 0; i < items.length; i++) {
					String col = columns[i].trim();
					if (equalsIgnoreCase(COLUMN_PARTICIPANT_NO, col)) {
						q.setParticipantId(getInt(items[i]));
					} else if (equalsIgnoreCase(COLUMN_PARTICIPANT_TYPE, col)) {
						q.setParticipantType(items[i]);
					} else if (equalsIgnoreCase(COLUMN_STAGE, col)) {
						q.setStage(items[i]);
					} else if (startsWith(COLUMN_PREFIX_Q_RESULTS_UNFORCED, col)) {
						q.getQResults(false).add(getDouble(items[i]));
					} else if (startsWith(COLUMN_PREFIX_Q_RESULTS_FORCED, col)) {
						q.getQResults(true).add(getDouble(items[i]));
					} else if (startsWith(
							COLUMN_PREFIX_RANKING_RESULTS_UNFORCED, col)) {
						q.getRankings(false).add(getDouble(items[i]));
					} else if (startsWith(COLUMN_PREFIX_RANKING_RESULTS_FORCED,
							col)) {
						q.getRankings(true).add(getDouble(items[i]));
					} else if (startsWith(COLUMN_PREFIX_METACONSENSUS_UNFORCED,
							col)) {
						q.getMetaconsensus().add(getDouble(items[i]));
					} else {
						unrecognizedColumns.add(col);
					}
				}
				if (q.getParticipantType() == null) {
					System.out.println("unrecognizedColumns="
							+ unrecognizedColumns);
					throw new Error("participant type not found");
				}
				String[] participantTypes = q.getParticipantType().split(";");
				for (String participantType : participantTypes) {
					QSort q2 = q.copy();
					q2.setParticipantType(participantType.trim());
					qSorts.add(q2);
				}
			} else if (readingStatements) {
				statements.put(Integer.parseInt(items[0]), items[1]);
			}
		}
		br.close();
		isr.close();
		is.close();
		System.out.println("unrecognizedColumns=" + unrecognizedColumns);
		System.out.println("loaded");
	}

	private Double getDouble(String s) {
		if (s == null || s.trim().equals(""))
			return null;
		else
			return Double.parseDouble(s);
	}

	private Integer getInt(String s) {
		if (s == null || s.trim().equals(""))
			return null;
		else
			return Integer.parseInt(s);
	}

	private int getTerminatingInteger(String s) {
		StringBuffer num = new StringBuffer();
		int i = s.length() - 1;
		while (s.charAt(i) >= '0' && s.charAt(i) <= '9') {
			num.insert(0, s.charAt(i));
			i--;
		}
		Integer result = Integer.parseInt(num.toString());
		return result;
	}

	public Map<Integer, Integer> getOrdered(String columns[], String[] items,
			int starti, String label) {
		Map<Integer, Integer> ordered = new LinkedHashMap<Integer, Integer>();
		int i = starti;
		while (columns[i].startsWith(label)) {
			int n = getTerminatingInteger(columns[i]);
			ordered.put(n, getInt(items[i++]));
		}
		return ordered;
	}

	public List<QSort> getQSorts() {
		List<QSort> list = new ArrayList<QSort>(qSorts);
		for (int i = list.size() - 1; i >= 0; i--)
			if (excludeParticipants.contains(list.get(i).getParticipantId())) {
				list.remove(i);
			}
		return list;
	}

	public void graph(boolean forced, String participantType, String stage,
			String bands, OutputStream imageOutputStream, boolean labelPoints,
			int size, Set<Integer> exclusions, Set<Integer> filter)
			throws IOException {
		List<QSort> subList = restrictList(forced, participantType, stage,
				exclusions);
		if (stage.equals("all"))
			graphConnected(subList, forced, imageOutputStream, labelPoints,
					size, filter);
		else
			graph(subList, forced, imageOutputStream, labelPoints, size,
					filter, bands);
	}

	public DataComponents getDataComponents(boolean forced,
			String participantType, String stage, Set<Integer> exclusions,
			Set<Integer> filter) {
		List<QSort> subList = restrictList(forced, participantType, stage,
				exclusions);
		return buildMatrix(subList, forced, filter);

	}

	public List<QSort> restrictList(boolean forced, String participantType,
			String stage, Set<Integer> exclusions) {
		// read data
		List<QSort> list = getQSorts();

		List<QSort> subList = new ArrayList<QSort>();
		for (QSort q : list) {
			if ((stage.equalsIgnoreCase("all") || q.getStage().trim()
					.equalsIgnoreCase(stage))
					&& (participantType.equalsIgnoreCase("all") || q
							.getParticipantType().trim()
							.equals(participantType))
					&& (exclusions == null || !exclusions.contains(q
							.getParticipantId()))) {

				boolean alreadyGotIt = false;
				for (QSort q2 : subList) {
					if (q2.getParticipantId().equals(q.getParticipantId())
							&& q2.getStage().equals(q.getStage())) {
						alreadyGotIt = true;
						break;
					}
				}
				if (!alreadyGotIt)
					subList.add(q);
			}
		}
		return subList;
	}

	public static class DataComponents {
		public List<QSort> list;
		public Matrix qSorts;
		public Matrix rankings;
		public Matrix correlations;
	}

	public DataComponents buildMatrix(List<QSort> list, boolean forced,
			Set<Integer> filter) {
		if (list == null)
			return null;
		list = new ArrayList<QSort>(list);

		// remove from the list all QSort objects that are missing a qResult or
		// a ranking value
		Set<QSort> removeThese = new HashSet<QSort>();
		for (QSort q : list) {
			if (q.getQResults(forced).size() == 0) {
				removeThese.add(q);
			} else {
				for (Double v : q.getQResults(forced)) {
					if (v == null)
						removeThese.add(q);
				}
			}
			if (q.getRankings(forced).size() == 0)
				removeThese.add(q);
			else {
				for (Double v : q.getRankings(forced)) {
					if (v == null)
						removeThese.add(q);
				}
			}
		}

		list.removeAll(removeThese);

		if (list.size() == 0) {
			return null;
		}
		// make the matrix of the qResults
		Matrix qSorts = new Matrix(list.size(), list.get(0).getQResults(forced)
				.size());
		for (int i = 0; i < list.size(); i++) {
			QSort q = list.get(i);
			qSorts.setRowLabel(i + 1, q.getParticipantId() + "");
			for (int j = 0; j < q.getQResults(forced).size(); j++) {
				qSorts.setValue(i + 1, j + 1, q.getQResults(forced).get(j));
				qSorts.setColumnLabel(j + 1, "Q" + (j + 1));
			}
		}

		// make the matrix of rankings
		Matrix rankings = new Matrix(list.size(), list.get(0).getRankings(
				forced).size());
		for (int i = 0; i < list.size(); i++) {
			QSort q = list.get(i);
			rankings.setRowLabel(i + 1, q.getParticipantId() + "");
			for (int j = 0; j < q.getRankings(forced).size(); j++) {
				rankings.setValue(i + 1, j + 1, q.getRankings(forced).get(j));
				rankings.setColumnLabel(j + 1, "R" + (j + 1));
			}
		}

		// perform correlations
		Matrix qSortsCorrelated = qSorts.transpose()
				.getPearsonCorrelationMatrix();
		Matrix rankingsCorrelated = rankings.transpose()
				.getPearsonCorrelationMatrix();

		// compare rankings and qSorts
		Matrix m = new Matrix(1, 4);
		for (int i = 1; i <= qSortsCorrelated.rowCount(); i++) {
			for (int j = i + 1; j <= qSortsCorrelated.columnCount(); j++) {
				boolean includeIt = filter == null
						|| filter.size() == 0
						|| filter.contains(Integer.parseInt(qSortsCorrelated
								.getRowLabel(i)))
						|| filter.contains(Integer.parseInt(qSortsCorrelated
								.getRowLabel(j)));
				if (includeIt) {
					if (i != 1 || j != 2)
						m = m.addRow();
					m.setValue(m.rowCount(), 1, Integer
							.parseInt(qSortsCorrelated.getRowLabel(i)));
					m.setValue(m.rowCount(), 2, Integer
							.parseInt(qSortsCorrelated.getRowLabel(j)));
					m
							.setValue(m.rowCount(), 3, qSortsCorrelated
									.getValue(i, j));
					m.setValue(m.rowCount(), 4, rankingsCorrelated.getValue(i,
							j));
					m.setRowLabel(m.rowCount(), qSortsCorrelated.getRowLabel(i)
							+ ":" + qSortsCorrelated.getRowLabel(j));
				}
			}
		}
		m.setColumnLabel(1, "participant1");
		m.setColumnLabel(2, "participant2");
		m.setColumnLabel(3, "qSort");
		m.setColumnLabel(4, "ranking");

		DataComponents dataComponents = new DataComponents();
		dataComponents.list = list;
		dataComponents.qSorts = qSorts;
		dataComponents.rankings = rankings;
		dataComponents.correlations = m;
		return dataComponents;
	}

	public void writeMatrix(Matrix m, OutputStream os) throws IOException {
		os.write(m.getDelimited(TAB, true).getBytes());
		os.flush();
	}

	public GraphPanel getGraphConnected(List<QSort>[] list, boolean forced,
			boolean labelPoints, int size, Set<Integer> filter) {
		List<Vector> vectors1 = new ArrayList<Vector>();
		List<Vector> vectors2 = new ArrayList<Vector>();
		for (int vi = 0; vi < list.length; vi++) {
			DataComponents d = buildMatrix(list[vi], forced, filter);
			if (d == null)
				return null;
			Matrix m = d.correlations;
			if (m == null)
				return null;

			Vector v1 = m.getColumnVector(3);
			Vector v2 = m.getColumnVector(4);
			for (int i = 1; i <= v1.rowCount(); i++) {
				DecimalFormat df3 = new DecimalFormat("0");
				v1.setRowLabel(i, df3.format(m.getValue(i, 2)));
				v2.setRowLabel(i, df3.format(m.getValue(i, 1)));
			}
			vectors1.add(v1);
			vectors2.add(v2);
		}

		GraphPanel gp = new GraphPanel(vectors1.toArray(new Vector[0]),
				vectors2.toArray(new Vector[0]));
		gp.setDisplayArrowHeads(false);
		gp.setBackground(Color.white);
		gp.setLabelsVisible(labelPoints);
		gp.setSize(size, size);
		gp.setXLabel("Intersubjective Agreement (Pearson)");
		gp.setYLabel("Preferences Agreement (Pearson)");
		return gp;
	}

	public GraphPanel getGraph(List<QSort> list, boolean forced,
			boolean labelPoints, int size, Set<Integer> filter,
			final String bands, boolean includeRegressionLines) {
		DataComponents d = buildMatrix(list, forced, filter);
		if (d == null)
			return null;
		Matrix m = d.correlations;
		if (m == null)
			return null;
		// if (textOutputStream != null)
		// textOutputStream.write(m.getDelimited(TAB, true).getBytes());

		final Vector v1 = m.getColumnVector(3);
		final Vector v2 = m.getColumnVector(4);
		for (int i = 1; i <= v1.rowCount(); i++) {
			DecimalFormat df3 = new DecimalFormat("0");
			v1.setRowLabel(i, df3.format(m.getValue(i, 2)));
			v2.setRowLabel(i, df3.format(m.getValue(i, 1)));
		}

		GraphPanel gp = new GraphPanel(v1, v2);
		gp.setDisplayMeans(true);
		gp.setDisplayArrowHeads(false);
		gp.setBackground(Color.white);
		gp.setLabelsVisible(labelPoints);
		gp.setSize(size, size);
		gp.setXLabel("Intersubjective Agreement (Pearson)");
		gp.setYLabel("Preferences Agreement (Pearson)");
		final SimpleRegression sr = new SimpleRegression();
		double[][] vals = new double[v1.size()][2];
		for (int i = 0; i < vals.length; i++) {
			vals[i][0] = v1.getValue(i + 1);
			vals[i][1] = v2.getValue(i + 1);
		}
		sr.addData(vals);

		if (includeRegressionLines) {
			final Function interval = new RegressionIntervalFunction(v1,
					PREDICTION_INTERVAL_95.equals(bands));

			gp.addFunction(new Function() {

				public double f(double x) {

					return sr.predict(x) + interval.f(x);
				}
			}, Color.lightGray);
			gp.addFunction(new Function() {

				public double f(double x) {
					return sr.predict(x) - interval.f(x);
				}
			}, Color.lightGray);
			gp.addFunction(new Function() {

				public double f(double x) {
					return sr.predict(x);
				}
			}, Color.BLACK);
		}
		DecimalFormat df = new DecimalFormat("0.00");
		gp.addComment(new Vector(new double[] { -0.8, 0.8 }), "r2="
				+ df.format(Math.pow(sr.getR(), 2)));
		return gp;
	}

	public Set<Integer> getParticipantIds(boolean forced, String participantType) {
		Set<Integer> result = new HashSet<Integer>();
		for (QSort q : restrictList(forced, participantType, "all", null)) {
			result.add(q.getParticipantId());
		}
		return result;
	}

	private void graph(List<QSort> list, boolean forced,
			OutputStream imageOutputStream, boolean labelPoints, int size,
			Set<Integer> filter, String bands) throws IOException {

		GraphPanel gp = getGraph(list, forced, labelPoints, size, filter,
				bands, true);
		if (gp != null)
			writeImage(gp, size, imageOutputStream);
	}

	private void graphConnected(List<QSort> list, boolean forced,
			OutputStream imageOutputStream, boolean labelPoints, int size,
			Set<Integer> filter) throws IOException {
		// split the list into separate lists by stage
		Map<String, List<QSort>> map = new LinkedHashMap<String, List<QSort>>();
		for (QSort q : list) {
			if (map.get(q.getStage()) == null)
				map.put(q.getStage(), new ArrayList<QSort>());
			map.get(q.getStage()).add(q);
		}

		GraphPanel gp = getGraphConnected(map.values()
				.toArray(new ArrayList[1]), forced, labelPoints, size, filter);

		if (gp != null) {
			gp.setDisplayMeans(true);
			gp.setDisplayRegression(true);
			if (size <= 1000)
				writeImageConnected(gp, size, imageOutputStream);
			else
				writeImage(gp, size, imageOutputStream);
		}
	}

	private void writeImage(GraphPanel gp, int imageSize, OutputStream imageOs)
			throws IOException {
		gp.setSize(imageSize, imageSize);
		ImageIO.write(gp.getImage(), "jpeg", imageOs);
	}

	private void writeImageConnected(GraphPanel gp, int imageSize,
			OutputStream imageOs) throws IOException {
		gp.setSize(imageSize, imageSize);
		gp.writeAnimatedImage(imageOs);
	}

	public void writeMatrix(boolean forced, String participantType,
			String stage, Set<Integer> exclusions, Set<Integer> filter,
			OutputStream os) throws IOException {
		List<QSort> list = restrictList(forced, participantType, stage,
				exclusions);
		Matrix matrix = buildMatrix(list, forced, filter).correlations;
		if (matrix != null)
			writeMatrix(matrix, os);
	}

	public Matrix getRawData(DataCombination dataCombination,
			Set<Integer> exclusions, Set<Integer> filter, int dataSet) {
		return getRawData(dataCombination.getForced(), dataCombination
				.getParticipantType(), dataCombination.getStage(), exclusions,
				filter, dataSet);
	}

	public Matrix getRawData(boolean forced, String participantType,
			String stage, Set<Integer> exclusions, Set<Integer> filter,
			int dataSet) {
		List<QSort> subList = restrictList(forced, participantType, stage,
				exclusions);

		if (subList.size() == 0) {
			return null;
		}

		int numRows = subList.get(0).getQResults(forced).size();
		if (dataSet == 2)
			numRows = subList.get(0).getRankings(forced).size();
		if (numRows == 0)
			return null;
		Matrix m = new Matrix(numRows, subList.size());
		int col = 1;
		for (QSort q : subList) {
			int row = 1;
			List<Double> items = q.getQResults(forced);
			if (dataSet == 2)
				items = q.getRankings(forced);
			for (double value : items) {
				m.setValue(row, col, value);
				row++;
			}
			m.setColumnLabel(col, participantPrefix + q.getParticipantId());
			col++;
		}
		if (dataSet == 1)
			m.setRowLabelPattern("Stmt<index>");
		else
			m.setRowLabelPattern("Pref<index>");

		// m.writeToFile(new File("/matrix.txt"), false);
		m = m.removeColumnsWithNoStandardDeviation();
		return m;
	}

	public void analyze(boolean forced, String participantType, String stage,
			Set<Integer> exclusions, Set<Integer> filter, int dataSet,
			double threshold, boolean doPca, boolean doCentroid,
			Set<RotationMethod> rotationMethods, SimpleHeirarchicalFormatter f) {

		if (!forced) {
			f.header("Error", true);
			f.blockStart();
			f.item("Factor analysis of unforced not implemented");
			f.blockFinish();
			return;
		}

		Matrix m = getRawData(forced, participantType, stage, exclusions,
				filter, dataSet);
		if (m == null) {
			f.header("Error", true);
			f.blockStart();
			f.item("No data to analyze");
			f.blockFinish();
			return;
		}

		FactorAnalysisResults results;
		try {
			if (doPca) {
				results = m.analyzeFactors(
						FactorExtractionMethod.PRINCIPAL_COMPONENTS_ANALYSIS,
						threshold, rotationMethods);
				results.process(results.getInitial(), f);
			}
			if (doCentroid) {
				results = m.analyzeFactors(
						FactorExtractionMethod.CENTROID_METHOD, threshold,
						rotationMethods);
				results.process(results.getInitial(), f);
			}
		} catch (FactorAnalysisException e) {
			f.header("Error", true);
			f.blockStart();
			f.item(e.getMessage());
			f.blockFinish();
		}
	}

	public Set<Integer> getExcludeParticipants() {
		return excludeParticipants;
	}

	public Map<Integer, String> getStatements() {
		return statements;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}