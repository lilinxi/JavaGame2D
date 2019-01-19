package Javagames.threads;

import Javagames.util.SimpleFramework;
import Javagames.util.Utility;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class FileLoadingExample extends SimpleFramework {
    //使用线程池模拟加载文件
    //使用3种不同的线程池，按照可以同时运行的线程个数划分
    //
    //initialize()方法创建了100个callable任务来模拟加载100个文件
    //processInput()方法将每个任务提交给不同的线程池
    //如果按下1，将使用单个的线程池，2键使用最大32个线程的线程池，3键使用无限制的缓存线程池
    //updateObject()方法遍历Future任务的每一个列表，并且移除任何已经完成的任务
    //render()方法显示了常用的指令和任务完成的百分比
    //在应用程序关闭前，terminate()方法关闭每个线程池
    private static final int NUMBER_OF_FILES = 100;
    private ExecutorService singleThread;
    private ExecutorService thirtyTwoThreads;
    private ExecutorService unlimitedThreads;

    private boolean loading = false;
    private List<Callable<Boolean>> fileTasks;
    private List<Future<Boolean>> fileResults;

    public FileLoadingExample() {
        appWidth = 640;
        appHeight = 640;
        appSleep = 1L;
        appTitle = "File Loading Example";
        appBackground = Color.WHITE;
        appFPSColor = Color.BLACK;
    }

    @Override
    protected void initialize() {
        super.initialize();
        singleThread = Executors.newSingleThreadExecutor();
        thirtyTwoThreads = Executors.newFixedThreadPool(32);
        unlimitedThreads = Executors.newCachedThreadPool();
        fileTasks = new ArrayList<Callable<Boolean>>();
        for(int i=0;i<NUMBER_OF_FILES;i++) {
            final int taskNumber = i;
            fileTasks.add(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    try {
                        //pretend to load a file
                        //just sleep a little
                        Thread.sleep(new Random().nextInt(750));
                        System.out.println("Task: " + taskNumber);
                    } catch (InterruptedException ex) {
                    }
                    return Boolean.TRUE;
                }
            });
        }
        fileResults = new ArrayList<Future<Boolean>>();
    }

    @Override
    protected void processInput(float delta) {
        super.processInput(delta);
        if (keyboard.keyDownOnce(KeyEvent.VK_1)) {
            if (!loading) {
                for (Callable<Boolean> task : fileTasks) {
                    fileResults.add(singleThread.submit(task));
                }
            }
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_2)) {
            if (!loading) {
                for (Callable<Boolean> task : fileTasks) {
                    fileResults.add(thirtyTwoThreads.submit(task));
                }
            }
        }
        if (keyboard.keyDownOnce(KeyEvent.VK_3)) {
            if (!loading) {
                for (Callable<Boolean> task : fileTasks) {
                    fileResults.add(unlimitedThreads.submit(task));
                }
            }
        }
    }

    @Override
    protected void updateObject(float delta) {
        super.updateObject(delta);
        Iterator<Future<Boolean>> it = fileResults.iterator();
        while (it.hasNext()) {
            Future<Boolean> next = it.next();
            if (next.isDone()) {
                try {
                    if (next.get()) {
                        //任务成功后移除
                        it.remove();
                    }
                } catch (ExecutionException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        loading = !fileResults.isEmpty();
    }

    @Override
    protected void render(Graphics g) {
        super.render(g);
        textPos = Utility.drawString(g, 20, textPos,
                "",
                "Press the num key to start loading files",
                "(1) 1 Thread",
                "(2) 32 Threads",
                "(3) Unlimited Threads",
                "");
        double percentComplete = (NUMBER_OF_FILES - fileResults.size()) / (double) NUMBER_OF_FILES;
        String fileProgress =
                String.format("File Progress: %.0f%%", 100.0 * percentComplete);
        textPos = Utility.drawString(g, 20, textPos, fileProgress);
    }

    @Override
    protected void terminate() {
        super.terminate();
        shutdownExecutor(singleThread);
        shutdownExecutor(thirtyTwoThreads);
        shutdownExecutor(unlimitedThreads);
    }

    private void shutdownExecutor(ExecutorService exec) {
        try {
            exec.shutdown();
            exec.awaitTermination(10, TimeUnit.SECONDS);
            System.out.println("Executor Shutdown!!!");
        } catch (InterruptedException e) {}
    }

    public static void main(String[] args) {
        launchApp(new FileLoadingExample());
    }
}
