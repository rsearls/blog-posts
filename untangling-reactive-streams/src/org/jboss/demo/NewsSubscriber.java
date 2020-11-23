package org.jboss.demo;

import org.reactivestreams.Subscription;
import org.reactivestreams.Subscriber;

import java.util.ArrayList;

public class NewsSubscriber implements Subscriber<String> {
    private Subscription subscription;
    private ArrayList<String> articleList = new ArrayList<>();
    private long r = 1;

    @Override
    public void onSubscribe(Subscription s) {
        System.out.println("Subscriber received Subscription object: "
                + s.getClass().getSimpleName());
        subscription = s;
        subscription.request(r);
    }

    @Override
    public void onNext(String t) {
        System.out.println("r=" + r + "  Subscriber received data: " + t);
        articleList.add(t);
        --r;
        if(r == 0) {
            // toggle article flow between 1 and 2 articles
            r = (articleList.size() % 2) + 1;
            subscription.request(r);
        }
    }

    @Override
    public void onError(Throwable t) {
        System.out.println("Subscriber received Error notification. Msg:" +t);
    }

    @Override
    public void onComplete() {
        System.out.println("Subscriber notified Publication Compete");
    }
}
