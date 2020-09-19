package com.gradel.parent.common.util.util;

import com.gradel.parent.common.util.threadlocal.SerialNo;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016-3-17
 */
@Slf4j
public abstract class FileUtil {

    //记录各个文件头信息及对应的文件类型

    public static Map<String, String> mFileTypes = new ConcurrentHashMap<>();

    static {
        // images
        mFileTypes.put("FFD8FFE0", "jpg");
        mFileTypes.put("89504E47", "png");
        mFileTypes.put("47494638", "gif");
        mFileTypes.put("49492A00", "tif");
        mFileTypes.put("424D", "bmp");

        //PS和CAD
        mFileTypes.put("38425053", "psd");
        mFileTypes.put("41433130", "dwg"); // CAD
        mFileTypes.put("252150532D41646F6265", "ps");

        //办公文档类
        mFileTypes.put("D0CF11E0", "doc"); //ppt、doc、xls
        mFileTypes.put("504B0304", "docx");//pptx、docx、xlsx

        /**注意由于文本文档录入内容过多，则读取文件头时较为多变-START**/
        mFileTypes.put("0D0A0D0A", "txt");//txt
        mFileTypes.put("0D0A2D2D", "txt");//txt
        mFileTypes.put("0D0AB4B4", "txt");//txt
        mFileTypes.put("B4B4BDA8", "txt");//文件头部为汉字
        mFileTypes.put("73646673", "txt");//txt,文件头部为英文字母
        mFileTypes.put("32323232", "txt");//txt,文件头部内容为数字
        mFileTypes.put("0D0A09B4", "txt");//txt,文件头部内容为数字
        mFileTypes.put("3132330D", "txt");//txt,文件头部内容为数字
        mFileTypes.put("C6BDCCA8", "txt");
        mFileTypes.put("B0B2C8AB", "txt");
        mFileTypes.put("EFBBBF23", "txt");


        /**注意由于文本文档录入内容过多，则读取文件头时较为多变-END**/

        mFileTypes.put("7B5C727466", "rtf"); // 日记本

        mFileTypes.put("255044462D312E", "pdf");

        //视频或音频类
        mFileTypes.put("3026B275", "wma");
        mFileTypes.put("57415645", "wav");
        mFileTypes.put("41564920", "avi");
        mFileTypes.put("4D546864", "mid");
        mFileTypes.put("2E524D46", "rm");
        mFileTypes.put("000001BA", "mpg");
        mFileTypes.put("000001B3", "mpg");
        mFileTypes.put("6D6F6F76", "mov");
        mFileTypes.put("3026B2758E66CF11", "asf");

        //压缩包
        mFileTypes.put("52617221", "rar");
        mFileTypes.put("1F8B08", "gz");

        //程序文件
        mFileTypes.put("3C3F786D6C", "xml");
        mFileTypes.put("68746D6C3E", "html");
        mFileTypes.put("7061636B", "java");
        mFileTypes.put("3C252D2D", "jsp");//3C252D2D 3C254020
        mFileTypes.put("4D5A9000", "exe");


        mFileTypes.put("44656C69766572792D646174653A", "eml"); // 邮件
        mFileTypes.put("5374616E64617264204A", "mdb");//Access数据库文件

        mFileTypes.put("46726F6D", "mht");
        mFileTypes.put("4D494D45", "mhtml");
    }

    /**
     * 根据文件的输入流获取文件头信息
     *
     * @param bytes 文件内容前4位
     * @return 文件头信息
     */
    public static String getFileType(byte[] bytes) {
        return mFileTypes.get(getFileHeader(bytes));
    }


    /**
     * 根据文件转换成的字节数组获取文件头信息
     *
     * @param bytes 文件内容前4位
     * @return 文件头信息
     */
    public static String getFileHeader(byte[] bytes) {
        String value = bytesToHexString(bytes);
        return value;
    }


