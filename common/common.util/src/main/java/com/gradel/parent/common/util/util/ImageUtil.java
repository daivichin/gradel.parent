package com.gradel.parent.common.util.util;

import com.gradel.parent.common.util.threadlocal.SerialNo;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public final class ImageUtil {
    /**
     * 图片水印
     *
     * @param pressImg  水印图片
     * @param targetImg 目标图片
     * @param x         修正值 默认在中间
     * @param y         修正值 默认在中间
     * @param alpha     透明度
     */
    public final static boolean pressImage(String pressImg, String targetImg, int x, int y, float alpha) {
        try {
            File img = new File(targetImg);
            Image src = ImageIO.read(img);
            int wideth = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(wideth, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, wideth, height, null);
            //水印文件   
            Image src_biao = ImageIO.read(new File(pressImg));
            int wideth_biao = src_biao.getWidth(null);
            int height_biao = src_biao.getHeight(null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
            g.drawImage(src_biao, (wideth - wideth_biao), (height - height_biao), wideth_biao, height_biao, null);
            //水印文件结束   
            g.dispose();
            ImageIO.write(image, "jpg", img);
            return true;
        } catch (Exception e) {
            log.error("[{}] ImageUtils Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
        }
        return false;
    }

    /**
     * 文字水印
     *
     * @param pressText 水印文字
     * @param targetImg 目标图片
     */
    public static boolean pressText(String pressText, String targetImg) {
        try {
            File img = new File(targetImg);
            Image src = ImageIO.read(img);
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);
            g.setColor(new Color(233, 230, 228));
            g.setFont(new Font("微软雅黑", Font.BOLD, 30));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.5f));
            g.rotate(Math.PI / 4, 150, 150);
            //g.drawString(pressText, (width - (getLength(pressText) * fontSize)) / 2 + x, (height - fontSize) / 2 + y);   
            // 循环打印文字水印
            float xpos = 0;
            float ypos = 0;
            for (int i = -width / 2; i < width * 1.5; i += 400) {
                xpos = i;
                for (int j = -height / 2; j < height * 1.5; j += 150) {
                    ypos = j;
                    g.drawString(pressText, xpos, ypos);
                }
            }

            //g.getTransform().invert();
            g.rotate(-Math.PI / 4, 150, 150);
            //g.drawString(pressText, (width - (getLength(pressText) * fontSize)) / 2 + x, (height - fontSize) / 2 + y);   
            g.setColor(new Color(147, 140, 122));
            g.setFont(new Font("微软雅黑", Font.BOLD, 30));
            g.drawString("2016-07-07 10:22:20", 220, 866);   //生成时间


            g.setColor(new Color(154, 132, 119));
            g.setFont(new Font("微软雅黑", Font.BOLD, 20));
            g.drawString("156145941146134421", 530, 356);         //授权编号
            g.drawString("雷小兰", 390, 409);    //姓名
            g.drawString("CL79895997", 390, 449);      //微信号
            g.drawString("雷小兰(核心合伙人)", 665, 434);        //类型
            g.drawString("纾雅花茵美、雅顺", 630, 496);        //许可内容

            g.drawString("2016-08-01".split("-")[0], 870, 569); //开始时间-年
            g.drawString("2016-08-01".split("-")[1], 990, 569);//开始时间-月
            g.drawString("2016-08-01".split("-")[2], 1100, 569); //开始时间-日

            g.drawString("2017-08-10".split("-")[0], 870, 611);   //结束时间-年
            g.drawString("2017-08-10".split("-")[1], 990, 611);  //结束时间-月
            g.drawString("2017-08-10".split("-")[2], 1100, 611);   //结束时间-日
            g.dispose();
            ImageIO.write(image, "jpg", img);
            return true;
        } catch (Exception e) {
            log.error("[{}] ImageUtils Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
        }
        return false;
    }

    /**
     * 缩放
     *
     * @param filePath 图片路径
     * @param height   高度
     * @param width    宽度
     * @param bb       比例不对时是否需要补白
     */
    public static boolean resize(String filePath, int height, int width, boolean bb) {
        try {
            double ratio = 0.0; //缩放比例    
            File f = new File(filePath);
            BufferedImage bi = ImageIO.read(f);
            Image itemp = bi.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH);
            //计算比例   
            if ((bi.getHeight() > height) || (bi.getWidth() > width)) {
                if (bi.getHeight() > bi.getWidth()) {
                    ratio = Integer.valueOf(height).doubleValue() / bi.getHeight();
                } else {
                    ratio = Integer.valueOf(width).doubleValue() / bi.getWidth();
                }
                AffineTransformOp op = new AffineTransformOp(AffineTransform.getScaleInstance(ratio, ratio), null);
                itemp = op.filter(bi, null);
            }
            if (bb) {
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = image.createGraphics();
                g.setColor(Color.white);
                g.fillRect(0, 0, width, height);
                if (width == itemp.getWidth(null)) {
                    g.drawImage(itemp, 0, (height - itemp.getHeight(null)) / 2, itemp.getWidth(null), itemp.getHeight(null), Color.white, null);
                } else {
                    g.drawImage(itemp, (width - itemp.getWidth(null)) / 2, 0, itemp.getWidth(null), itemp.getHeight(null), Color.white, null);
                }
                g.dispose();
                itemp = image;
            }
            ImageIO.write((BufferedImage) itemp, "jpg", f);
            return true;
        } catch (IOException e) {
            log.error("[{}] ImageUtils Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
        }
        return false;
    }

    /**
     * 图形验证码
     *
     * @param buffImage
     * @return 验证码
     */
    public static String getImageCode(BufferedImage buffImage) {
        Graphics g = buffImage.getGraphics();
        // 随机产生一个比较接近白色的底色
        g.setColor(getRandColor(230, 250));
        // 填充底色
        g.fillRect(0, 0, 70, 20);
        // 增加干扰线
        Random r = new Random();
        for (int i = 0; i < 133; i++) {
            g.setColor(getRandColor(150, 210));
            int x1 = r.nextInt(70);
            int y1 = r.nextInt(20);
            int x2 = r.nextInt(12);
            int y2 = r.nextInt(12);
            g.drawLine(x1, y1, x2 + x1, y2 + y1);
        }

        StringBuilder regCodeStrBuilder = new StringBuilder(5);
        g.setFont(new Font("Times New Roman", Font.BOLD, 18));
        // 产生5个验证字符
        for (int i = 0; i < 5; i++) {
            g.setColor(getRandColor(50, 150));
            int num = getCode();
            if (num > 10) { // 返回的是字符
                char temp = (char) num;
                regCodeStrBuilder.append(temp);
                g.drawString(String.valueOf(temp), 13 * i + 6, 16);
            } else {// 返回的是数字
                regCodeStrBuilder.append(num);
                g.drawString(String.valueOf(num), 13 * i + 6, 16);
            }
        }
        return regCodeStrBuilder.toString();
    }

    /**
     * @param low
     * @param high
     * @return Color
     */
    private static Color getRandColor(int low, int high) {
        Random ran = new Random();
        int r = low + ran.nextInt(high - low);
        int g = low + ran.nextInt(high - low);
        int b = low + ran.nextInt(high - low);
        return new Color(r, g, b);
    }

    /**
     * @return int
     */
    private static int getCode() {
        /*
         * 1 : 0-9 2 : a-z 3 : A-Z
         */
        Random r = new Random();
        int code = 0;

        int flag = r.nextInt(3);
        if (flag == 0) {
            code = r.nextInt(10);
        } else if (flag == 1) {
            code = getCodeByScope('a', 'z');
        } else {
            code = getCodeByScope('A', 'Z');
        }
        return code;
    }

    /**
     * @param low
     * @param high
     * @return int
     */
    private static int getCodeByScope(int low, int high) {
        Random ran = new Random();
        return low + ran.nextInt(high - low);
    }

    /**
     * 图片加边框
     *
     * @param image
     * @param frame
     * @return BufferedImage
     */
    public static BufferedImage makeFrame(BufferedImage image, String frame) {
        BufferedImage newImage = null;
        try {
            // 水印文件
            File file = new File(frame);
            BufferedImage frameImage = ImageIO.read(file);
            int frameWidth = frameImage.getWidth();
            int frameHeight = frameImage.getHeight();
            Graphics2D g2d = frameImage.createGraphics();
            newImage = g2d.getDeviceConfiguration().createCompatibleImage(
                    frameWidth, frameHeight, Transparency.TRANSLUCENT);
            g2d.dispose();

            // 目标加水印
            int imgWidth = image.getWidth();
            int imgHeight = image.getHeight();
            g2d = newImage.createGraphics();
            g2d.drawImage(frameImage, 0, 0, frameWidth, frameHeight, null);
            g2d.drawImage(image, (frameWidth - imgWidth) / 2, (frameHeight -
                    imgHeight - 1) / 2, imgWidth, imgHeight, null);
            g2d.dispose();
        } catch (Exception e) {
            log.error("[{}] ImageUtils Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
        }
        return newImage;
    }

    /**
     * 图片加水印
     *
     * @param image
     * @param watermark
     * @return BufferedImage
     */
    public static BufferedImage makeWatermark(BufferedImage image,
                                              String watermark) {
        try {
            // 目标文件
            int width = image.getWidth();
            int height = image.getHeight();
            Graphics2D g2d = image.createGraphics();
            g2d.drawImage(image, 0, 0, width, height, null);

            // 目标加水印
            File wmFile = new File(watermark);
            Image wmImage = ImageIO.read(wmFile);
            int wmWidth = wmImage.getWidth(null);
            int wmHeight = wmImage.getHeight(null);
            g2d.drawImage(wmImage, (width - wmWidth) / 2,
                    (height - wmHeight) / 2, wmWidth, wmHeight, null);
            g2d.dispose();
        } catch (Exception e) {
            log.error("[{}] ImageUtils Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
        }
        return image;
    }

    /**
     * 下载网络图片到本地
     *
     * @param url      图片地址
     * @param destPath 存储路径
     * @return 结果
     */
    public static boolean downloadImage(String url, String destPath) {
        boolean result = false;
        BufferedInputStream bis = null;
        OutputStream bos = null;
        try {
            File file = new File(destPath);
            FileUtil.createDirs(file.getParent(), true); // 创建文件夹

            URL _url = new URL(url);
            bis = new BufferedInputStream(_url.openStream());
            bos = new FileOutputStream(file);
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = bis.read(bytes)) > 0) {
                bos.write(bytes, 0, len);
            }
            result = true;
        } catch (IOException e) {
            log.error("[{}] ImageUtils Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.flush();
                    bos.close();
                }
            } catch (IOException e) {
                log.error("[{}] ImageUtils Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
            }
        }
        return result;
    }

    /**
     * 根据图片文件路径生成图片缩略图
     *
     * @param imagePath 原图片路径
     * @param thumbPath 缩略图路径
     * @param width     生成图片宽度
     * @param height    生成图片高度
     * @param mode      模式(1.补白)
     * @return 结果
     */
    public static boolean makeFileThumb(String imagePath, String thumbPath,
                                        int width, int height, int mode) {
        boolean result = false;
        try {
            BufferedImage srcImage = ImageIO.read(new File(imagePath)); // 构造Image对象
            if (thumbPath.toLowerCase().endsWith("png")) {
                result = makePngThumb(srcImage, thumbPath, width, height, mode);
            } else {
                result = makeJpgThumb(srcImage, thumbPath, width, height, mode);
            }
        } catch (IOException e) {
            log.error("[{}] ImageUtils Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
        }
        return result;
    }

    /**
     * 根据图片网络地址生成图片缩略图
     *
     * @param url       图片地址
     * @param thumbPath 缩略图路径
     * @param width     生成图片宽度
     * @param height    生成图片高度
     * @param mode      模式(1.补白)
     * @return 结果
     */
    public static boolean makeUrlThumb(String url, String thumbPath, int width,
                                       int height, int mode) {
        boolean result = false;
        try {
            URL _url = new URL(url);
            HttpURLConnection httpConn = (HttpURLConnection) _url
                    .openConnection();
            httpConn.setDoOutput(false); // 设置是否输出流，因为这个是GET请求，所以设为false,
            // 默认情况下是false
            httpConn.setDoInput(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            // 判断请求状态，获取返回数据
            if (httpConn.getResponseCode() == 200) {
                BufferedImage srcImage = ImageIO
                        .read(httpConn.getInputStream());
                if (thumbPath.toLowerCase().endsWith("png")) {
                    result = makePngThumb(srcImage, thumbPath, width, height,
                            mode);
                } else {
                    result = makeJpgThumb(srcImage, thumbPath, width, height,
                            mode);
                }
            }
        } catch (IOException e) {
            log.error("[{}] ImageUtils Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
        }
        return result;
    }

    /**
     * 生成JPG图片缩略图
     *
     * @param srcImage  源图形图像
     * @param thumbPath 缩略图路径
     * @param width     生成图片宽度
     * @param height    生成图片高度
     * @param mode      模式(1.补白)
     * @return 结果
     */
    private static boolean makeJpgThumb(BufferedImage srcImage,
                                        String thumbPath, int width, int height, int mode) {
        boolean result = false;
        try {
            int oldWidth = srcImage.getWidth(null); // 原图宽
            int oldHeight = srcImage.getHeight(null); // 原图高

            // 计算宽高比例
            float scaleWidth = 1f;
            float scaleHeight = 1f;

            if (width > 0 && height > 0) {
                if (mode == 1) {
                    if (oldWidth > width && oldHeight > height) {
                        scaleWidth = width / (oldWidth * 1f);
                        scaleHeight = height / (oldHeight * 1f);
                    } else if (oldWidth > width) {
                        scaleWidth = width / (oldWidth * 1f);
                        scaleHeight = scaleWidth;
                    } else if (oldHeight > height) {
                        scaleHeight = height / (oldHeight * 1f);
                        scaleWidth = scaleHeight;
                    } else {
                        scaleWidth = width / (oldWidth * 1f);
                        scaleHeight = height / (oldHeight * 1f);
                    }
                } else {
                    scaleWidth = width / (oldWidth * 1f);
                    scaleHeight = height / (oldHeight * 1f);
                }
            } else if (width > 0) {
                scaleWidth = width / (oldWidth * 1f);
                scaleHeight = scaleWidth;
            } else if (height > 0) {
                scaleHeight = height / (oldHeight * 1f);
                scaleWidth = scaleHeight;
            }

            // 转为整型比例(四舍五入)
            int newWidth = Math.round(oldWidth * scaleWidth);
            int newHeight = Math.round(oldHeight * scaleHeight);

            BufferedImage newImage = null;
            if (mode == 1) {
                newImage = new BufferedImage(width, height,
                        BufferedImage.TYPE_INT_RGB); // 初始化一个图像
                Graphics2D g2d = newImage.createGraphics();
                g2d.setBackground(Color.WHITE);
                g2d.clearRect(0, 0, width, height); // 使用当前绘图表面的背景色进行填充来清除指定的矩形
                g2d.dispose(); // 释放图像资源
                newImage.getGraphics().drawImage(
                        srcImage.getScaledInstance(newWidth, newHeight,
                                Image.SCALE_SMOOTH), (width - newWidth) / 2,
                        (height - newHeight - 1) / 2, newWidth, newHeight,
                        Color.WHITE, null); // 合并缩略图与底图
            } else {
                newImage = new BufferedImage(newWidth, newHeight,
                        BufferedImage.TYPE_INT_RGB); // 初始化一个图像
                newImage.getGraphics().drawImage(
                        srcImage.getScaledInstance(newWidth, newHeight,
                                Image.SCALE_SMOOTH), 0, 0, Color.WHITE, null);
            }

            File file = new File(thumbPath);
            FileUtil.createDirs(file.getParent(), true); // 先创建文件夹
            ImageIO.write(newImage, "jpg", file);
            result = true;
        } catch (IOException e) {
            log.error("[{}] ImageUtils Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
        }
        return result;
    }

    /**
     * 生成PNG图片缩略图，支持背景透明
     *
     * @param srcImage  源图形图像
     * @param thumbPath 缩略图路径
     * @param width     生成图片宽度
     * @param height    生成图片高度
     * @param mode      模式(1.补白)
     * @return 结果
     */
    private static boolean makePngThumb(BufferedImage srcImage,
                                        String thumbPath, int width, int height, int mode) {
        boolean result = false;
        try {
            int oldWidth = srcImage.getWidth(null); // 原图宽
            int oldHeight = srcImage.getHeight(null); // 原图高

            // 计算宽高比例
            float scaleWidth = 1f;
            float scaleHeight = 1f;

            if (width > 0 && height > 0) {
                if (mode == 1) {
                    if (oldWidth > width && oldHeight > height) {
                        scaleWidth = width / (oldWidth * 1f);
                        scaleHeight = height / (oldHeight * 1f);
                    } else if (oldWidth > width) {
                        scaleWidth = width / (oldWidth * 1f);
                        scaleHeight = scaleWidth;
                    } else if (oldHeight > height) {
                        scaleHeight = height / (oldHeight * 1f);
                        scaleWidth = scaleHeight;
                    } else {
                        scaleWidth = width / (oldWidth * 1f);
                        scaleHeight = height / (oldHeight * 1f);
                    }
                } else {
                    scaleWidth = width / (oldWidth * 1f);
                    scaleHeight = height / (oldHeight * 1f);
                }
            } else if (width > 0) {
                scaleWidth = width / (oldWidth * 1f);
                scaleHeight = scaleWidth;
            } else if (height > 0) {
                scaleHeight = height / (oldHeight * 1f);
                scaleWidth = scaleHeight;
            }

            // 转为整型比例(四舍五入)
            int newWidth = Math.round(oldWidth * scaleWidth);
            int newHeight = Math.round(oldHeight * scaleHeight);

            BufferedImage newImage = null;
            if (mode == 1) {
                Graphics2D g2d = srcImage.createGraphics();
                newImage = g2d.getDeviceConfiguration().createCompatibleImage(
                        width, height, Transparency.TRANSLUCENT); // 创建一个透明图像
                g2d.setBackground(Color.WHITE);
                g2d.clearRect(0, 0, width, height); // 使用当前绘图表面的背景色进行填充来清除指定的矩形
                g2d.dispose(); // 释放图像资源
                newImage.getGraphics().drawImage(
                        srcImage.getScaledInstance(newWidth, newHeight,
                                Image.SCALE_SMOOTH), (width - newWidth) / 2,
                        (height - newHeight - 1) / 2, newWidth, newHeight,
                        Color.WHITE, null); // 合并缩略图与底图
            } else {
                Graphics2D g2d = srcImage.createGraphics();
                newImage = g2d.getDeviceConfiguration().createCompatibleImage(
                        newWidth, newHeight, Transparency.TRANSLUCENT); // 创建一个透明图像
                g2d.dispose(); // 释放图像资源
                newImage.getGraphics().drawImage(
                        srcImage.getScaledInstance(newWidth, newHeight,
                                Image.SCALE_SMOOTH), 0, 0, null); // 合并缩略图与底图
            }
            File file = new File(thumbPath);
            FileUtil.createDirs(file.getParent(), true); // 先创建文件夹
            ImageIO.write(newImage, "png", file);
            result = true;
        } catch (IOException e) {
            log.error("[{}] ImageUtils Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
        }
        return result;
    }

    /**
     * 获取图片规格(宽x高)
     *
     * @param is 输入流
     * @return 验证码
     */
    public static String getSpec(InputStream is) {
        String spec = null;
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(is);
            spec = bi.getWidth() + "x" + bi.getHeight();// 宽x高
        } catch (IOException e) {
            log.error("[{}] ImageUtils Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
        } finally {
            if (bi != null) {
                bi.flush();
            }
        }
        return spec;
    }

    /**
     * 获取图片规格(宽x高)
     *
     * @param file 输入流
     * @return 验证码
     */
    public static String getSpec(File file) {
        FileInputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            log.error("[{}] ImageUtils Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e1));
        }
        String spec = null;
        BufferedImage bi = null;
        try {
            bi = ImageIO.read(is);
            spec = bi.getWidth() + "x" + bi.getHeight();// 宽x高
        } catch (IOException e) {
            log.error("[{}] ImageUtils Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
        } finally {
            if (bi != null) {
                bi.flush();
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    log.error("[{}] ImageUtils Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
                }
            }
        }
        return spec;
    }

    /**
     * 获取HTML中图片列表
     *
     * @param html
     * @return 图片列表
     */
    public static java.util.List<String> getImgSrc(String html) {
        Pattern pattern = Pattern.compile(
                "<img\\s+(?:[^>]*)src\\s*=\\s*([^>]+)", Pattern.CASE_INSENSITIVE |
                        Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(html);
        java.util.List<String> list = new ArrayList<>();
        while (matcher.find()) {
            String group = matcher.group(1);
            if (group != null) {
                if (group.startsWith("'")) {
                    list.add(group.substring(1, group.indexOf('\'', 1)));
                } else if (group.startsWith("\"")) {
                    list.add(group.substring(1, group.indexOf('"', 1)));
                } else {
                    list.add(group.split("\\s")[0]);
                }
            }
        }
        return list;
    }

    public static void main(String[] args) {
        //pressImage("D:\\xinweishanglicence.jpg", "f:\\imgtest\\test1.jpg", 0, 0, 1f);   
        pressText("雷小兰:CL79895997", "D:\\xinweishanglicence.jpg");
        //resize("G:\\imgtest\\test1.jpg", 500, 500, true);
    }

    public static int getLength(String text) {
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            if (String.valueOf(text.charAt(i)).getBytes().length > 1) {
                length += 2;
            } else {
                length += 1;
            }
        }
        return length / 2;
    }
}  
