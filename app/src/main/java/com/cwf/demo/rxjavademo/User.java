package com.cwf.demo.rxjavademo;

/**
 * Created at 陈 on 2016/6/29.
 *
 * @author cwf
 * @email 237142681@qq.com
 */
public class User {
    private String name;
    private String address;

    public User(String name, String address) {
        this.name = name;
        this.address = address;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
