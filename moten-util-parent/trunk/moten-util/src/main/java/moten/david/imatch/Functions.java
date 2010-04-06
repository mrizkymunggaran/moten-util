package moten.david.imatch;

public interface Functions {
    IdentifierSet conflicting(IdentifierSet x, IdentifierSet y);

    IdentifierSet alpha(Identifier identifier);

    double time(IdentifierSet time);

    double dmax(IdentifierSet x);

    IdentifierSet nms(IdentifierSet x, IdentifierSet y);

    IdentifierSet pm(IdentifierSet x);

    IdentifierType t(Identifier x);

    double d(IdentifierType t);

}
