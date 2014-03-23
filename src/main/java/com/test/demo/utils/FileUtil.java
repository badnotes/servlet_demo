package com.test.demo.utils;

import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Calendar;

/**
 * 文件工具类
 *
 * @author wanjun
 *
 */
public class FileUtil {
	private static final Logger logger = Logger.getLogger(FileUtil.class);

	private final static int BUFFER = 1024;

	/**
	 * 功 能: 移动文件(只能移动文件) 参 数: strSourceFileName:指定的文件全路径名 strDestDir: 移动到指定的文件夹 返回值: 如果成功true;否则false
	 *
	 * @param strSourceFileName
	 * @param strDestDir
	 * @return
	 */
	public static boolean copyTo(String strSourceFileName, String strDestDir) {
		File fileSource = new File(strSourceFileName);
		File fileDest = new File(strDestDir);

		// 如果源文件不存或源文件是文件夹
		if (!fileSource.exists() || !fileSource.isFile()) {
			logger.debug("源文件[" + strSourceFileName + "],不存在或是文件夹!");
			return false;
		}

		// 如果目标文件夹不存在
		if (!fileDest.isDirectory() || !fileDest.exists()) {
			if (!fileDest.mkdirs()) {
				logger.debug("目录文件夹不存，在创建目标文件夹时失败!");
				return false;
			}
		}

		try {
			String strAbsFilename = strDestDir + File.separator + fileSource.getName();

			FileInputStream fileInput = new FileInputStream(strSourceFileName);
			FileOutputStream fileOutput = new FileOutputStream(strAbsFilename);

			logger.debug("开始拷贝文件");

			int count = -1;

			long nWriteSize = 0;
			long nFileSize = fileSource.length();

			byte[] data = new byte[BUFFER];

			while (-1 != (count = fileInput.read(data, 0, BUFFER))) {

				fileOutput.write(data, 0, count);

				nWriteSize += count;

				long size = (nWriteSize * 100) / nFileSize;
				long t = nWriteSize;

				String msg = null;

				if (size <= 100 && size >= 0) {
					msg = "\r拷贝文件进度:   " + size + "%   \t" + "\t   已拷贝:   " + t;
					logger.debug(msg);
				} else if (size > 100) {
					msg = "\r拷贝文件进度:   " + 100 + "%   \t" + "\t   已拷贝:   " + t;
					logger.debug(msg);
				}

			}

			fileInput.close();
			fileOutput.close();

			logger.debug("拷贝文件成功!");
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 功 能: 删除指定的文件 参 数: 指定绝对路径的文件名 strFileName 返回值: 如果删除成功true否则false;
	 *
	 * @param strFileName
	 * @return
	 */
	public static boolean delete(String strFileName) {
		File fileDelete = new File(strFileName);

		if (!fileDelete.exists() || !fileDelete.isFile()) {
			logger.debug(strFileName + "不存在!");
			return false;
		}

		return fileDelete.delete();
	}

	/**
	 * 功 能: 移动文件(只能移动文件) 参 数: strSourceFileName: 是指定的文件全路径名 strDestDir: 移动到指定的文件夹中 返回值: 如果成功true; 否则false
	 *
	 * @param strSourceFileName
	 * @param strDestDir
	 * @return
	 */
	public static boolean moveFile(String strSourceFileName, String strDestDir) {
		if (copyTo(strSourceFileName, strDestDir))
			return delete(strSourceFileName);
		else
			return false;
	}

	/**
	 * 功 能: 创建文件夹 参 数: strDir 要创建的文件夹名称 返回值: 如果成功true;否则false
	 *
	 * @param strDir
	 * @return
	 */
	public static boolean makeDir(String strDir) {
		File fileNew = new File(strDir);
        return fileNew.exists() || fileNew.mkdirs();
	}

	/**
	 * 功 能: 删除文件夹 参 数: strDir 要删除的文件夹名称 返回值: 如果成功true;否则false
	 *
	 * @param strDir
	 * @return
	 */
	public static boolean removeDir(String strDir) {
		File rmDir = new File(strDir);
		if (rmDir.isDirectory() && rmDir.exists()) {
			String[] fileList = rmDir.list();

			for (int i = 0; i < fileList.length; i++) {
				String subFile = strDir + File.separator + fileList[i];
				File tmp = new File(subFile);
				if (tmp.isFile())
					tmp.delete();
				else if (tmp.isDirectory())
					removeDir(subFile);
			}
			rmDir.delete();
		} else {
			return false;
		}
		return true;
	}

	/**
	 *
	* @Description: chmod
	 */
	public static void chmod(String strDir,int rwx){
		File file = new File(strDir);
		try {
			Runtime.getRuntime().exec("chmod " + rwx + " "+ file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	* @Description: chmod
	*/
	public static void chmodPatch(String strDir,int rwx){
		File file = new File(strDir);
		try {
			Runtime.getRuntime().exec("chmod -Rf " + rwx + " "+ file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public static void base64File(String context,String targetFile) throws IOException {
		//byte[] bytefs = org.apache.mina.util.Base64.decodeBase64(context.getBytes());
		byte[] bytes = com.fst.web.utils.Base64.decodeBase64(context.getBytes());
		File file = new File(targetFile);
		if(!file.exists()){
			file.getParentFile().mkdirs();
			file.createNewFile();
		}
		OutputStream out = new FileOutputStream(file);
        for (byte aByte : bytes) {
            out.write(aByte);
        }
		out.close();
	}


	public static String base64String (String fileName){
		File file = new File(fileName);
		if(!file.exists()){
			return null;
		}
		return Base64.encodeFromFile(fileName);
	}

	/**
	 *
	 * @Description: get date path ,eg. 2013/1/1
	 */
	public static String getDatePath(){
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH)+1;
		int day = c.get(Calendar.DAY_OF_MONTH);
		return "/"+year+"/"+month+"/"+day+"/";
	}

    public static void makeRoundedCorner(String origPath,String distPath,int cornerRadius) throws IOException {
        BufferedImage image = ImageIO.read(new File(origPath));
        if (image == null) return;
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage output = new BufferedImage(w, h,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        // This is what we want, but it only does hard-clipping, i.e. aliasing
        // g2.setClip(new RoundRectangle2D ...)
        // so instead fake soft-clipping by first drawing the desired clip shape
        // in fully opaque white with antialiasing enabled...
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        //g2.setColor(Color.WHITE);
        g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius,
                cornerRadius));
        // ... then compositing the image on top,
        // using the white shape from above as alpha source
        g2.setComposite(AlphaComposite.SrcAtop);
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
        ImageIO.write(output,"png",new File(distPath));
    }

    public static void main(String[] args) {
		/*System.out.println(base64String("/home/wanjun/Pictures/Selection_003.png"));
		try {
			base64File(base64String("/home/wanjun/Pictures/Selection_003.png"), "/home/wanjun/Pictures/Selection_003_.png");
		} catch (IOException e) {
			e.printStackTrace();
		}
        */
        try {
            makeRoundedCorner("/home/wanjun/ce2427c800a34df18e0e16c3cb45831f.png","/home/wanjun/test.png",50);
            makeRoundedCorner("/home/wanjun/a006.png","/home/wanjun/a.png",50);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
