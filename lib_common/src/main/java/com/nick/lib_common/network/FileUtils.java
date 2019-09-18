package com.nick.lib_common.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class FileUtils
{

    public static void main(String[] args)
    {
        String url = "http://192.168.0.103:8080/docs/1.zip";
        String des = "d://test";
//        DownloadHelper.download(url, des, true, new DownloadHelper.DownloadListener() {
//            @Override
//            public void onProgress(long current, long total) {
//
//                System.out.println(current+"/"+total);
//            }
//
//            @Override
//            public void onComplete() {
//
//                System.out.println("complete");
//            }
//
//            @Override
//            public void onError(Exception e) {
//
//                System.out.println(e.getMessage());
//            }
//        });

        Observable.create(new ObservableOnSubscribe<Long>() {

            long i = 10;

            @Override
            public void subscribe(ObservableEmitter<Long> emitter) throws Exception {

                emitter.onNext(i);
//                emitter.onNext(i);
                emitter.onComplete();
            }
        }).doOnNext(new Consumer<Long>() {
            @Override
            public void accept(Long integer) throws Exception {

                System.out.println(integer + Thread.currentThread().getName());
            }
        }).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {

                System.out.println("c" + Thread.currentThread().getName());
            }
        }).map(new Function<Long, Long>() {
        @Override
        public Long apply(Long aLong) throws Exception {

            System.out.println("map" + Thread.currentThread().getName());

            return aLong + 100;
        }
    }).delay(2, TimeUnit.SECONDS).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {

                System.out.println(aLong + Thread.currentThread().getName());
            }
        });

    }

    public static void writeToFile(InputStream inputStream, File file, FileWriteListener fileWriteListener)
    {
        RandomAccessFile randomAccessFile = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException e) {
            fileWriteListener.onError(e);
        }
        long currentLength = file.length();
        byte[] bytes = new byte[1024];
        int len;
        try {
            randomAccessFile.seek(currentLength);
            while ((len = inputStream.read(bytes)) != -1)
            {
                randomAccessFile.write(bytes, 0, len);
                currentLength += len;
                fileWriteListener.onProgress(currentLength);
            }

            randomAccessFile.close();
            inputStream.close();
        } catch (Exception e){
            fileWriteListener.onError(e);
        }
        fileWriteListener.onComplete();
    }

    public interface FileWriteListener
    {
        void onProgress(long current);

        void onComplete();

        void onError(Exception e);
    }
}
