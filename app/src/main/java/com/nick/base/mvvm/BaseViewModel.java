package com.nick.base.mvvm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.nick.base.Student;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author NICK
 */
public class BaseViewModel extends AndroidViewModel
{
    private MutableLiveData<Student> studentMutableLiveData = new MutableLiveData<>();

    public BaseViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Student> getStudent()
    {
        return studentMutableLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }


    public static void main(String[] args){
        set(new A<List<String>>() {
            @Override
            public void a(List<String> o) {

            }
        });
    }

    static void set(A a){
    Type type = a.getGenericType();
    System.out.println(type.toString());
}

    interface A<T>{
        void a(T t);

        default Type getGenericType() {
            Type[] types = getClass().getGenericInterfaces();
            return ((ParameterizedType)types[0]).getActualTypeArguments()[0];
        }
    }

}
