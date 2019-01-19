package Javagames.threads;

import java.util.Random;
import java.util.concurrent.*;

public class BlockingQueueExample {
    //介绍了如何在线程之间传递消息
    //
    //BlockingQueue是一个线程安全的类，它提供了方法从队列获取项并将项放入到队列中。
    //put/take方法允许BlockingQueue在线程之间传递信息。
    //传递给put的项添加到了队列中，并且只要还没有达到队列的大小，该方法就立即返回。它不会等待某个其他的线程获取值。
    //take方法将阻塞，直到有某些内容可用。
    //
    //Producer发送消息，Consumer接受消息
    class Producer implements Callable<Void> {
        //注意Producer使用一个Void对象作为返回类型，Void类型用于那些不返回任何内容的Callable对象。
        private Random rand = new Random();
        private int numberOfMessages;
        private int sleep;

        private Producer(int numberOfMessages, int sleep) {
            this.numberOfMessages = numberOfMessages;
            this.sleep = sleep;
        }

        @Override
        public Void call() throws Exception {
            Message[] messages = Message.values();
            for(int i=0;i<numberOfMessages;i++) {
                try {
                    //don't include last message
                    int index = rand.nextInt(messages.length - 2);
                    System.out.println("PUT(" + (i + 1) + ")" + messages[index]);
                    queue.put(messages[index]);
                    sleep(sleep);
                } catch (InterruptedException ex) {}
            }
            //All done. Shut her done...
            queue.put(messages[messages.length - 1]);
            return null;
        }
    }

    class Consumer implements Callable<Integer> {
        private int messageCount = 0;

        @Override
        public Integer call() throws Exception {
            while (true) {
                //take will block forever unless we do something
                Message msg = queue.take();
                messageCount++;
                System.out.println("Received: " + msg);
                //由于take方法将阻塞，直到有一条消息等待，因此关闭该线程的一种方法是传递一条特殊的POISON_PILL消息，
                //以便当队列关闭线程的时候，消费者能够识别到。
                if (msg == Message.POISON_PILL) {
                    break;
                }
            }
            return new Integer(messageCount);
        }
    }

    enum Message {
        MESSAGE_ONE,
        MESSAGE_TWO,
        MESSAGE_THREE,
        POISON_PILL,
    }

    private ExecutorService exec;
    private BlockingQueue<Message> queue;

    public BlockingQueueExample() {
        exec = Executors.newCachedThreadPool();
        queue = new LinkedBlockingQueue<Message>();
    }

    public void runTest() {
        //Producer和Consumer可以互换顺序执行
        int numberOfMessages = 100;
        int sleep = 100;
        System.out.println("Message Sent: " + numberOfMessages);
        //Future<Integer> consumer = exec.submit(new Consumer());
        exec.submit(new Producer(numberOfMessages, sleep));
        //sleep a little
        sleep(2000);
        try {
            //Start the consumer much latter, but that's ok!
            //exec.submit(new Producer(numberOfMessages, sleep));
            Future<Integer> consumer = exec.submit(new Consumer());
            try {
                System.out.println("Message Processed: " + consumer.get());
            } catch (ExecutionException ex) {
            } catch (InterruptedException ex) {}
        }finally {
            try {
                exec.shutdown();
                exec.awaitTermination(10, TimeUnit.SECONDS);
                System.out.println("Threadpool Shutdown");
            } catch (InterruptedException e) {
                //at this point, just give up...
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

    protected void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) { }
    }

    public static void main(String[] args) {
        new BlockingQueueExample().runTest();
    }
}
