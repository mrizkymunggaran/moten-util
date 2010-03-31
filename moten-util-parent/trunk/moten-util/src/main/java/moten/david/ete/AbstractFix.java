package moten.david.ete;

public abstract class AbstractFix implements Fix {

    @Override
    public int compareTo(Fix o) {
        return this.getTime().compareTo(o.getTime());
    }

}
