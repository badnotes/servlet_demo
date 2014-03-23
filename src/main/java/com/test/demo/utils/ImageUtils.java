package com.test.demo.utils;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: wanjun
 * Date: 9/26/13
 * Time: 11:55 AM
 */
public class ImageUtils {

    public void locate(String filePath) throws IOException{

        /**
         * 图片中心400*400的区域
         */
        Thumbnails.of("images/test.jpg").sourceRegion(Positions.CENTER, 400,
                400).size(200, 200).keepAspectRatio(false).toFile(
                "C:/image_region_center.jpg");
        /**
         * 图片右下400*400的区域
         */
        Thumbnails.of("images/test.jpg").sourceRegion(Positions.BOTTOM_RIGHT,
                400, 400).size(200, 200).keepAspectRatio(false).toFile(
                "C:/image_region_bootom_right.jpg");
        /**
         * 指定坐标
         */
        Thumbnails.of("images/test.jpg").sourceRegion(600, 500, 400, 400).size(
                200, 200).keepAspectRatio(false).toFile(
                "C:/image_region_coord.jpg");
    }

    public static void main(String[] args){

        try {
            Thumbnails.of("/home/wanjun/cc.jpg").sourceRegion(0, 0, 400, 400).size(200,200).keepAspectRatio(false).toFile("/home/wanjun/ddd.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
