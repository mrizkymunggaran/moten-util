package moten.david.util.shell;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Shell {

    private static Logger log = Logger.getLogger(Shell.class.getName());

    private Executor executor;

    private synchronized Executor getExecutor() {
        if (executor == null)
            executor = Executors.newSingleThreadExecutor();
        return executor;
    }

    public static boolean isWindows() {
        return File.pathSeparatorChar == ';';
    }

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

    public static interface LogListener {
        void log(String message);

        void finished();
    }

    public int launch(String workingDirectory, File script) {
        return launch(getExecutor(), workingDirectory, script, new DefaultLog());
    }

    public int launch(String workingDirectory, String script) {
        return launch(getExecutor(), workingDirectory,
                createScriptFile(script), new DefaultLog());
    }

    public int launch(Executor executor, String workingDirectory,
            String script, LogListener listener) {
        return launch(executor, workingDirectory, createScriptFile(script),
                listener);
    }

    public int launch(String workingDirectory, String script,
            LogListener listener) {
        return launch(getExecutor(), workingDirectory,
                createScriptFile(script), listener);
    }

    public int launch(Executor executor, String workingDirectory, File script,
            LogListener listener) {

        ProcessBuilder builder = new ProcessBuilder(getSh(), "-xe", script
                .getAbsolutePath());
        builder.directory(new File(workingDirectory));
        builder.redirectErrorStream();
        log.info(builder.command().toString());
        try {
            Process process = builder.start();
            executor.execute(new Log(process.getInputStream(), listener));
            int returnCode = process.waitFor();
            return returnCode;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            log.info("interrupted");
            return 1001001;
        }

    }

    public static class Recorder implements LogListener {
        private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        private final PrintWriter writer;

        public Recorder() {
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    bytes)));
        }

        @Override
        public void finished() {
            writer.close();
        }

        @Override
        public void log(String message) {
            writer.println(message);
        }

        @Override
        public String toString() {
            return bytes.toString();
        }

    }

    public static class DefaultLog implements LogListener {

        @Override
        public void finished() {
            log.info("finished stream logger");
        }

        @Override
        public void log(String message) {
            log.info(message);
        }

    }

    private static class Log implements Runnable {

        private final InputStream is;
        private final LogListener listener;

        public Log(InputStream is, LogListener listener) {
            this.is = is;
            this.listener = listener;
        }

        @Override
        public void run() {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    listener.log(line);
                }
            } catch (IOException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
            } finally {
                listener.finished();
            }
        }
    }

    private String enhanceScript(String script) {
        return addCrForNonASCII(fixCrLf(script));
    }

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

    public int launch(Executor executor, String workingDirectory, String script) {
        return launch(executor, workingDirectory, createScriptFile(script),
                new DefaultLog());
    }

}
