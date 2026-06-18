package top.jemen.utils.threadpool;

import android.app.Notification;

import java.util.concurrent.ThreadFactory;

public class BasicThreadFactory {
    public static class Builder{
        private String threadName ="jemenPoolThread"  ;
        public Builder namingPattern(final String threadPoolName) {
            this.threadName=threadName;
            return this;
        }

        public Builder daemon(boolean b) {

            return this;
        }

        public ThreadFactory build() {
            return  new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r,threadName);
                }
            };
        }
    }
}
