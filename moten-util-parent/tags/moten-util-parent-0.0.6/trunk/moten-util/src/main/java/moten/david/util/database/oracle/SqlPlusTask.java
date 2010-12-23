package moten.david.util.database.oracle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class SqlPlusTask extends Task {

	private String exe = "sqlplus";
	private String version;
	private boolean logonOnce = false;
	private String markup;
	private Integer restrictedMode;
	private boolean silent;
	private boolean displayVersion;
	private boolean help;
	private String logon;
	private boolean sysdba = false;
	private boolean sysoper = false;
	private boolean noLogon = false;
	private boolean failOnError = false;
	private boolean abortScriptOnError = false;
	private Sql sql;
	private String file;
	private String url;
	private List<Parameter> parameters;

	private void check() throws BuildException {
		if (sql != null && (file != null || url != null))
			throw new BuildException("cannot set both sql and (file|url)");
		if (silent && failOnError)
			throw new BuildException(
					"cannot be silent and failOnError because the script output is used to detect an error");
		if (sql == null && file == null)
			throw new BuildException("must set sql or file");
		if (noLogon && logon != null)
			throw new BuildException("cannot specify logon if nologon set");
	}

	@Override
	public void execute() throws BuildException {
		try {
			check();

			List<String> args = new ArrayList<String>();
			if (version != null) {
				args.add("-C");
				args.add("version");
			}
			if (logonOnce)
				args.add("-L");
			if (markup != null) {
				args.add("-M");
				args.add(markup);
			}
			if (restrictedMode != null) {
				args.add("-R");
				args.add("" + restrictedMode);
			}
			if (silent)
				args.add("-S");
			if (displayVersion)
				args.add("-V");
			if (help)
				args.add("-H");
			if (noLogon)
				logon = "/NOLOG";
			if (sysdba)
				logon = logon + " as sysdba";
			if (sysoper)
				logon = logon + " as sysoper";
			if (logon != null)
				args.add(logon);
			if (sql != null) {
				File f = File.createTempFile("sqlplustask-", ".sql");
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(sql.getSql().getBytes());
				fos.close();
				file = f.getPath();
			}
			if (file != null)
				args.add("@" + file);
			if (url != null)
				args.add("@" + url);
			if (parameters != null)
				for (Parameter p : parameters)
					args.add(p.getValue());
			try {
				args.add(0, exe);
				Process process = new ProcessBuilder(args).start();
				InputStream is = process.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line;
				log("Output of running " + args + " is:");
				boolean errorOccurred = false;
				List<String> errors = new ArrayList<String>();
				while ((line = br.readLine()) != null) {
					log(line);
					if (line.startsWith("ORA-") || line.startsWith("PLS-")
							|| line.startsWith("SP2-")
							|| line.startsWith("Errors for ")) {
						errorOccurred = true;
						errors.add(line);
						boolean tnsCouldNotResolveServiceName = line
								.startsWith("ORA-12154:");
						if (abortScriptOnError || tnsCouldNotResolveServiceName)
							process.destroy();
					}
				}
				if (errorOccurred) {
					log("Errors occurred:\n");
					for (String s : errors) {
						log(s);
					}
					if (failOnError)
						throw new BuildException(
								"Errors in the script occurred");
				}
			} catch (IOException e) {
				throw new BuildException(e);
			}
		} catch (BuildException e) {
			log(e, 1);
			throw e;
		} catch (Exception e) {
			log(e, 1);
			throw new BuildException(e);
		}
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isLogonOnce() {
		return logonOnce;
	}

	public void setLogonOnce(boolean logonOnce) {
		this.logonOnce = logonOnce;
	}

	public String getMarkup() {
		return markup;
	}

	public void setMarkup(String markup) {
		this.markup = markup;
	}

	public Integer getRestrictedMode() {
		return restrictedMode;
	}

	public void setRestrictedMode(Integer restrictedMode) {
		this.restrictedMode = restrictedMode;
	}

	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}

	public boolean isDisplayVersion() {
		return displayVersion;
	}

	public void setDisplayVersion(boolean displayVersion) {
		this.displayVersion = displayVersion;
	}

	public boolean isHelp() {
		return help;
	}

	public void setHelp(boolean help) {
		this.help = help;
	}

	public String getLogon() {
		return logon;
	}

	public void setLogon(String logon) {
		this.logon = logon;
	}

	public boolean isSysdba() {
		return sysdba;
	}

	public void setSysdba(boolean sysdba) {
		this.sysdba = sysdba;
	}

	public boolean isSysoper() {
		return sysoper;
	}

	public void setSysoper(boolean sysoper) {
		this.sysoper = sysoper;
	}

	public boolean isNoLogon() {
		return noLogon;
	}

	public void setNoLogon(boolean noLogon) {
		this.noLogon = noLogon;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void addParameter(Parameter parameter) {
		parameters.add(parameter);
	}

	public String getExe() {
		return exe;
	}

	public void setExe(String exe) {
		this.exe = exe;
	}

	public boolean isFailOnError() {
		return failOnError;
	}

	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	public boolean isAbortScriptOnError() {
		return abortScriptOnError;
	}

	public void setAbortScriptOnError(boolean abortScriptOnError) {
		this.abortScriptOnError = abortScriptOnError;
	}

	public void addSql(Sql sql) {
		this.sql = sql;
	}

	public static class Sql {
		private String sql;

		public void addText(String text) {
			sql = text;
		}

		public String getSql() {
			return sql;
		}

		public void setSql(String sql) {
			this.sql = sql;
		}

	}

	public static class Parameter {

		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

	}

}
