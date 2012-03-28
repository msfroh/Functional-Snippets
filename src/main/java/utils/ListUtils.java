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
        public List evaluate(Function2<List, Function1, List> self, Function1 func, List list) {
            List outputList = new ArrayList(list.size());
            for (Object t : list) {
                outputList.add(func.evaluate(t));
            }
            return outputList;
        }
    };

    public static <R, T1> Function2<List<R>, Function1<R, T1>, List<? extends T1>> hoMap(R[] returnClass,
                                                                             T1[] inputClass) {
        return rawMap;
    }

    public static <R, T1> Function1<List<R>, List<? extends T1>> map(final Function1<R, T1> function1) {
        Function2<List<R>, Function1<R, T1>, List<? extends T1>> mapFunc = rawMap;
        return mapFunc.apply(function1);
    }

    private static final Function3 rawFoldLeft = new Function3<Object, Function2, Object, List>() {

        @Override
        public Object evaluate(final Function3<Object, Function2, Object, List> self,
                               final Function2 func, final Object seed, final List list) {
            Object accumulated = seed;
            for (Object t : list) {
                accumulated = func.evaluate(accumulated, t);
            }
            return accumulated;
        }
    };

    public static <R, T1> Function3<R, Function2<R, R, T1>, R, List<? extends T1>> hoFoldLeft(Class<R> returnClass,
                                                                                      Class<T1> inputClass) {
        return rawFoldLeft;
    }

    public static <R, T1> Function2<R, R, List<? extends T1>> foldLeft(Function2<R, R, T1> function2) {
        Function3<R, Function2<R, R, T1>, R, List<? extends T1>> foldLeft = rawFoldLeft;
        return foldLeft.apply(function2);
    }

    private static final Function3 rawFoldRight = new Function3<Object, Function2, Object, List>() {

        @Override
        public Object evaluate(final Function3<Object, Function2, Object, List> self,
                final Function2 func, final Object seed, final List list) {
            LinkedList reversedList = new LinkedList();
            for (Object t : list) {
                reversedList.addFirst(t);
            }
            return rawFoldLeft.evaluate(func.flip(), seed, reversedList);
        }
    };

    public static <R, T1> Function3<R, Function2<R, T1, R>, R, List<T1>> hoFoldRight(Class<R> returnClass,
                                                                                       Class<T1> inputClass) {
        return rawFoldRight;
    }

    public static <R, T1> Function2<R, R, List<? extends T1>> foldRight(Function2<R, T1, R> function2) {
        final Function3<R, Function2<R, T1, R>, R, List<? extends T1>> foldRight = rawFoldRight;
        return foldRight.apply(function2);
    } 
}
