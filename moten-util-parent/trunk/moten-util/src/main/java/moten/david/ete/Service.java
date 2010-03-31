package moten.david.ete;

import java.util.Calendar;

/**
 * Concentrates on client demands for data.
 * 
 * @author dxm
 */
public interface Service {

    public Fix[] getCraftFixes(Calendar calendar);

}