    /**
     * 将要读取文件头信息的文件的byte数组转换成string类型表示
     * <p>
     * 下面这段代码就是用来对文件类型作验证的方法，
     * <p>
     * 将字节数组的前四位转换成16进制字符串，并且转换的时候，要先和0xFF做一次与运算。
     * <p>
     * 这是因为，整个文件流的字节数组中，有很多是负数，进行了与运算后，可以将前面的符号位都去掉，
     * <p>
     * 这样转换成的16进制字符串最多保留两位，如果是正数又小于10，那么转换后只有一位，
     * <p>
     * 需要在前面补0，这样做的目的是方便比较，取完前四位这个循环就可以终止了
     *
     * @param src 要读取文件头信息的文件的byte数组
     * @return 文件头信息
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (int i = 0; i < src.length; i++) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
            hv = Integer.toHexString(src[i] & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
        log.info("获取文件头信息:" + builder.toString());
        return builder.toString();
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

    /**
     * 创建文件夹
     *
     * @param dir           目录路径
     * @param ignoreIfExist true表示如果文件夹存在就不再创建了,false是重新创建
     * @throws IOException
     */
    public static void createDirs(String dir, boolean ignoreIfExist)
            throws IOException {
        File file = new File(dir);
        if (ignoreIfExist && file.exists()) {
            return;
        }
        if (!file.mkdirs()) {
            throw new IOException("Cannot create directories = " + dir);
        }
    }

