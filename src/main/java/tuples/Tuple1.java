// This class is auto-generated! Modify at your own risk.
package tuples;

public final class Tuple1<T1> {
    public final T1 _1;

    public Tuple1(final T1 v1) {
        _1 = v1;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple1 tuple = (Tuple1) o;

        if (_1 != null ? !_1.equals(tuple._1) : tuple._1 != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = _1 != null ? _1.hashCode() : 0;
        return result;
    }

    @Override
    public String toString() {
        return "(" +
            _1 +
        ')';
    }

}
