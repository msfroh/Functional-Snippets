package concurrency;

import functions.Function0;
import functions.Function1;
import tuples.Tuple0;

import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: msfroh
 * Date: 2013-01-05
 * Time: 3:06 PM
 */
public class AsyncEvaluator<T> {
  private final Function0<T> value;
  private final Executor executor;
  private boolean invoked = false;
  private final Collection<Function1<Tuple0, ? super T>> callbacks =
    new LinkedList<>();

  public AsyncEvaluator(final Function0<T> value, final Executor executor) {
    this.value = value;
    this.executor = executor;
  }

  public synchronized void invoke() {
    if (invoked) {
      return;
    }
    invoked = true;
    executor.execute(new Runnable() {
      @Override
      public void run() {
        T t = value.get();
        for (Function1<Tuple0, ? super T> callback : callbacks) {
          callback.evaluate(t);
        }
      }
    });
  }

  public synchronized void addCallback(Function1<Tuple0, ? super T> callback) {
    if (invoked) {
      throw new RuntimeException("Should not add callback to an " +
        "invoked AsyncEvaluator");
    }
    callbacks.add(callback);
  }

  public <U> AsyncEvaluator<U> chain(final Function0<U> value,
                                     final Executor executor) {
    final AsyncEvaluator<U> evaluator = new AsyncEvaluator<>(value, executor);
    addCallback(new Function1<Tuple0, T>() {
      @Override
      public Tuple0 evaluate(final Function1<Tuple0, T> self, final T i1) {
        evaluator.invoke();
        return Tuple0.INSTANCE;
      }
    });
    return evaluator;
  }

  public <U> AsyncEvaluator<U> chain(final Function0<U> value) {
    return chain(value, executor);
  }

  public static <U> AsyncEvaluator<U> joinedChain(Function0<U> value,
                                    Executor executor,
                                    AsyncEvaluator<?>... previousEvaluators) {
    final AsyncEvaluator<U> evaluator = new AsyncEvaluator<>(value, executor);
    final AtomicInteger remainingEvaluators =
      new AtomicInteger(previousEvaluators.length);
    final Function1<Tuple0, Object> callback = new Function1<Tuple0, Object>() {
      @Override
      public Tuple0 evaluate(final Function1<Tuple0, Object> self,
                             final Object i1) {
        if (remainingEvaluators.decrementAndGet() == 0) {
          evaluator.invoke();
        }
        return Tuple0.INSTANCE;
      }
    };
    for (AsyncEvaluator<?> prev : previousEvaluators) {
      prev.addCallback(callback);
    }
    return evaluator;
  }
}
