import functions.Function0;
import functions.Function1;
import functions.Function2;
import functions.MemoizationUtils;
import tuples.Tuple2;
import utils.ListUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;
import static tuples.TupleUtils.tuple;
import static tuples.TupleUtils.tupled;
//import static tuples.TupleUtils.tuple;

public class Samples {
    public static <R, T1> List<R> transformList(final List<T1> inputList, final Function1<R, T1> f) {
        List<R> outputList = new ArrayList<R>();
        for (T1 t : inputList) {
            outputList.add(f.evaluate(t));
        }
        return outputList;
    }

    private static final Function2 rawTransformList = new Function2<List, List, Function1>() {
        @Override
        public List evaluate(Function2<List, List, Function1> self, List i1, Function1 i2) {
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
            public Integer evaluate(Function1<Integer, Integer> self, Integer i1) {
                return i1 * 2;
            }
        };
        System.out.println("timesTwo.evaluate(5) = " + timesTwo.apply(5));
        System.out.println("transformList([1,2,3,4,5], timesTwo) = "
                + transformList(Arrays.asList(1, 2, 3, 4, 5), timesTwo));

        // Here is a simple binary function. It takes two integers, and returns an integer.
        // In Haskell, we would write its signature like Integer -> Integer -> Integer
        final Function2<Integer, Integer, Integer> add = new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer evaluate(Function2<Integer, Integer, Integer> self,
                                    Integer i1, Integer i2) {
                return i1 + i2;
            }
        };
        System.out.println("4 + 5 = " + add.evaluate(4, 5));
        System.out.println("4 + 5 = " + add.curry().apply(4).get().evaluate(5));

        System.out.println("3 + 8 = " + add.evaluate(3, 8));

        // Let's do a partial application, which curries our binary function down to a unary function.
        final Function1<Integer, Integer> addFive = add.apply(5);
        System.out.println("5 + 7 = " + addFive.apply(7));

        // Here's a binary function involving multiple types:
        final Function2<String, String, Integer> repeatString = new Function2<String, String, Integer>() {
            @Override
            public String evaluate(Function2<String, String, Integer> self,
                                   String i1, Integer i2) {
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < i2; i++) {
                    builder.append(i1);
                }
                return builder.toString();
            }
        };
        System.out.println("repeatString.evaluate(\"badger\", 3) = " + repeatString.evaluate("badger", 3));

        // Here's a higher-order binary function:
        final Function2<Integer, Integer, Function1<Integer, Integer>> applyFunctionAndAddResultToOriginalValue =
                new Function2<Integer, Integer, Function1<Integer, Integer>>() {
                    @Override
                    public Integer evaluate(Function2<Integer, Integer, Function1<Integer, Integer>> self,
                                            Integer i1, Function1<Integer, Integer> i2) {
                        return i1 + i2.apply(i1).get();
                    }
                };
        System.out.println("applyFunctionAndAddResultToOriginalValue.evaluate(5, timesTwo) = " +
                applyFunctionAndAddResultToOriginalValue.apply(5, timesTwo).get());

        final Function1<Integer, String> stringLength = new Function1<Integer, String>() {
            @Override
            public Integer evaluate(Function1<Integer, String> self, String i1) {
                return i1.length();
            }
        };

        Function2<List<Integer>, List<Integer>, Function1<Integer, Integer>> transformIntListToIntList =
                buildTransformList(Integer.class, Integer.class);
        System.out.println("transformIntListToIntList.evaluate([1, 2, 3], timesTwo) = "
                + transformIntListToIntList.apply(Arrays.asList(1, 2, 3), timesTwo).get());

        /* System.out.println("transformIntListToIntList.evaluate([1, 2, 3], stringLength) = "
                + transformIntListToIntList.evaluate(Arrays.asList(1, 2, 3), stringLength));
         */
        System.out.println("map(stringLength, [\"a\",\"bb\",\"ccc\"]) = " +
                ListUtils.map(stringLength).apply(Arrays.asList("a", "bb", "ccc")));


        final Function2<List, List, Function1> rawTransformList = new Function2<List, List, Function1>() {
            @Override
            public List evaluate(Function2<List, List, Function1> self, List i1, Function1 i2) {
                List outputList = new ArrayList();
                for (Object t : i1) {
                    outputList.add(i2.apply(t));
                }
                return outputList;
            }
        };
        // The following outputs: rawTransformList.evaluate([1,2,3], timesTwo) = [2, 4, 6]
        System.out.println("rawTransformList.evaluate([1,2,3], timesTwo) = "
                + rawTransformList.apply(Arrays.asList(1, 2, 3), timesTwo).get());

        // The following produces a ClassCastException
