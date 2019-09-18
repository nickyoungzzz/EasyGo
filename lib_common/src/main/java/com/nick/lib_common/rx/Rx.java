package com.nick.lib_common.rx;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

public class Rx {
    static Disposable disposable;
    public static void main(String[] args){

//        test();
    }
    static void test()
    {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                emitter.onNext("0123");
            }
        }).flatMap(new Function<String, ObservableSource<Character>>() {
            @Override
            public ObservableSource<Character> apply(String s) throws Exception {

                if (s.length() == 4)
                {
                    return Observable.just(s.charAt(0), s.charAt(1));
                }
                return Observable.error(new RuntimeException("error"));
            }
        }).subscribe(new Observer<Character>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Character character) {

                p(character);
            }

            @Override
            public void onError(Throwable e) {

                p(e.getMessage());
            }

            @Override
            public void onComplete() {

            }
        });

    }

    static void p(Object t)
    {
        System.out.println(t);
    }
}
