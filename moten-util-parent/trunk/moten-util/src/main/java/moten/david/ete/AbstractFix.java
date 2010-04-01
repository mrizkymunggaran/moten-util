package moten.david.ete;

/**
 * Abstract implementation of Fix that implements compareTo based on the fix
 * time only (ordering is ascending time).
 * 
 * @author dxm
 */
public abstract class AbstractFix implements Fix {

    @Override
    public int compareTo(Fix o) {
        return this.getTime().compareTo(o.getTime());
    }

}
