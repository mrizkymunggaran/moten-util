package au.edu.anu.delibdem.qsort.gui;

import java.awt.GridLayout;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import moten.david.util.event.Event;
import moten.david.util.event.EventManager;
import moten.david.util.math.FactorAnalysisException;
import moten.david.util.math.FactorAnalysisResults;
import moten.david.util.math.FactorExtractionMethod;
import moten.david.util.math.Matrix;
import moten.david.util.math.MatrixProvider;
import moten.david.util.math.Varimax.RotationMethod;
import au.edu.anu.delibdem.qsort.Data;

public class FactorTreePanel extends JPanel {

	private static final long serialVersionUID = -1191102361903304632L;

	private final JTree tree;

	private final List<EventManager> eventManagers = new ArrayList<EventManager>();

	public FactorTreePanel(FactorAnalysisResults results) {
		setLayout(new GridLayout(1, 1));
		DefaultMutableTreeNode top = createNodes(results);
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.SINGLE_TREE_SELECTION);
		add(tree);
	}

	private static String getCapitalized(String s) {
		return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}

	private static DefaultMutableTreeNode createFieldNode(final String name,
			final Object o, String fieldName) throws SecurityException,
			NoSuchFieldException {
		final Field field = o.getClass().getDeclaredField(fieldName);
		String methodName = "get" + getCapitalized(field.getName());
		final Method method;
		try {
			method = o.getClass().getMethod(methodName, new Class[] {});
		} catch (NoSuchMethodException e) {
			throw new Error(e);
		}
		MatrixProvider matrixProvider = new MatrixProvider() {
			@Override
			public Matrix getMatrix() {
				try {
					Object result = method.invoke(o, (Object[]) new Class[] {});
					if (result instanceof Matrix)
						return (Matrix) result;
					else if (result instanceof Double) {
						Matrix m = new Matrix(
								new double[][] { { (Double) result } });
						m.setRowLabel(1, "Item");
						m.setColumnLabel(1, "Value");
						return m;
					} else
						throw new Error("not sure how to implement " + result);
				} catch (Exception e) {
					throw new Error(e);
				}
			}

			@Override
			public String toString() {
				return name;
			}

		};
		return new DefaultMutableTreeNode(matrixProvider);
	}

	public static DefaultMutableTreeNode createNodes(
			FactorAnalysisResults results) {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(
				new ObjectDecorator(results, results.extractionMethod
						.toString()));
		try {
			top.add(createFieldNode("Raw Data", results, "initial"));
			top.add(createFieldNode("Correlations", results, "correlations"));
			top.add(createFieldNode("Eigenvalues", results, "eigenvalues"));
			top.add(createFieldNode("Eigenvectors", results, "eigenvectors"));
			top.add(createFieldNode("Loadings", results, "loadings"));
			top.add(createFieldNode("Percent Variance", results,
					"percentVariance"));
			top.add(createFieldNode("Eigenvalue Threshold", results,
					"eigenvalueThreshold"));
			top.add(createFieldNode("Principal Eigenvalues", results,
					"principalEigenvalues"));
			top.add(createFieldNode("Principal Eigenvectors", results,
					"principalEigenvectors"));
			top.add(createFieldNode("Principal Loadings", results,
					"principalLoadings"));
		} catch (Exception e) {
			throw new Error(e);
		}
		return top;
	}

	public void addEventManager(final EventManager eventManager) {
		eventManagers.add(eventManager);
		tree.addTreeSelectionListener(new TreeSelectionListener() {

			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
						.getLastSelectedPathComponent();

				/* if nothing is selected */
				if (node == null)
					return;

				/* retrieve the node that was selected */
				Object nodeInfo = node.getUserObject();
				if (nodeInfo instanceof MatrixProvider) {
					eventManager.notify(new Event(((MatrixProvider) nodeInfo)
							.getMatrix(), Events.MATRIX));

				}
			}
		});
	}

	public static void main(String[] args) throws FileNotFoundException,
			IOException, FactorAnalysisException {
		Data data = new Data(new FileInputStream("src/New Mexico.txt"));
		Matrix matrix = data.getRawData(data.getParticipantTypes().iterator()
				.next(), data.getStageTypes().iterator().next(), null, null, 1);
		Set<RotationMethod> set = new HashSet<RotationMethod>();
		set.add(RotationMethod.NONE);
		FactorAnalysisResults r = matrix.analyzeFactors(
				FactorExtractionMethod.PRINCIPAL_COMPONENTS_ANALYSIS, set);
		JFrame frame = new QuickFrame(new FactorPanel(r));
		frame.setSize(600, frame.getSize().height);
	}

}
