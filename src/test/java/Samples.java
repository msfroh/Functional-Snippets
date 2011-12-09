import functions.Function0;
import functions.Function1;
import functions.Function2;
import sun.org.mozilla.javascript.internal.Function;
import tuples.Tuple2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Samples {
    public static <R, T1> List<R> transformList(final List<T1> inputList, final Function1<R, T1> f) {
        List<R> outputList = new ArrayList<R>();
        for (T1 t : inputList) {
            outputList.add(f.apply(t));
        }
        return outputList;
    }

private static final Function2 rawTransformList = new Function2<List, List, Function1>() {
        @Override
        public List apply(List i1, Function1 i2) {
            List outputList = new ArrayList();
            for (Object t : i1) {
                outputList.add(i2.apply(t));
            }
            return outputList;
        }
    };
private static <R, T1> Function2<List<R>, List<T1>, Function1<R, T1>> buildTransformList(Class<R> returnClass,
                                                                                        Class<T1> inputClass) {
    return rawTransformList;
}


    public static void main(String[] args) {
        // First example using a unary function.
        final Function1<Integer, Integer> timesTwo = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer i1) {
                return i1 * 2;
            }
        };
        System.out.println("timesTwo.apply(5) = " + timesTwo.apply(5));
        System.out.println("transformList([1,2,3,4,5], timesTwo) = "
                + transformList(Arrays.asList(1, 2, 3, 4, 5), timesTwo));

        // Here is a simple binary function. It takes two integers, and returns an integer.
        // In Haskell, we would write its signature like Integer => Integer => Integer
        final Function2<Integer, Integer, Integer> add = new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer i1, Integer i2) {
                return i1 + i2;
            }
        };
        System.out.println("3 + 8 = " + add.apply(3, 8));

        // Let's do a partial application, which curries our binary function down to a unary function.
        final Function1<Integer, Integer> addFive = add.apply(5);
        System.out.println("5 + 7 = " + addFive.apply(7));

        // Here's a binary function involving multiple types:
        final Function2<String, String, Integer> repeatString = new Function2<String, String, Integer>() {
            @Override
            public String apply(String i1, Integer i2) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < i2; i++) {
                    builder.append(i1);
                }
                return builder.toString();
            }
        };
        System.out.println("repeatString.apply(\"badger\", 3) = " + repeatString.apply("badger", 3));

        // Here's a higher-order binary function:
        final Function2<Integer, Integer, Function1<Integer, Integer>> applyFunctionAndAddResultToOriginalValue =
                new Function2<Integer, Integer, Function1<Integer, Integer>>() {
                    @Override
                    public Integer apply(Integer i1, Function1<Integer, Integer> i2) {
                        return i1 + i2.apply(i1);
                    }
                };
        System.out.println("applyFunctionAndAddResultToOriginalValue.apply(5, timesTwo) = " +
            applyFunctionAndAddResultToOriginalValue.apply(5, timesTwo));

        Function2<List<Integer>, List<Integer>, Function1<Integer, Integer>> transformIntListToIntList =
                buildTransformList(Integer.class, Integer.class);
        System.out.println("transformIntListToIntList.apply([1, 2, 3], timesTwo) = "
                + transformIntListToIntList.apply(Arrays.asList(1, 2, 3), timesTwo));

        final Function2<List, List, Function1> rawTransformList = new Function2<List, List, Function1>() {
            @Override
            public List apply(List i1, Function1 i2) {
                List outputList = new ArrayList();
                for (Object t : i1) {
                    outputList.add(i2.apply(t));
                }
                return outputList;
            }
        };
        System.out.println("rawTransformList.apply([1,2,3], timesTwo) = "
                + rawTransformList.apply(Arrays.asList(1, 2, 3), timesTwo));


        // Here's a nullary function, which behaves like a constant value (unless it has side-effects, which
        // shouldn't be the case in a "proper" functional approach).
        final Function0<Integer> noisyThree = new Function0<Integer>() {
            @Override
            public Integer apply() {
                System.out.println("I am the number 3!");
                return 3;
            }
        };
        System.out.println("Noisy numbers are not noisy until we use them.");
        System.out.println("5 + 3 = " + addFive.apply(noisyThree));

        // Here is a more useful nullary function, which implements a lazy value.
        // The first time is used, it will initialize its value. Thereafter it will just return it.
        final Function0<Integer> lazyThree = Function0.lazy(new Function0<Integer>() {
            @Override
            public synchronized Integer apply() {
                try {
                    // Think really hard about what the value should be
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // Ignore
                }
                return 3;
            }
        });

        System.out.println("About to do a slow addition...");
        System.out.println("5 + 3 = " + addFive.apply(lazyThree));
        System.out.println("But this one is much faster: 4 + 3 = " + add.apply(4).apply(lazyThree));

        final Function1<Integer, Tuple2<Integer, Integer>> tupledAdd = add.tupled();
        System.out.println("9 + 2 = " + tupledAdd.apply(new Tuple2<Integer, Integer>(9, 2)));

    }
}
