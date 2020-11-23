package org.jboss.demo;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.List;
import java.util.Stack;

public class NewsPublisher implements Publisher<String> {
    private Subscriber subscriber;
    private NewsSubscription subscription = new NewsSubscription();
    private List<String> articles = List.of(
            "NASA Notables",
            "The Gardian",
            "Soccer Weekly",
            "Better Farming",
            "Fine Home Building",
            "Consumer Report");
    private Stack<String> stack = null;

    @Override
    public void subscribe(Subscriber<? super String> s) {
        subscriber = s;
        s.onSubscribe(subscription);

        stack = new Stack();
        articles.forEach(a -> stack.push(a));
        sendData();
    }

    private void sendData() {

        if (stack.empty()) {
            subscriber.onError(new RuntimeException("no news articles") );
            return;
        }

        while(true) {
            long cnt = subscription.getCnt();

            if (stack.empty()) {
                subscriber.onComplete();
                return;
            } else {
                for (; cnt > 0; cnt--) {
                    subscriber.onNext(stack.pop());

                    if (stack.empty()) {
                        subscriber.onComplete();
                        return;
                    }
                }
            }
        }
    }
}
