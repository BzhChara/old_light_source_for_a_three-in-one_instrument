package top.jemen.utils.threadpool;

import top.jemen.utils.ExceptionHandler;

public class AsyncProcessQueue {

    /**
     * Task 包装类<br>
     * 此类型的意义是记录可能会被 Executor 吃掉的异常<br>
     */
    public static class TaskWrapper implements Runnable {
        private final Runnable gift;

        public TaskWrapper(final Runnable target) {
            this.gift = target;
        }

        @Override
        public void run() {
            // 捕获异常，避免在 Executor 里面被吞掉了
            if (gift != null) {

                try {
                    gift.run();
                } catch (Exception e) {
                    ExceptionHandler.handleException(e);
                }
            }
        }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * 执行指定的任务
     *
     * @param task
     * @return
     */
    public static boolean execute(final Runnable task) {
        return AsyncProcessor.executeTask(new TaskWrapper(task));
    }
}
