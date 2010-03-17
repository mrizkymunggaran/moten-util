package moten.david.util.shell;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Runs scripts on the operating system.
 * 
 * @author dxm
 * 
 */
public class Shell {

    /**
     * The wait time interval for testing that streams have finished and been
     * closed.
     */
    private static final int CLOSE_STREAMS_WAIT_INTERVAL_MS = 50;

    /**
     * Logger
     */
    private static Logger log = Logger.getLogger(Shell.class.getName());

    /**
     * singleton instance of Executor (single threaded)
     */
    private ExecutorService executor;

    /**
     * Get the singleton instance of the executor
     * 
     * @return
     */
    private synchronized Executor getExecutor() {
        if (executor == null)
            executor = Executors.newSingleThreadExecutor();
        return executor;
    }

    /**
     * If using the default ExecutorService this call will shutdow the
     * ExecutorService.
     */
    public void shutdown() {
        if (executor != null)
            executor.shutdownNow();
    }

    /**
     * Is the underlying operating system Microsoft Windows
     * 
     * @return
     */
    public static boolean isWindows() {
        return File.pathSeparatorChar == ';';
    }

    /**
     * Get the shell command for the operating system.
     * 
     * @return
     */
    private static String getSh() {
        if (isWindows())
            return "run";
        else
            return "/bin/sh";
    }

    /**
     * Fix CR/LF and always make it Unix style.
     */
    private static String fixCrLf(String s) {
        // eliminate CR
        int idx;
        while ((idx = s.indexOf("\r\n")) != -1)
            s = s.substring(0, idx) + s.substring(idx + 1);
        return s;
    }

    /**
     * Older versions of bash have a bug where non-ASCII on the first line makes
     * the shell think the file is a binary file and not a script. Adding a
     * leading line feed works around this problem.
     */
    private static String addCrForNonASCII(String s) {
        if (!s.startsWith("#!")) {
            if (s.indexOf('\n') != 0) {
                return "\n" + s;
            }
        }
        return s;
    }

    /**
     * Launch a script and wait for it to finish.
     * 
     * @param workingDirectory
     * @param script
     * @return the returnCode of the process. A return code !=0 means an error
     *         occurred.
     */
    public int launch(String workingDirectory, File script) {
        return launch(getExecutor(), workingDirectory, script,
                new LineListenerLogging());
    }

    /**
     * Launch a script and wait for it to finish.
     * 
     * @param workingDirectory
     * @param script
     * @return the returnCode of the process. A return code !=0 means an error
     *         occurred.
     */
    public int launch(String workingDirectory, String script) {
        return launch(getExecutor(), workingDirectory,
                createScriptFile(script), new LineListenerLogging());
    }

    /**
     * Launch a script and wait for it to finish.
     * 
     * @param executor
     * @param workingDirectory
     * @param script
     * @param listener
     * @return the returnCode of the process. A return code !=0 means an error
     *         occurred.
     */
    public int launch(Executor executor, String workingDirectory,
            String script, LineListener listener) {
        return launch(executor, workingDirectory, createScriptFile(script),
                listener);
    }

    /**
     * Launch a script and wait for it to finish.
     * 
     * @param workingDirectory
     * @param script
     * @param listener
     * @return the returnCode of the process. A return code !=0 means an error
     *         occurred.
     */
    public int launch(String workingDirectory, String script,
            LineListener listener) {
        return launch(getExecutor(), workingDirectory,
                createScriptFile(script), listener);
    }

    /**
     * Launch a script and wait for it to finish.
     * 
     * @param executor
     * @param workingDirectory
     * @param script
     * @return
     */
    public int launch(Executor executor, String workingDirectory, String script) {
        return launch(executor, workingDirectory, createScriptFile(script),
                new LineListenerLogging());
    }

    /**
     * Launch a script and wait for it to finish.
     * 
     * @param executor
     * @param workingDirectory
     * @param script
     * @param listener
     * @return the returnCode of the process. A return code !=0 means an error
     *         occurred.
     */
    public int launch(Executor executor, String workingDirectory, File script,
            LineListener listener) {

        ProcessBuilder builder = new ProcessBuilder(getSh(), "-xe", script
                .getAbsolutePath());
        builder.directory(new File(workingDirectory));
        builder.redirectErrorStream();
        log.info(builder.command().toString());
        try {
            Process process = builder.start();
            InputStreamReporter logger = new InputStreamReporter(process.getInputStream(),
                    listener);
            executor.execute(logger);
            int returnCode = process.waitFor();
            // now wait for the streams to finish outputting
            while (!logger.isFinished())
                Thread.sleep(CLOSE_STREAMS_WAIT_INTERVAL_MS);
            if (returnCode!=0)
            	log.info("The script had an error");
            log.info("return code="+ returnCode);
            return returnCode;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            log.info("interrupted");
            return 1001001;
        }
    }

    /**
     * Fix a script so that it will be more likely to run.
     * 
     * @param script
     * @return
     */
    private String enhanceScript(String script) {
        return addCrForNonASCII(fixCrLf(script));
    }

    /**
     * Create a temporary file containing the enhanced script.
     * 
     * @param script
     * @return
     */
    private File createScriptFile(String script) {

        try {
            File file = File.createTempFile("script", ".sh");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(enhanceScript(script).getBytes());
            fos.close();
            return file;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
