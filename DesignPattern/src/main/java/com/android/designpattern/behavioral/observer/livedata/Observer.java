package com.android.designpattern.behavioral.observer.livedata;

public interface Observer<T> {

    void onChanged(T t);

}
