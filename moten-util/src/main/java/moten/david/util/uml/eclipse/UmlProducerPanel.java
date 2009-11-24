package moten.david.util.uml.eclipse;

import java.awt.GridLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.URL;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;

public class UmlProducerPanel extends JPanel {

	private static final long serialVersionUID = 3421000726452821070L;
	private final DefaultListModel listModel;

	public UmlProducerPanel() {
		setLayout(new GridLayout(1, 1));
		listModel = new DefaultListModel();
		JList list = new JList(listModel);
		add(list);
		createDropTarget(list);
	}

	private void createDropTarget(final JList list) {
		DropTarget dropTarget = new DropTarget(list, new DropTargetListener() {

			@Override
			public void dragEnter(DropTargetDragEvent dtde) {
				System.out.println("drag enter");
			}

			@Override
			public void dragExit(DropTargetEvent dte) {

			}

			@Override
			public void dragOver(DropTargetDragEvent dtde) {
			}

			@Override
			public void drop(DropTargetDropEvent dtde) {
				System.out.println("drop");
				dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				Transferable trans = dtde.getTransferable();
				DataFlavor[] flavors = trans.getTransferDataFlavors();
				for (int i = 0; i < flavors.length; i++) {
					DataFlavor flavor = flavors[i];
					if (flavor.getRepresentationClass().equals(String.class))
						try {
							String url = trans.getTransferData(flavor)
									.toString();
							System.out.println(url);
							if (url.startsWith("file://"))
								listModel.addElement(getClassName(url));

						} catch (UnsupportedFlavorException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			}

			private String getClassName(String url) {
				try {
					URL u = new URL(url);
					InputStreamReader reader = new InputStreamReader(u
							.openStream());
					// Create the tokenizer to read from a file
					StreamTokenizer st = new StreamTokenizer(reader);

					// Prepare the tokenizer for Java-style tokenizing rules
					st.parseNumbers();
					st.wordChars('_', '_');
					st.eolIsSignificant(true);

					// If whitespace is not to be discarded, make this call
					st.ordinaryChars(0, ' ');

					// These calls caused comments to be discarded
					st.slashSlashComments(true);
					st.slashStarComments(true);

					// Parse the file
					String packageName = null;
					String className = null;
					String previousWord = null;

					int token = st.nextToken();
					while (token != StreamTokenizer.TT_EOF && className == null) {
						switch (token) {
						case StreamTokenizer.TT_NUMBER:
							// A number was found; the value is in nval
							double num = st.nval;
							break;
						case StreamTokenizer.TT_WORD:
							// A word was found; the value is in sval
							String word = st.sval;
							System.out.println("word=" + word);
							if ("package".equals(previousWord)
									&& packageName == null)
								packageName = word;
							if (("class".equals(previousWord) || "interface"
									.equals(previousWord))
									&& className == null)
								className = word;
							previousWord = word;
							break;
						case '"':
							// A double-quoted string was found; sval
							// contains the contents
							String dquoteVal = st.sval;
							break;
						case '\'':
							// A single-quoted string was found; sval
							// contains the contents
							String squoteVal = st.sval;
							break;
						case StreamTokenizer.TT_EOL:
							// End of line character found
							break;
						case StreamTokenizer.TT_EOF:
							// End of file has been reached
							break;
						default:
							// A regular character was found; the value is
							// the token itself
							char ch = (char) st.ttype;
							break;
						}
						token = st.nextToken();
					}
					reader.close();
					return packageName + "." + className;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void dropActionChanged(DropTargetDragEvent dtde) {

			}
		});
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("UML Producer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new UmlProducerPanel());
		frame.setSize(500, 500);
		frame.setVisible(true);
	}
}
