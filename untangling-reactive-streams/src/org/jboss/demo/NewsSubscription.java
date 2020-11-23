package org.jboss.demo;

import org.reactivestreams.Subscription;

public class NewsSubscription implements Subscription {
    private long cnt = 0;

    @Override
    public void request(long n) {
        cnt = n;
    }

    @Override
    public void cancel() {
    }

    public long getCnt() {
        return cnt;
    }
}
