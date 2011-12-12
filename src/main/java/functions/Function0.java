package functions;

public abstract class Function0<R> {
    /**
     * @return a value
     */
    public abstract R apply();

    public static <R> Function0<R> value(final R val) {
        return new Function0<R>() {
            @Override
            public R apply() {
                return val;
            }
        };
    }

    public static <R> Function0<R> lazy(final Function0<R> init) {
        return new Function0<R>() {
            private R value;
            @Override
            public synchronized R apply() {
                if (value == null) {
                    value = init.apply();
                }
                return value;
            }
        };
    }
}
