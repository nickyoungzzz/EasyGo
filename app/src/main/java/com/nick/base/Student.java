package com.nick.base;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 *
 */
public class Student extends BaseObservable implements Serializable,SS {

    @Bindable
    public String name;

    public Student(String name, int age) {
        this.name = name;
        this.age = age;
        F.a();
    }

    @Bindable
    public int age;


    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
        notifyChange();
    }

    public void setAge(int age) {
        this.age = age;
        notifyPropertyChanged(BR.age);
    }

    @Override
    public void b() {
    }

    @Override
    public void a(@NotNull String ss) {
    }
}
