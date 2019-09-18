package com.nick.lib_common.network;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public class DownloadHelper {

    public static void download(String url, String des, boolean point, DownloadListener listener) {
//        File file = new File(des + url.substring(url.lastIndexOf("/")));
        File file = new File(des);
        if (file.exists() && file.length() > 0 && file.isFile() && !point) {
            file.delete();
        }
        long current = file.length();
        boolean completed = false;
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {

                Request request = chain.request();
                okhttp3.Response response = chain.proceed(request);
                ResponseBody responseBody = response.body();
                long totalContentLength = responseBody.contentLength();
                boolean completed = current == totalContentLength;
                FileResponseBody fileResponseBody = new FileResponseBody(responseBody, totalContentLength, completed);
                if (current < totalContentLength && current > 0) {
                    Request request1 = request.newBuilder().addHeader("RANGE", "bytes=" + current + "-").addHeader("Accept-Encoding", "identity").build();
                    okhttp3.Response response1 = chain.proceed(request1);
                    fileResponseBody = new FileResponseBody(response1.body(), totalContentLength, completed);
                }
                return response.newBuilder().body(fileResponseBody).build();
            }
        }).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url + "/").client(okHttpClient).addConverterFactory(new Converter.Factory() {
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
                return (Converter<ResponseBody, FileResponseBody>) value -> new FileResponseBody(value, current + value.contentLength(), completed);
            }
        }).build();

        DownLoadHelperService downLoadHelperService = retrofit.create(DownLoadHelperService.class);
        downLoadHelperService.download(url).enqueue(new Callback<FileResponseBody>() {
            @Override
            public void onResponse(Call<FileResponseBody> call, Response<FileResponseBody> response) {

                FileResponseBody fileResponseBody = response.body();

                System.out.println(current +"/" );

                if ((fileResponseBody.contentLength() == current && fileResponseBody.completed)) {
                    listener.onComplete();
                    return;
                }

                FileUtils.writeToFile(fileResponseBody.byteStream(), file, new FileUtils.FileWriteListener() {

                    long current;

                    long total = fileResponseBody.getTotalContentLength();

                    Disposable disposable = Observable.interval(1, TimeUnit.SECONDS).observeOn(Schedulers.io()).subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {

                            listener.onProgress(current, total);
                        }
                    });

                    @Override
                    public void onProgress(long currentLength) {

                        current = currentLength;
                        if (!disposable.isDisposed() && current == total) {
                            listener.onProgress(current, total);
                        }
                    }

                    @Override
                    public void onComplete() {

                        listener.onComplete();
                        disposable.dispose();
                    }

                    @Override
                    public void onError(Exception e) {

                        listener.onError(e);
                        disposable.dispose();
                    }
                });

                fileResponseBody.close();
            }

            @Override
            public void onFailure(Call<FileResponseBody> call, Throwable t) {

            }
        });
    }

    public interface DownloadListener {

        void onProgress(long current, long total);

        void onComplete();

        void onError(Exception e);
    }

    public interface DownLoadHelperService {

        @Streaming
        @GET
        Call<FileResponseBody> download(@Url String url);
    }

    static class FileResponseBody extends ResponseBody {

        private ResponseBody responseBody;

        private boolean completed;

        public long getTotalContentLength() {
            return totalContentLength;
        }

        private long totalContentLength;

        public FileResponseBody(ResponseBody responseBody, long totalContentLength, boolean completed) {
            this.responseBody = responseBody;
            this.totalContentLength = totalContentLength;
            this.completed = completed;
        }

        @Override
        public MediaType contentType() {
            return responseBody.contentType();
        }

        @Override
        public long contentLength() {
            return responseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            return responseBody.source();
        }
    }

}