//        System.out.println("rawTransformList.evaluate([1,2,3], stringLength) = "
//                + rawTransformList.evaluate(Arrays.asList(1, 2, 3), stringLength));


        // Here's a nullary function, which behaves like a constant value (unless it has side-effects, which
        // shouldn't be the case in a "proper" functional approach).
        final Function0<Integer> noisyThree = new Function0<Integer>() {
            @Override
            public Integer evaluate() {
                System.out.println("I am the number 3!");
                return 3;
            }
        };
        System.out.println("Noisy numbers are not noisy until we use them.");
        System.out.println("5 + 3 = " + addFive.apply(noisyThree).get());

        // Here is a more useful nullary function, which implements a lazy value.
        // The first time is used, it will initialize its value. Thereafter it will just return it.
        final Function0<Integer> lazyThree = new Function0<Integer>() {
            @Override
            public synchronized Integer evaluate() {
                try {
                    // Think really hard about what the value should be
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    // Ignore
                }
                return 3;
            }
        };

        System.out.println("About to do a slow addition...");
        System.out.println("5 + 3 = " + addFive.apply(lazyThree).get());
        System.out.println("But this one is much faster: 4 + 3 = " + add.apply(4).apply(lazyThree).get());

        // Test tupling a function
        final Function1<Integer, Tuple2<Integer, Integer>> tupledAdd = tupled(add);
        System.out.println("9 + 2 = " + tupledAdd.apply(tuple(9, 2)).get());

        // Let's test some folds
        final List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        final Function2<Integer, Integer, Integer> noisyAdd = new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer evaluate(Function2<Integer, Integer, Integer> self, Integer i1, Integer i2) {
                System.out.println(format("Add called with arguments %d and %d", i1, i2));
                return i1 + i2;
            }
        };
        System.out.println("foldLeft result is " +
                ListUtils.foldLeft(noisyAdd).apply(0, list).get());
        System.out.println("foldRight result is " +
                ListUtils.foldRight(noisyAdd).apply(0, list).get());

        final Function1<Integer, Integer> fibonacci =
                new Function1<Integer, Integer>() {
            @Override
            public Integer evaluate(final Function1<Integer, Integer> self, final Integer i1) {
                if (i1 <= 0) {
                    return 0;
                }
                return self.apply(i1 - 1).get() + self.apply(i1 - 2).get();
            }
        };
        long startFib = System.currentTimeMillis();
        for (int i = 1; i < 35; i++) {
            fibonacci.apply(i).get();
        }
        System.out.println("Fib took " + (System.currentTimeMillis() - startFib) + "ms");
        startFib = System.currentTimeMillis();
        Function1<Integer, Integer> memoizedFibonacci = MemoizationUtils.memoize(fibonacci);
        for (int i = 1; i < 35; i++) {
            memoizedFibonacci.apply(i).get();
        }
        System.out.println("MemoizedFib took " + (System.currentTimeMillis() - startFib) + "ms");

        final AtomicLong numCalls = new AtomicLong(0);
        Function2<Boolean, Integer, Integer> recursiveFunction2 = new Function2<Boolean, Integer, Integer>() {
            @Override
            public Boolean evaluate(final Function2<Boolean, Integer, Integer> self, final Integer i1, final Integer i2) {
                numCalls.incrementAndGet();
                boolean result = true;
                if (i1 > 1) {
                    result |= self.apply(i1 - 1, i2).get() && self.apply(i1 - 2, i2).get();
                }
                if (i2 > 1) {
                    result &= self.apply(i1, i2 - 1).get() && self.apply(i1, i2 - 2).get();
                }
                return result;
            }
        };
        recursiveFunction2.apply(10, 10).get();
        System.out.println("Num evaluations in recursiveFunction2 = " + numCalls.get());
        numCalls.set(0);
        MemoizationUtils.memoize(recursiveFunction2).apply(10, 10).get();
        System.out.println("Num evaluations in memoized recursiveFunction2 = " + numCalls.get());
    }

}
