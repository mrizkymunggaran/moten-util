package moten.david.util.monitoring;

import java.util.List;

import moten.david.util.monitoring.lookup.DefaultLevel;

import com.google.inject.Inject;

public class DefaultChecker extends Checker {

    @Inject
    public DefaultChecker(List<Check> checks) {
        super(DefaultLevel.OK, DefaultLevel.UNKNOWN, DefaultLevel.EXCEPTION,
                checks);
    }

}
