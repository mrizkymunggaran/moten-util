package moten.david.util.monitoring.gwt.client.check;

public class AppCheckResult {

    private String level;
    private String exception;
    private boolean inherited;

    public boolean isInherited() {
        return inherited;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    public String getLevel() {
        return level;
    }

    public String getException() {
        return exception;
    }

    @Override
    public String toString() {
        return level.toString();
    }

}
