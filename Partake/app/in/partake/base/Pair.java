package in.partake.base;

public class Pair<F, S> {
    private F first;
    private S second;
    
    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }
    
    public F getFirst() {
        return first;
    }
    
    public S getSecond() {
        return second;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair)) { return false; }
        @SuppressWarnings("unchecked")
        Pair<F, S> pair = (Pair<F, S>) obj;
        return first.equals(pair.getFirst()) && second.equals(pair.getSecond());
    }
    
    @Override
    public int hashCode() {
        return first.hashCode() * 37 + second.hashCode();
    }
}
