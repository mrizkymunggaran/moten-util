package moten.david.ete.memory;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import moten.david.ete.Entity;
import moten.david.ete.Fix;
import moten.david.ete.Identifier;

public class MyEntity implements Entity {

    private static final int MAX_FIXES = 5;
    private final TreeSet<Fix> fixes = new TreeSet<Fix>();
    private final SortedSet<Identifier> identifiers;

    public MyEntity(SortedSet<Identifier> identifiers) {
        this.identifiers = identifiers;
    }

    @Override
    public void addFix(Fix fix) {
        synchronized (fixes) {
            fixes.add(fix);

            // trim fixes
            int numberToDelete = fixes.size() - MAX_FIXES;
            if (numberToDelete > 0) {
                Iterator<Fix> it = fixes.iterator();
                for (int i = 0; i < numberToDelete; i++) {
                    fixes.remove(it.next());
                }
            }
        }
    }

    @Override
    public SortedSet<Identifier> getIdentifiers() {
        return identifiers;
    }

    @Override
    public Fix getLatestFix() {
        synchronized (fixes) {
            return fixes.last();
        }
    }

    @Override
    public Fix getLatestFixBefore(Calendar calendar) {
        synchronized (fixes) {
            Fix fix = new MyFix(
                    new MyPosition(BigDecimal.ZERO, BigDecimal.ZERO), calendar);
            return fixes.floor(fix);
        }
    }

    @Override
    public BigDecimal getMaximumSpeedMetresPerSecond() {
        return BigDecimal.valueOf(20);
    }

    @Override
    public BigDecimal getMinimumTimeForSpeedCalculationSeconds() {
        return BigDecimal.valueOf(60);
    }

    @Override
    public void moveFixes(Entity entity) {
        synchronized (fixes) {
            ((MyEntity) entity).fixes.addAll(fixes);
            fixes.clear();
        }
    }

    @Override
    public boolean hasFixAlready(Fix fix) {
        return fixes.contains(fix);
    }

}
