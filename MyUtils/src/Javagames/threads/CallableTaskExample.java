package Javagames.threads;

import java.util.Random;
import java.util.concurrent.*;

public class CallableTaskExample implements Callable<Boolean> {
    //如何使用一个线程池
    //使用ExecutorService，Callable<V>和Future<V>
    //由Executors.newCachedThreadPool()创建ExecutorService对象，在线程池中运行Callable，使用Future接收返回结果
    //
    //ExecutorService
    //public interface ExecutorService
    // extends Executor
    // Executor 提供了管理终止的方法，以及可为跟踪一个或多个异步任务执行状况而生成 Future 的方法。
    //
    //
    //Callable<V>
    //public interface Callable<V>返回结果并且可能抛出异常的任务。实现者定义了一个不带任何参数的叫做 call 的方法。
    //
    //Callable 接口类似于 Runnable，两者都是为那些其实例可能被另一个线程执行的类设计的。
    // 但是 Runnable 不会返回结果，并且无法抛出经过检查的异常。
    //
    // V call()
    //          计算结果，如果无法计算结果，则抛出一个异常。
    //
    //Executors 类包含一些从其他普通形式转换成 Callable 类的实用方法
    //
    //
    //Future
    //public interface Future<V>
    // Future 表示异步计算的结果。
    // 它提供了检查计算是否完成的方法，以等待计算的完成，并获取计算的结果。
    // 计算完成后只能使用 get 方法来获取结果，如有必要，计算完成前可以阻塞此方法。
    // 取消则由 cancel 方法来执行。还提供了其他方法，以确定任务是正常完成还是被取消了。
    // 一旦计算完成，就不能再取消计算。
    // 如果为了可取消性而使用 Future 但又不提供可用的结果，
    // 则可以声明 Future<?> 形式类型、并返回 null 作为底层任务的结果
    //
    //
    @Override
    public Boolean call() throws Exception {
        //simulate some stupid long task and maybe fail...
        Random rand = new Random();
        int seconds = rand.nextInt(6);
        if (seconds == 0) {
            //pretend there was an error
            throw new RuntimeException("I love the new thread stuff!!!");
        }
        try {
            //当睡眠时间为0的时候抛出一个异常
            Thread.sleep(seconds * 100);
        } catch (InterruptedException e) {}
        //even = true, odd = false
        return seconds % 2 == 0;
    }

    public static void main(String[] args) {
        //public class Executors extends Object此包中所定义的
        // Executor、ExecutorService、ScheduledExecutorService、
        // ThreadFactory 和 Callable 类的工厂和实用方法。
        //
        // 此类支持以下各种方法：
        //
        //创建并返回设置有常用配置字符串的 ExecutorService 的方法。
        //创建并返回设置有常用配置字符串的 ScheduledExecutorService 的方法。
        //创建并返回“包装的”ExecutorService 方法，它通过使特定于实现的方法不可访问来禁用重新配置。
        //创建并返回 ThreadFactory 的方法，它可将新创建的线程设置为已知的状态。
        //创建并返回非闭包形式的 Callable 的方法，这样可将其用于需要 Callable 的执行方法中
        //
        //static ExecutorService newCachedThreadPool()
        //          创建一个可根据需要创建新线程的线程池，但是在以前构造的线程可用时将重用它们。
        ExecutorService exec = Executors.newCachedThreadPool();
        try {
            for(int i=0;i<50;i++) {
                try {
                    // Future<?> submit(Runnable task)
                    //          提交一个 Runnable 任务用于执行，并返回一个表示该任务的 Future。
                    Future<Boolean> result =
                            exec.submit(new CallableTaskExample());
                    // V get()
                    //          如有必要，等待计算完成，然后获取其结果。
                    Boolean success = result.get();
                    System.out.println("Result: " + success);
                } catch (ExecutionException ex) {
                    //public class ExecutionException extends Exception
                    // 当试图获取已通过抛出异常而中止的任务的结果时，抛出此异常。
                    // 可以使用 Throwable.getCause() 方法来检查此异常
                    //
                    //public Throwable getCause()返
                    // 回此 throwable 的 cause；
                    // 如果 cause 不存在或未知，则返回 null。
                    // （该 Cause 是导致抛出此 throwable 的throwable。）
                    Throwable throwable = ex.getCause();
                    System.out.println("Error: " + throwable.getMessage());
                } catch (InterruptedException e) {
                    System.out.println("Awesome! Thread was canceled");
                    e.printStackTrace();
                }
            }
        }finally {
            try {
                exec.shutdown();
                //boolean awaitTermination(long timeout,
                //                         TimeUnit unit)
                //                         throws InterruptedException
                // 请求关闭、发生超时或者当前线程中断，无论哪一个首先发生之后，都将导致阻塞，直到所有任务完成执行。
                //
                //参数：
                //timeout - 最长等待时间
                //unit - timeout 参数的时间单位
                exec.awaitTermination(10, TimeUnit.SECONDS);
                System.out.println("Threadpool ShutDown");
            } catch (InterruptedException e) {
                //at this point, just give up...
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }
}
