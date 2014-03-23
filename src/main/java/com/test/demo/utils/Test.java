package com.test.demo.utils;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 14-3-17
 * Time: 下午11:01
 * To change this template use File | Settings | File Templates.
 */
public class Test {

    private static final int ARRAY_SIZE = 10;
    public static void main(String[] args){

        int[] arr = {51,116,53,120,85,66,71,98,86,100};
        int i, j;
        for(i = 0; i < ARRAY_SIZE; i++)
        for(j = 0; j < ARRAY_SIZE-1; j++)
        if(arr[j] > arr[j+1]) {
            arr[j] ^= arr[j+1];
            arr[j+1] ^= arr[j];
            arr[j] ^= arr[j+1];
        }
        for(i = 0; i < ARRAY_SIZE; i++){
            System.out.printf("%c",arr[i], arr[i]);
        }
        for(i = 0; i < ARRAY_SIZE; i++){
            System.out.printf("%d", arr[i]);
        }
        System.out.println();
    }
}
