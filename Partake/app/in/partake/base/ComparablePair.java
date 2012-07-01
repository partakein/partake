package in.partake.base;

public class ComparablePair<F extends Comparable<F>, S extends Comparable<S>>
        extends Pair<F, S>
        implements Comparable<ComparablePair<F, S>> {
    
    public ComparablePair(F first, S second) {
        super(first, second);
    }
    
    @Override
    public int compareTo(ComparablePair<F, S> rhs) {
        ComparablePair<F, S> lhs = this;
        int x = lhs.getFirst().compareTo(rhs.getFirst());
        if (x != 0) { return x; }
        return lhs.getSecond().compareTo(rhs.getSecond());
    }
}
