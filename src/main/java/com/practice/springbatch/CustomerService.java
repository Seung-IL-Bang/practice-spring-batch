package com.practice.springbatch;

public class CustomerService<T> {

    private int cnt = 0;

    public T joinCustomer() {
        return (T) ("item" + cnt++);
    }

}
