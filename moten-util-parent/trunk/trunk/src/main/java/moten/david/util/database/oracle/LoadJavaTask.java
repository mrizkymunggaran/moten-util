package moten.david.util.database.oracle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

public class LoadJavaTask extends Task {
	private boolean failOnError = true;
	private boolean showLog = true;
	private boolean showChangesOnly = true;
	private boolean definer = false;
	private String user;
	private boolean thin = true;
	private boolean resolve = true;
	private boolean verbose = false;
	private boolean force = false;
	private boolean oci8 = false;
	private boolean showHelp = false;
	private List<Path> paths = new ArrayList<Path>();
	private String encoding;
	private boolean genmissing = false;
	private String genmissingjar;
	private String grants;
	private boolean nousage = false;
	private boolean help = false;
	private boolean noverify = false;
	private boolean order = false;
	private String resolver;
	private String schema;
	private boolean synonym = false;
	private String tableschema;

	@Override
	public void execute() throws BuildException {
		log("executing");
		super.execute();
		ArrayList<String> args = new ArrayList<String>();
		if (failOnError)
			args.add("-failOnError");
		if (showLog)
			args.add("-showLog");
		if (showChangesOnly)
			args.add("-showChangesOnly");
		if (showHelp)
			args.add("-showHelp");
		if (definer)
			args.add("-definer");
		if (encoding != null)
			args.add("-encoding " + encoding);
		if (force)
			args.add("-force");
		if (genmissing)
			args.add("-genmissing");
		if (genmissingjar != null) {
			args.add("-genmissingjar");
			args.add(genmissingjar);
		}
		if (grants != null) {
			args.add("-grant");
			args.add(grants);
		}
		if (help)
			args.add("-help");
		if (nousage)
			args.add("-nousage");
		if (noverify)
			args.add("-noverify");
		if (oci8)
			args.add("-oci8");
		if (order)
			args.add("-order");
		if (resolve)
			args.add("-resolve");
		if (resolver != null) {
			args.add("-resolver");
			args.add(resolver);
		}
		if (schema != null) {
			args.add("-schema");
			args.add(schema);
		}
		if (synonym)
			args.add("-synonym");
		if (thin)
			args.add("-thin");
		if (tableschema != null) {
			args.add("-tableschema");
			args.add(tableschema);
		}
		if (user != null) {
			args.add("-user");
			args.add(user);
		}
		if (verbose)
			args.add("-verbose");
		for (Iterator<Path> itPaths = paths.iterator(); itPaths.hasNext();) {
			Path path = itPaths.next();
			String[] includedFiles = path.list();
			for (int i = 0; i < includedFiles.length; i++) {
				args.add(includedFiles[i]);
			}
		}
		log(args+"");
		String[] arguments = args.toArray(new String[] {});
		try {
			Deployer.main(arguments);
		} catch (IOException e) {
			throw new BuildException(e);
		}
	}

	public List<Path> getPaths() {
		return paths;
	}

	public void setPaths(List<Path> paths) {
		this.paths = paths;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isGenMissing() {
		return genmissing;
	}

	public void setGenMissing(boolean genMissing) {
		this.genmissing = genMissing;
	}

	public String getGenMissingJar() {
		return genmissingjar;
	}

	public void setGenMissingJar(String genMissingJar) {
		this.genmissingjar = genMissingJar;
	}

	public String getGrant() {
		return grants;
	}

	public void setGrant(String grant) {
		this.grants = grant;
	}

	public boolean isNousage() {
		return nousage;
	}

	public void setNousage(boolean nousage) {
		this.nousage = nousage;
	}

	public boolean isHelp() {
		return help;
	}

	public void setHelp(boolean help) {
		this.help = help;
	}

	public boolean isNoverify() {
		return noverify;
	}

	public void setNoverify(boolean noverify) {
		this.noverify = noverify;
	}

	public boolean isOrder() {
		return order;
	}

	public void setOrder(boolean order) {
		this.order = order;
	}

	public String getResolver() {
		return resolver;
	}

	public void setResolver(String resolver) {
		this.resolver = resolver;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public boolean isSynonym() {
		return synonym;
	}

	public void setSynonym(boolean synonym) {
		this.synonym = synonym;
	}

	public String getTableschema() {
		return tableschema;
	}

	public void setTableschema(String tableschema) {
		this.tableschema = tableschema;
	}

	public LoadJavaTask() {
		// do nothing
	}

	public void addPath(Path path) {
		paths.add(path);
	}

	public boolean isFailOnError() {
		return failOnError;
	}

	public void setFailOnError(boolean failOnError) {
		this.failOnError = failOnError;
	}

	public boolean isShowLog() {
		return showLog;
	}

	public void setShowLog(boolean showLog) {
		this.showLog = showLog;
	}

	public boolean isShowChangesOnly() {
		return showChangesOnly;
	}

	public void setShowChangesOnly(boolean showChangesOnly) {
		this.showChangesOnly = showChangesOnly;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public boolean isThin() {
		return thin;
	}

	public void setThin(boolean thin) {
		this.thin = thin;
	}

	public boolean isResolve() {
		return resolve;
	}

	public void setResolve(boolean resolve) {
		this.resolve = resolve;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public boolean isForce() {
		return force;
	}

	public void setForce(boolean force) {
		this.force = force;
	}

	public boolean isOci8() {
		return oci8;
	}

	public void setOci8(boolean oci8) {
		this.oci8 = oci8;
	}

	public boolean isShowHelp() {
		return showHelp;
	}

	public void setShowHelp(boolean showHelp) {
		this.showHelp = showHelp;
	}

	public boolean isDefiner() {
		return definer;
	}

	public void setDefiner(boolean definer) {
		this.definer = definer;
	}

}
