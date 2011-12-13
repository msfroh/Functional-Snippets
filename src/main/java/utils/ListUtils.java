package utils;

import functions.Function1;
import functions.Function2;
import functions.Function3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListUtils {

    private static final Function2 rawMap = new Function2<List, Function1, List>() {
        @Override
        public List apply(Function1 func, List list) {
            List outputList = new ArrayList(list.size());
            for (Object t : list) {
                outputList.add(func.apply(t));
            }
            return outputList;
        }
    };

    public static <R, T1> Function2<List<R>, Function1<R, T1>, List<T1>> map(Class<R> returnClass,
                                                                             Class<T1> inputClass) {
        return rawMap;
    }

    private static final Function3 rawFoldLeft = new Function3<Object, Function2, Object, List>() {

        @Override
        public Object apply(final Function2 func, final Object seed, final List list) {
            Object accumulated = seed;
            for (Object t : list) {
                accumulated = func.apply(accumulated, t);
            }
            return accumulated;
        }
    };

    public static <R, T1, T2> Function3<R, Function2<R, R, T1>, R, List<T1>> foldLeft(Class<R> returnClass,
                                                                                      Class<T1> inputClass) {
        return rawFoldLeft;
    }

    private static final Function3 rawFoldRight = new Function3<Object, Function2, Object, List>() {

        @Override
        public Object apply(final Function2 func, final Object seed, final List list) {
            LinkedList reversedList = new LinkedList();
            for (Object t : list) {
                reversedList.addFirst(t);
            }
            return rawFoldLeft.apply(func.flip(), seed, reversedList);
        }
    };

    public static <R, T1, T2> Function3<R, Function2<R, R, T1>, R, List<T1>> foldRight(Class<R> returnClass,
                                                                                       Class<T1> inputClass) {
        return rawFoldRight;
    }
}