    /**
     * 删除一个文件
     *
     * @param filePath 文件路径
     * @throws IOException
     */
    public static boolean deleteFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.isDirectory()) {
            throw new IOException(
                    "IOException -> BadInputException: not a file.");
        }
        if (!file.exists()) {
            throw new IOException(
                    "IOException -> BadInputException: file is not exist.");
        }
        if (!file.delete()) {
            throw new IOException("Cannot delete file. filename = " + filePath);
        }
        return true;
    }

    /**
     * 删除文件夹及其下面的子文件夹
     *
     * @param dir 目录路径
     * @throws IOException
     */
    public static void deleteDir(File dir) throws IOException {
        if (dir.isFile()) {
            throw new IOException(
                    "IOException -> BadInputException: not a directory.");
        }
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    file.delete();
                } else {
                    deleteDir(file);
                }
            }
        }// if
        dir.delete();
    }

    /**
     * 获取到目录下面文件的大小。包含了子目录。
     *
     * @param dir
     * @return 文件大小
     * @throws IOException
     */
    public static long getDirLength(File dir) throws IOException {
        if (dir.isFile()) {
            throw new IOException("BadInputException: not a directory.");
        }
        long size = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                long length = 0;
                if (file.isFile()) {
                    length = file.length();
                } else {
                    length = getDirLength(file);
                }
                size += length;
            }// for
        }// if
        return size;
    }

    /**
     * 将文件清空。
     *
     * @param srcFilename
     * @throws IOException
     */
    public static void emptyFile(String srcFilename) throws IOException {
        File srcFile = new File(srcFilename);
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Cannot find the file: " +
                    srcFile.getAbsolutePath());
        }
        if (!srcFile.canWrite()) {
            throw new IOException("Cannot write the file: " +
                    srcFile.getAbsolutePath());
        }

        FileOutputStream outputStream = new FileOutputStream(srcFilename);
        outputStream.close();
    }

    /**
     * 写文件。如果此文件不存在就创建一个。
     *
     * @param content      String
     * @param fileName     String
     * @param destEncoding String
     * @param append
     * @return fileName
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String writeFile(String content, String fileName,
                                   String destEncoding, boolean append) throws IOException {
        String targetFileName = null;
        File file = new File(fileName);
        if (!file.exists()) {
            // 先创建文件夹，存在就忽略
            createDirs(file.getParent(), true);
            if (!file.createNewFile()) {
                throw new IOException("create file '" + fileName + "' failure.");
            }
        }
        if (!file.isFile()) {
            throw new IOException("'" + fileName + "' is not a file.");
        }
        if (!file.canWrite()) {
            throw new IOException("'" + fileName + "' is a read-only file.");
        }
        targetFileName = file.getName(); // 获取文件名

        BufferedWriter out = null;
        try {
            FileOutputStream fos = new FileOutputStream(fileName, append);
            out = new BufferedWriter(new OutputStreamWriter(fos, destEncoding));
            out.write(content);
            out.flush();
        } finally {
            IOUtil.closeQuietly(out);
        }
        return targetFileName;
    }

    /**
     * 以字符为单位读取文件的内容，并将文件内容以字符串的形式返回。
     *
     * @param filePath
     * @return 字符串内容
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String readFileString(String filePath) throws IOException {

        File file = new File(filePath);
        if (!file.isFile()) {
            throw new IOException("'" + filePath + "' is not a file.");
        }

        BufferedReader reader = null;
        try {
            StringBuilder result = new StringBuilder(1024);
            FileInputStream fis = new FileInputStream(filePath);
            reader = new BufferedReader(new InputStreamReader(fis,
                    getCharset(file)));

            char[] block = new char[512];
            while (true) {
                int readLength = reader.read(block);
                if (readLength == -1) {
                    break;// end of file
                }
                result.append(block, 0, readLength);
            }
            return result.toString();
        } finally {
            IOUtil.closeQuietly(reader);
        }
    }

    /**
     * 以行为单位读取文件的内容，并将文件内容以字符串的形式返回。
     *
     * @param filePath
     * @return 字符串内容
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static String readFileLine(String filePath) throws IOException {

        File file = new File(filePath);
        if (!file.isFile()) {
            throw new IOException("'" + filePath + "' is not a file.");
        }

        BufferedReader reader = null;
        try {
            StringBuilder result = new StringBuilder(1024);
            FileInputStream fis = new FileInputStream(file);
            reader = new BufferedReader(new InputStreamReader(fis,
                    getCharset(file)));
            String line = null;
            // 一次读入一行，直到读入null为文件结束
            while ((line = reader.readLine()) != null) {
                // 显示行号
                result.append(line).append("\r\n");
            }
            return result.toString();
        } finally {
            IOUtil.closeQuietly(reader);
        }
    }

    /**
     * 从最后开始以行为单位读取文件指定行数的内容，并将文件行内容以字符串数组的形式返回。
     *
     * @param file
     * @param size
     * @return String[]
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static String[] getLastLines(File file, int size) throws IOException {
        List<String> list = new ArrayList<>();
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "r");
            long len = raf.length();
            long pos = len - 1;
            String line = null;
            int c = -1;
            while (pos > 0 && size > 0) {
                c = raf.read();
                if (c == '\n' || c == '\r') {
                    line = raf.readLine();
                    if (line != null) {
                        list.add(new String(line.getBytes("ISO-8859-1"),
                                "UTF-8"));
                    }
                    pos--;
                    size--;
                }
                pos--;
                raf.seek(pos);
                if (pos == 0) {// 当文件指针退至文件开始处，输出第一行
                    list.add(new String(raf.readLine().getBytes("ISO-8859-1"),
                            "UTF-8"));
                }
            }
            Collections.reverse(list); // 倒序
        } finally {
            IOUtil.closeQuietly(raf);
        }
        return list.toArray(new String[list.size()]);
    }

    /**
     * 单个文件拷贝(路径方式)
     *
     * @param srcFilename
     * @param destFilename
     * @param overwrite    是否覆盖目标文件
     * @throws IOException
     */
    public static void copyFile(String srcFilename, String destFilename,
                                boolean overwrite) throws IOException {

        File srcFile = new File(srcFilename);
        // 首先判断源文件是否存在
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Cannot find the source file: " +
                    srcFile.getAbsolutePath());
        }
        // 判断源文件是否可读
        if (!srcFile.canRead()) {
            throw new IOException("Cannot read the source file: " +
                    srcFile.getAbsolutePath());
        }

        File destFile = new File(destFilename);

        if (!overwrite) {
            // 目标文件存在就不覆盖
            if (destFile.exists()) {
                return;
            }
        } else {
            // 如果要覆盖已经存在的目标文件，首先判断是否目标文件可写。
            if (destFile.exists()) {
                if (!destFile.canWrite()) {
                    throw new IOException(
                            "Cannot write the destination file: " +
                                    destFile.getAbsolutePath());
                }
            } else {
                // 先创建文件夹，存在就忽略
                createDirs(destFile.getParent(), true);
                // 文件不存在就创建一个新的空文件。
                if (!destFile.createNewFile()) {
                    throw new IOException(
                            "Cannot write the destination file: " +
                                    destFile.getAbsolutePath());
                }
            }
        }

        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        byte[] block = new byte[1024];
        try {
            inputStream = new BufferedInputStream(new FileInputStream(srcFile));
            outputStream = new BufferedOutputStream(new FileOutputStream(
                    destFile));
            while (true) {
                int readLength = inputStream.read(block);
                if (readLength == -1) {
                    break;// end of file
                }
                outputStream.write(block, 0, readLength);
            }
        } finally {
            IOUtil.closeQuietly(inputStream);
            IOUtil.closeQuietly(outputStream);
        }
    }

    /**
     * 单个文件拷贝(文件方式)
     *
     * @param srcFile
     * @param destFile
     * @param overwrite 是否覆盖目标文件
     * @throws IOException
     */
    public static void copyFile(File srcFile, File destFile, boolean overwrite)
            throws IOException {

        // 首先判断源文件是否存在
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Cannot find the source file: " +
                    srcFile.getAbsolutePath());
        }
        // 判断源文件是否可读
        if (!srcFile.canRead()) {
            throw new IOException("Cannot read the source file: " +
                    srcFile.getAbsolutePath());
        }

        if (!overwrite) {
            // 目标文件存在就不覆盖
            if (destFile.exists()) {
                return;
            }
        } else {
            // 如果要覆盖已经存在的目标文件，首先判断是否目标文件可写。
            if (destFile.exists()) {
                if (!destFile.canWrite()) {
                    throw new IOException(
                            "Cannot write the destination file: " +
                                    destFile.getAbsolutePath());
                }
            } else {
                // 先创建文件夹，存在就忽略
                createDirs(destFile.getParent(), true);
                // 文件不存在就创建一个新的空文件。
                if (!destFile.createNewFile()) {
                    throw new IOException(
                            "Cannot write the destination file: " +
                                    destFile.getAbsolutePath());
                }
            }
        }

        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        byte[] block = new byte[1024];
        try {
            inputStream = new BufferedInputStream(new FileInputStream(srcFile));
            outputStream = new BufferedOutputStream(new FileOutputStream(
                    destFile));
            while (true) {
                int readLength = inputStream.read(block);
                if (readLength == -1) {
                    break;// end of file
                }
                outputStream.write(block, 0, readLength);
            }
        } finally {
            IOUtil.closeQuietly(inputStream);
            IOUtil.closeQuietly(outputStream);
        }
    }

    /**
     * 单个文件拷贝(文件方式)
     *
     * @param inputStream
     * @param destFilename
     * @param overwrite    是否覆盖目标文件
     * @throws IOException
     */
    public static void copyFile(InputStream inputStream, String destFilename,
                                boolean overwrite) throws IOException {

        File destFile = new File(destFilename);

        if (!overwrite) {
            // 目标文件存在就不覆盖
            if (destFile.exists()) {
                return;
            }
        } else {
            // 如果要覆盖已经存在的目标文件，首先判断是否目标文件可写。
            if (destFile.exists()) {
                if (!destFile.canWrite()) {
                    throw new IOException(
                            "Cannot write the destination file: " +
                                    destFile.getAbsolutePath());
                }
            } else {
                // 先创建文件夹，存在就忽略
                createDirs(destFile.getParent(), true);
                // 文件不存在就创建一个新的空文件。
                if (!destFile.createNewFile()) {
                    throw new IOException(
                            "Cannot write the destination file: " +
                                    destFile.getAbsolutePath());
                }
            }
        }

        BufferedInputStream bufferedInputStream = null;
        BufferedOutputStream outputStream = null;
        byte[] block = new byte[1024];
        try {
            bufferedInputStream = new BufferedInputStream(inputStream);
            outputStream = new BufferedOutputStream(new FileOutputStream(
                    destFile));
            while (true) {
                int readLength = bufferedInputStream.read(block);
                if (readLength == -1) {
                    break;// end of file
                }
                outputStream.write(block, 0, readLength);
            }
        } finally {
            IOUtil.closeQuietly(bufferedInputStream);
            IOUtil.closeQuietly(outputStream);
        }
    }

    /**
     * 拷贝文件，从源文件夹拷贝文件到目标文件夹。 <br>
     * 参数源文件夹和目标文件夹，最后都不要带文件路径符号，例如：C:/Path正确，C:/Path/错误。
     *
     * @param srcDirName  源文件夹名称 ,例如：C:/Path/file 或者C:\\Path\\file
     * @param destDirName 目标文件夹名称,例如：C:/Path/file 或者C:\\Path\\file
     * @param overwrite   是否覆盖目标文件夹下面的文件。
     * @throws IOException
     */
    public static void copyFiles(String srcDirName, String destDirName,
                                 boolean overwrite) throws IOException {
        File srcDir = new File(srcDirName);// 声明源文件夹
        // 首先判断源文件夹是否存在
        if (!srcDir.exists()) {
            throw new FileNotFoundException(
                    "Cannot find the source directory: " + srcDir.getAbsolutePath());
        }

        File destDir = new File(destDirName);
        if (!overwrite && !destDir.exists() && !destDir.mkdirs()) {
            throw new IOException(
                    "Cannot create the destination directories = " +
                            destDir);
        } else {
            // 覆盖存在的目标文件夹
            if (!destDir.exists() && !destDir.mkdirs()) {
                // create a new directory
                throw new IOException(
                        "Cannot create the destination directories = " +
                                destDir);
            }
        }

        // 循环查找源文件夹目录下面的文件（屏蔽子文件夹），然后将其拷贝到指定的目标文件夹下面。
        File[] srcFiles = srcDir.listFiles();
        if (srcFiles == null || srcFiles.length < 1) {
            // throw new IOException ("Cannot find any file from source
            // directory!!!");
            return;// do nothing
        }

        for (File srcFile : srcFiles) {
            File destFile = new File(destDirName + File.separator +
                    srcFile.getName());
            // 注意构造文件对象时候，文件名字符串中不能包含文件路径分隔符";".
            if (srcFile.isFile()) {
                copyFile(srcFile, destFile, overwrite);
            } else {
                // 在这里进行递归调用，就可以实现子文件夹的拷贝
                copyFiles(srcFile.getAbsolutePath(), destDirName +
                        File.separator + srcFile.getName(), overwrite);
            }
        }
    }

    /**
     * 压缩单个文件
     *
     * @param srcFilename
     * @param destFilename
     * @param overwrite
     * @throws IOException
     */
    public static void zipFile(String srcFilename, String destFilename,
                               boolean overwrite) throws IOException {

        File srcFile = new File(srcFilename);
        // 首先判断源文件是否存在
        if (!srcFile.exists()) {
            throw new FileNotFoundException("Cannot find the source file: " +
                    srcFile.getAbsolutePath());
        }
        // 判断源文件是否可读
        if (!srcFile.canRead()) {
            throw new IOException("Cannot read the source file: " +
                    srcFile.getAbsolutePath());
        }

        if (destFilename == null || destFilename.trim().equals("")) {
            destFilename = srcFilename + ".zip";
        } else {
            destFilename += ".zip";
        }
        File destFile = new File(destFilename);

        if (!overwrite) {
            // 目标文件存在就不覆盖
            if (destFile.exists()) {
                return;
            }
        } else {
            // 如果要覆盖已经存在的目标文件，首先判断是否目标文件可写。
            if (destFile.exists()) {
                if (!destFile.canWrite()) {
                    throw new IOException(
                            "Cannot write the destination file: " +
                                    destFile.getAbsolutePath());
                }
            } else {
                // 不存在就创建一个新的空文件。
                if (!destFile.createNewFile()) {
                    throw new IOException(
                            "Cannot write the destination file: " +
                                    destFile.getAbsolutePath());
                }
            }
        }

        BufferedInputStream inputStream = null;
        ZipOutputStream zipOutputStream = null;
        byte[] block = new byte[1024];
        try {
            inputStream = new BufferedInputStream(new FileInputStream(srcFile));
            zipOutputStream = new ZipOutputStream(
                    new FileOutputStream(destFile));

            zipOutputStream.setComment("通过java程序压缩的");
            ZipEntry zipEntry = new ZipEntry(srcFile.getName());
            zipEntry.setComment("zipEntry通过java程序压缩的");
            zipOutputStream.putNextEntry(zipEntry);
            while (true) {
                int readLength = inputStream.read(block);
                if (readLength == -1) {
                    break;// end of file
                }
                zipOutputStream.write(block, 0, readLength);
            }
            zipOutputStream.flush();
            zipOutputStream.finish();
        } finally {
            IOUtil.closeQuietly(inputStream);
            IOUtil.closeQuietly(zipOutputStream);
        }
    }

    /**
     * 根据文件得到该文件中文本内容的编码
     *
     * @param file 要分析的文件
     * @return 文件编码
     */
    public static String getCharset(File file) {
        String charset = "GBK"; // 默认编码
        byte[] first3Bytes = new byte[3];
        BufferedInputStream bis = null;
        try {
            boolean checked = false;
            bis = new BufferedInputStream(new FileInputStream(file));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1) {
                return charset;
            }
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE &&
                    first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF &&
                    first3Bytes[1] == (byte) 0xBB && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                while ((read = bis.read()) != -1) {
                    if (read >= 0xF0) {
                        break;
                    }
                    // 单独出现BF以下的，也算是GBK
                    if (0x80 <= read && read <= 0xBF) {
                        break;
                    }
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        // (0x80 -
                        // 0xBF),也可能在GB编码内
                        if (!(0x80 <= read && read <= 0xBF)) {
                            break;
                            // 也有可能出错，但是几率较小
                        }
                    } else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("[{}] FileUtil getCharset Exception:{}", SerialNo.getSerialNo(), ExceptionUtil.getAsString(e));
        } finally {
            IOUtil.closeQuietly(bis);
        }
        return charset;
    }

    /**
     * 取得文件扩展名(包含.分隔符)
     *
     * @param fileName
     * @return String
     */
    public static String getExtension(String fileName) {
        return fileName != null ? fileName.substring(fileName.lastIndexOf('.')).toLowerCase() : "";
    }

    /**
     * 取得文件扩展名(不包含.分隔符)
     *
     * @param fileName
     * @return String
     */
    public static String getExtensionNotDot(String fileName) {
        return fileName != null ? fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase() : "";
    }

    /**
     * 获取文件类型
     *
     * @param fileName
     * @return String
     */
    public static String getFileType(String fileName) {
        String fileType = null;
        fileName = fileName.toLowerCase(); // 转为小写
        if (fileName.endsWith(".jpeg") || fileName.endsWith(".jpg") ||
                fileName.endsWith(".gif") || fileName.endsWith(".png") ||
                fileName.endsWith(".bmp")) {
            fileType = "picture";
        } else if (fileName.endsWith(".3gp") || fileName.endsWith(".mov") ||
                fileName.endsWith(".mp4") || fileName.endsWith(".wmv") ||
                fileName.endsWith(".avi") || fileName.endsWith(".mpg") ||
                fileName.endsWith(".rm") || fileName.endsWith(".rmvb")) {
            fileType = "video";
        } else if (fileName.endsWith(".mp3") || fileName.endsWith(".wma")) {
            fileType = "audio";
        } else if (fileName.endsWith(".flv") || fileName.endsWith(".swf")) {
            fileType = "flash";
        } else if (fileName.endsWith(".html") || fileName.endsWith(".htm") ||
                fileName.endsWith(".jsp")) {
            fileType = "html";
        } else {
            fileType = "file";
        }
        return fileType;
    }

    /**
     * 根据文件路径字符串提取文件名称
     *
     * @param filePath 文件路径
     * @return fileName 文件名
     */
    public static String getFileName(String filePath) {
        String temp[] = filePath.replaceAll("\\\\", "/").split("/");
        String fileName = null;
        if (temp.length > 0) {
            fileName = temp[temp.length - 1];
        }
        return fileName;
    }

    /**
     * 根据文件路径字符串提取不包含扩展名的文件名称
     *
     * @param filePath 文件路径
     * @return fileName 文件名
     */
    public static String getNoExtFileName(String filePath) {
        String temp[] = filePath.replaceAll("\\\\", "/").split("/");
        String fileName = null;
        if (temp.length > 0) {
            fileName = temp[temp.length - 1].replace(
                    getExtension(temp[temp.length - 1]), "");
        }
        return fileName;
    }

    /**
     * 根据文件路径字符串提取文件(或文件夹)的父级路径
     *
     * @param filePath 文件路径
     * @return fileName 文件名
     */
    public static String getParentPath(String filePath) {
        String[] temp = filePath.replaceAll("\\\\", "/").split("/");
        StringBuilder paths = new StringBuilder();
        if (temp.length > 0) {
            for (int i = 0; i < temp.length - 1; i++) {
                paths.append(temp[i]).append(File.separator);
            }
        }
        return paths.toString();
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return 判断结果
     */
    public static boolean isExist(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 下载网络文件
     *
     * @param url          网络地址
     * @param destFilename 存储目标地址
     * @param overwrite    是否覆盖目标文件
     * @return 下载结果
     * @throws IOException
     */
    public static boolean downloadFile(String url, String destFilename,
                                       boolean overwrite) throws IOException {
        boolean result = false;
        File destFile = new File(destFilename);
        if (!overwrite) {
            // 目标文件存在就不覆盖
            if (destFile.exists()) {
                return result;
            }
        } else {
            // 如果要覆盖已经存在的目标文件，首先判断是否目标文件可写。
            if (destFile.exists()) {
                if (!destFile.canWrite()) {
                    throw new IOException(
                            "Cannot write the destination file: " +
                                    destFile.getAbsolutePath());
                }
            } else {
                // 先创建文件夹，存在就忽略
                createDirs(destFile.getParent(), true);
                // 文件不存在就创建一个新的空文件。
                if (!destFile.createNewFile()) {
                    throw new IOException(
                            "Cannot write the destination file: " +
                                    destFile.getAbsolutePath());
                }
            }
        }

        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            URL resource = new URL(url);
            HttpURLConnection httpConn = (HttpURLConnection) resource
                    .openConnection();
            httpConn.setConnectTimeout(1000 * 10);
            inputStream = httpConn.getInputStream();
            byte[] bs = new byte[1024];
            int len = 0;
            outputStream = new FileOutputStream(destFilename);
            // 开始读取
            while ((len = inputStream.read(bs)) != -1) {
                outputStream.write(bs, 0, len);
            }
            result = true;
        } catch (IOException e) {
            Object[] params = {SerialNo.getSerialNo(), url, destFilename, ExceptionUtil.getAsString(e)};
            log.error("[{}] DownloadFile url[{}], destFilename[{}] Exception:{}", params);
        } finally {
            IOUtil.closeQuietly(inputStream);
            IOUtil.closeQuietly(outputStream);
        }
        return result;
    }
}



