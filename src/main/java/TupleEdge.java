import org.apache.commons.lang3.tuple.MutablePair;

public class TupleEdge<L, R> extends MutablePair<L, R> {


    public TupleEdge(L l, R r) {
        setLeft(l);
        setRight(r);

    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getLeft().hashCode();
        result = 31 * result + getRight().hashCode();
        return result;
    }


}
