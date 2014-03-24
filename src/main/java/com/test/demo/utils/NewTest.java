package com.test.demo.utils;

/**
 * Created by jun.wan on 14-3-24.
 */
public class NewTest {
    public static void main(String[] args){
        String uri = "/hello/hi/test";
        String action = uri.substring(0,uri.lastIndexOf("/"));
        String method = uri.substring(uri.lastIndexOf("/")+1);

        System.out.println(action);
        System.out.println(method);
    }
}
