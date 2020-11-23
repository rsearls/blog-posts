package org.jboss.demo;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.internal.operators.flowable.FlowableCreate;
import org.reactivestreams.Subscription;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Publisher;

public class Main {

    public static void main(String[] args) {
        Main m = new Main();

        System.out.println("-- pojoStyle --");
        m.pojoStyle();

        System.out.println("\n-- lambdaStyle --");
        m.lambdaStyle();

        System.out.println("\n-- flowableCreateStyle --");
        m.flowableCreateStyle();

        System.out.println("\n-- flowableStyle --");
        m.flowableStyle();
    }

    /**
     * Old school coding style that uses a POJO
     */
    public void pojoStyle() {
        NewsPublisher publisher = new NewsPublisher();
        NewsSubscriber subscriber = new NewsSubscriber();
        publisher.subscribe(subscriber);
    }

    /**
     * Modern Lambda coding style.
     */
    public void lambdaStyle() {
        NewsPublisher pub = new NewsPublisher();
        pub.subscribe(new Subscriber <>(){
            ArrayList<String> articleList = new ArrayList<>();
            private Subscription subscription;
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("Subscriber received Subscription object: "
                        + s.getClass().getSimpleName());
                subscription = s;
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(String t) {
                System.out.println("Subscriber received data: " + t);
                articleList.add(t);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Subscriber received Error notification. Msg:" +t);
            }

            @Override
            public void onComplete() {
                System.out.println("Subscriber notified Publication Compete");
            }
        });
    }

    /**
     *
     */
    public void flowableCreateStyle() {
        List<String> articles = List.of(
                "NASA Notables",
                "The Gardian",
                "Soccer Weekly",
                "Better Farming",
                "Fine Home Building",
                "Consumer Report");

        FlowableCreate<String> flowableC = new FlowableCreate(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                for (int i = articles.size()-1; i >= 0; i--) {
                    emitter.onNext(articles.get(i));
                }
                emitter.onComplete();
            }
        }, BackpressureStrategy.BUFFER);

        flowableC.subscribeActual(new NewsSubscriber());
    }

    /**
     *
     */
    public void flowableStyle() {
        List<String> articles = List.of(
                "NASA Notables",
                "The Gardian",
                "Soccer Weekly",
                "Better Farming",
                "Fine Home Building",
                "Consumer Report");

        Flowable<String> flowable = Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                for (int i = articles.size()-1; i >= 0; i--) {
                    emitter.onNext(articles.get(i));
                }
                emitter.onComplete();
            }
        }, BackpressureStrategy.BUFFER);

        ((Publisher<String>)flowable).subscribe(new Subscriber <>(){
            ArrayList<String> articleList = new ArrayList<>();
            private Subscription subscription;
            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("Subscriber received Subscription object: "
                        + s.getClass().getSimpleName());
                subscription = s;
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(String t) {
                System.out.println("Subscriber received data: " + t);
                articleList.add(t);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("Subscriber received Error notification. Msg:" +t);
            }

            @Override
            public void onComplete() {
                System.out.println("Subscriber notified Publication Compete");
            }
        });
    }
}
