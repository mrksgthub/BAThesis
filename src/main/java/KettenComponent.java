public class KettenComponent<V> {

    V start;
    V end;

    public KettenComponent(V start, V end) {
        this.start = start;
        this.end = end;
    }

    public V getStart() {
        return start;
    }

    public void setStart(V start) {
        this.start = start;
    }

    public V getEnd() {
        return end;
    }

    public void setEnd(V end) {
        this.end = end;
    }
}
