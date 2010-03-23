package moten.david.util.tv.recorder;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import moten.david.util.tv.schedule.ScheduleItem;
import moten.david.util.tv.servlet.ApplicationInjector;

import com.google.inject.Inject;

public class RecorderLinux implements Recorder {
	private static Logger log = Logger.getLogger(RecorderLinux.class.getName());
	private final AliasProvider aliasProvider;

	@Inject
	public RecorderLinux(AliasProvider aliasProvider) {
		this.aliasProvider = aliasProvider;
	}

	@Override
	public boolean isRecording(ScheduleItem item) {
		log.info("checking if is recording " + item.getName());
		String output = startProcess("src/main/resources/is-recording.sh",
				aliasProvider.getAlias(item.getChannelId()),
				getStartDateString(item));
		boolean recording = output.trim().length() > 0;
		log.info("recording=" + recording + " output length="
				+ output.trim().length() + " output=" + output);
		return recording;
	}

	private String getStartDateString(ScheduleItem item) {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmm");
		return dateFormat.format(item.getStartDate());
	}

	@Override
	public void startRecording(ScheduleItem item) {
		log.info("starting recording for " + item.getName());

		File file = new File("src/main/resources/start-recording.sh");
		// pass in as parameters to the script
		// 1. channel alias
		// 2. tuner no
		// 3. yyyyMMdd_HHmm date
		// 4. HHmm duration
		// 5. title
		// 6. series no
		// 7. episode no
		startProcess(file.getAbsolutePath(), aliasProvider.getAlias(item
				.getChannelId()), "0", getStartDateString(item), "00:00", item
				.getName());
	}

	@Override
	public void stopRecording(ScheduleItem item) {
		stopRecorder();
	}

	@Override
	public void play(String channelId) {
		stopPlayer();
		String alias = aliasProvider.getAlias(channelId);
		startProcess("/usr/bin/mplayer", "-quiet", "dvb://" + alias);
	}

	private void stopRecorder() {
		File file = new File("src/main/resources/stop-recorder.sh");
		startProcess(file.getAbsolutePath());
	}

	private void stopPlayer() {
		File file = new File("src/main/resources/stop-player.sh");
		startProcess(file.getAbsolutePath());
	}

	private void startProcess(Collection<String> commandParts) {
		startProcess(commandParts.toArray(new String[] {}));
	}

	private String startProcess(String... commandParts) {

		List<String> command = new ArrayList<String>();
		for (String commandPart : commandParts)
			command.add(commandPart);
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		try {
			Process process = builder.start();
			ConsoleWriter writer = new ConsoleWriter(process.getInputStream());
			int resultCode = process.waitFor();
			log.info("result code = " + resultCode);
			return writer.getOutput();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

	}

	private static class ConsoleWriter {

		private static final int MAX_BUFFER_SIZE = 100000;
		private StringBuffer output = new StringBuffer();
		private boolean finished = false;

		public ConsoleWriter(final InputStream is) {
			Thread t = new Thread(new Runnable() {

				@Override
				public void run() {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(is));
					String line;
					try {
						while ((line = br.readLine()) != null) {
							log.info(line);
							if (output.length() > 0)
								output.append("\n");
							output.append(line);

							// output guaranteed only to have the last
							// MAX_BUFFER_SIZE bytes
							if (output.length() >= 2 * MAX_BUFFER_SIZE) {
								// reset buffer, too bad if you were interested
								// in the output!
								output.replace(0, MAX_BUFFER_SIZE, "");
								output = new StringBuffer();
							}
						}
					} catch (IOException e) {
						log.log(Level.SEVERE, e.getMessage(), e);
					}
					finished = true;
				}
			});
			t.start();
		}

		public String getOutput() {
			while (!finished)
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// do nothing
				}
			return output.toString();
		}
	}

	public static void main(String[] args) {
		RecorderLinux r = ApplicationInjector.getInjector().getInstance(
				RecorderLinux.class);
		String output = r.startProcess("ls", "-l");
		System.out.println("output=" + output);
	}
}
