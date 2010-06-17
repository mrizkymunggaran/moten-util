package moten.david.util.database.oracle;

/*
 * Created on 14/12/2005
 *
 * Author: DMoten
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.security.Permission;

import oracle.aurora.server.tools.loadjava.LoadJavaMain;

public class Deployer {

	/**
	 * Runs oracle.aurora.server.tools.loadjava.LoadJavaMain with args and
	 * appends output to file deployer.log. If the option -failOnError is
	 * included in the arguments then an Error will be thrown if the word
	 * 'errors' exists in the output from LoadJavaMain. If the option -showLog
	 * is included then -verbose info will be output (i.e. info for each class
	 * being loaded). The log is always shown if an error occurrs or even if a
	 * System.exit is called (the SecurityManager is overriden).
	 * 
	 * Note that the LoadJavaMain method hangs if the login is incorrect. Watch
	 * out for that one!
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		if (args != null && args.length > 0) {
			System.out.println("Deploying " + args[args.length - 1]);
		}
		final PrintStream stdOut = System.out;
		final PrintStream stdErr = System.err;
		boolean errorOccurred = false;
		MainParameters params = new MainParameters(args);
		boolean failOnError = params.optionExists("-failOnError");
		final boolean showLog = params.optionExists("-showLog");
		final boolean showChangesOnly = params.optionExists("-showChangesOnly");
		boolean showHelp = params.optionExists("-showHelp");
		boolean logToFile = params.optionExists("-logToFile");
		args = params.removeOption("-failOnError");
		args = params.removeOption("-showLog");
		args = params.removeOption("-showChangesOnly");
		args = params.removeOption("-showHelp");
		args = params.removeOption("-logToFile");
		final ByteArrayOutputStream logBytes = new ByteArrayOutputStream() {
			@Override
			public synchronized void write(int b) {
				super.write(b);
				if (showLog)
					stdOut.append((char) b);
			}
		};
		final PrintStream myOut = new PrintStream(logBytes);
		System.setOut(myOut);
		System.setErr(myOut);

		final SecurityManager sm = System.getSecurityManager();

		System.setSecurityManager(new SecurityManager() {
			@Override
			public void checkAccept(String host, int port) {
				// ok
			}

			@Override
			public void checkAccess(Thread t) {
				// ok
			}

			@Override
			public void checkAccess(ThreadGroup g) {
				// ok
			}

			@Override
			public void checkAwtEventQueueAccess() {
				// ok
			}

			@Override
			public void checkConnect(String host, int port, Object context) { // ok
			}

			@Override
			public void checkConnect(String host, int port) { // ok
			}

			@Override
			public void checkCreateClassLoader() { // ok
			}

			@Override
			public void checkDelete(String file) { // ok
			}

			@Override
			public void checkExec(String cmd) { // ok
			}

			@Override
			public void checkLink(String lib) { // ok
			}

			@Override
			public void checkListen(int port) { // ok
			}

			@Override
			public void checkMemberAccess(Class arg0, int arg1) { // ok
			}

			@Override
			public void checkMulticast(InetAddress maddr) { // ok
			}

			@Override
			public void checkPackageAccess(String pkg) { // ok
			}

			@Override
			public void checkPackageDefinition(String pkg) { // ok
			}

			@Override
			public void checkPermission(Permission perm, Object context) { // ok
			}

			@Override
			public void checkPrintJobAccess() { // ok
			}

			@Override
			public void checkPropertyAccess(String key) { // ok
			}

			@Override
			public void checkRead(FileDescriptor fd) { // ok
			}

			@Override
			public void checkRead(String file, Object context) { // ok
			}

			@Override
			public void checkRead(String file) { // ok
			}

			@Override
			public void checkSecurityAccess(String target) {// ok
			}

			@Override
			public void checkSetFactory() {// ok
			}

			@Override
			public void checkSystemClipboardAccess() {// ok
			}

			@Override
			public boolean checkTopLevelWindow(Object window) {// ok
				return sm.checkTopLevelWindow(window);
			}

			@Override
			public void checkWrite(FileDescriptor fd) {// ok
			}

			@Override
			public void checkWrite(String file) {// ok
			}

			@Override
			public void checkPermission(Permission perm) {// ok
				// sm.checkPermission(perm);
			}

			@Override
			public void checkExit(int status) {
				if (status != 0) {
					myOut.close();
					stdOut.println(logBytes.toString());
				}
				sm.checkExit(status);
			}

			@Override
			public void checkPropertiesAccess() {
				super.checkPropertiesAccess();
			}
		});

		if (showHelp)
			LoadJavaMain.main(new String[] { "-help" });

		try {
			StringBuffer s = new StringBuffer();
			for (String element : args) {
				s.append(" '" + element + "'");
			}
			myOut.println("running LoadJavaMain class with args: "
					+ s.toString());
			LoadJavaMain.main(args);
		} catch (Error e) {
			e.printStackTrace();
			errorOccurred = true;
		}
		myOut.close();
		errorOccurred = errorOccurred
				|| logBytes.toString().indexOf("errors :") > 0;
		FileOutputStream fos = null;
		if (logToFile)
			fos = new FileOutputStream(new File("deployJava.log"));
		String log;
		if (showChangesOnly) {
			StringBuffer b = new StringBuffer(logBytes.toString());
			while (b.toString().indexOf("identical:") >= 0) {
				int startIndex = b.toString().indexOf("identical:");
				int endIndex = b.toString().indexOf("\n", startIndex);
				b.delete(startIndex, endIndex);
			}
			while (b.toString().indexOf("skipping :") >= 0) {
				int startIndex = b.toString().indexOf("skipping :");
				int endIndex = b.toString().indexOf("\n", startIndex);
				b.delete(startIndex, endIndex);
			}
			log = b.toString();
		} else
			log = logBytes.toString();

		if (logToFile) {
			fos.write(log.getBytes());
			if (errorOccurred) {
				String msg = "\nErrors Occurred!";
				fos.write(msg.getBytes());
				stdOut.write(msg.getBytes());
			}
			fos.close();
		}
		System.setOut(stdOut);
		System.setErr(stdErr);
		if (failOnError) {
			if (errorOccurred) {
				if (!showLog)
					System.out.println(log);
				throw new DeployerError(
						"deployment failed due to error appearing in ");
			}
		}
	}
}

class DeployerError extends Error {

	private static final long serialVersionUID = 1809358860733551336L;

	public DeployerError(String msg) {
		super(msg);
	}
}
