package org.moten.david.util.media;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class SystemPlayer implements Player {

    private final String commandTemplate;
    private Process process;
    private final List<Listener> listeners = new ArrayList<Listener>();

    public SystemPlayer(String commandTemplate) {
        this.commandTemplate = commandTemplate;
    }

    public static interface Listener {
        void finished();
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public void play(String extension, InputStream is) {
        synchronized (this) {
            if (process != null)
                throw new RuntimeException("player not stopped yet!");
            try {
                File temp = File.createTempFile("media", "." + extension);
                FileOutputStream fos = new FileOutputStream(temp);
                IOUtils.copy(is, fos);
                fos.close();
                ProcessBuilder builder = new ProcessBuilder("mplayer", temp
                        .getAbsolutePath());
                builder.redirectErrorStream(true);
                process = builder.start();
                new Thread(createOutputListener(process)).start();
                new Thread(createProcessListener(process)).start();
                is.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public Runnable createOutputListener(final Process process) {
        return new Runnable() {

            @Override
            public void run() {
                BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                try {
                    while ((line=br.readLine())!=null)
                        System.out.println(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }};
    }

    public Runnable createProcessListener(final Process process) {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    // ignore
                }
                fireFinished();
            }
        };
    }

    protected void fireFinished() {
        for (Listener listener : listeners)
            listener.finished();
    }

    public void stop() {
        synchronized (this) {
            if (process == null)
                return;
            process.destroy();
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                // ignore
            }
            process = null;
        }
    }

}
