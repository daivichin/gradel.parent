package com.gradel.parent.common.util.util;

import com.gradel.parent.common.util.api.error.CommonErrCodeEnum;
import com.gradel.parent.common.util.api.interfaces.ReadCallBack;
import com.gradel.parent.common.util.exception.SystemException;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.List;

/**
 * 这是对象序列化的程序
 * 实现了对象序列化的追加
 * 追加时，最好reset一下，否则有可能两边jvm读取出来的数据会报错，reset还可以解决长时间运行，导致内存泄漏问题
 * reset与不reset文件大小：reset大于不reset，因为reset之后要重新写入类描述
 * 从而保证两边读取的类结构与数据一致
 * <p>
 * reset注意点说明：
 * 在不调用 reset 的方式下，Java 的优化对于减轻 socket 开销还是很可观的，当然代价是有的，
 * 那就是直到你调用 reset 或者是关闭输出流之前，对于发送过的对象的实例是不会释放的。
 * 如果你的程序需要很长时间的运行，建议你还是调用 reset 避免最后内存溢出程序崩溃，
 * 但是如果你又要长时间运行，且发送的消息量又很大，那么调用 reset 无疑会增加开销，
 * 那么这个时候最好的做法我觉得是自己实现一套机制，定时的调用 reset 或者是定量，
 * 比如查看到内存已经涨到一个水平后调用一下，这样既可以避免内存无限的增长下去，又可以减少不少 socket 通信的开销。
 *
 * @author: sdeven.chen.dongwei@gmail.com
 */
@Slf4j
public class FileObjectUtil {

    /**
     * 写入对象到文件（覆盖写入）
     *
     * @param fileName     文件
     * @param serializable 对象
     * @return true：写入成功，否则失败
     */
    public static boolean writeToFile(String fileName, Object serializable) {
        return write(new File(fileName), serializable, false, false);
    }

    /**
     * 写入对象到文件（覆盖写入）
     *
     * @param fileName     文件
     * @param serializable 对象
     * @param reset        是否重新写入类描述等信息
     * @return true：写入成功，否则失败
     */
    public static boolean writeToFile(String fileName, Object serializable, boolean reset) {
        return write(new File(fileName), serializable, false, reset);
    }

    /**
     * 写入对象到文件（覆盖写入）
     *
     * @param file         文件
     * @param serializable 对象
     * @return true：写入成功，否则失败
     */
    public static boolean writeToFile(File file, Object serializable) {
        return write(file, serializable, false, false);
    }

    /**
     * 写入对象并追加到文件
     *
     * @param fileName     文件
     * @param serializable 对象
     * @return true：写入成功，否则失败
     */
    public static boolean writeAndAppendToFile(String fileName, Object serializable) {
        return write(new File(fileName), serializable, true, false);
    }

    /**
     * 写入对象并追加到文件
     *
     * @param fileName
     * @param serializable 对象
     * @param reset        是否重新写入类描述等信息
     * @return true：写入成功，否则失败
     */
    public static boolean writeAndAppendToFile(String fileName, Object serializable, boolean reset) {
        return write(new File(fileName), serializable, true, reset);
    }

    /**
     * 写入对象并追加到文件
     *
     * @param file
     * @param serializable 对象
     * @return true：写入成功，否则失败
     */
    public static boolean writeAndAppendToFile(File file, Object serializable) {
        return write(file, serializable, true, false);
    }

    /**
     * 把对象写入文件
     *
     * @param file         目标文件
     * @param serializable 对象
     * @param append       是否追加
     * @param reset        是否重新写入类描述等信息
     * @return true：写入成功，否则失败
     */
    public static boolean write(File file, Object serializable, boolean append, boolean reset) {
//        long start = System.currentTimeMillis();
        ObjectOutputStream oos = null;
        FileChannel channel = null;
        FileLock fileLock = null;
        boolean isexist = file.exists();//定义一个用来判断文件是否需要截掉头aced 0005的
        try {
            if (!isexist) {
                //createNewFile若返回true，则表示文件未存在并创建成功，否则表示文件已经存在
                file.createNewFile();
            }
            FileOutputStream fo = new FileOutputStream(file, append);
            channel = fo.getChannel();
            while (true) {
                try {
                    fileLock = channel.tryLock();
                } catch (OverlappingFileLockException e) {
                }
                if (fileLock != null) {
                    break;
                } else {
                    log.info("Other threads are the file[{}] operation, the current thread to sleep 100 milliseconds", file.getPath());
                    Thread.sleep(100);
                }
            }
            ObjectOutputStream out = new ObjectOutputStream(fo);
            if (append && channel.position() > 4) {//追加的时候去掉头部aced 0005
                long pos = channel.position() - 4;//追加的时候去掉头部aced 0005
                if (pos > 0) {
                    channel.truncate(pos);
                }
                if (reset) {
                    out.reset();
                }
            }
            out.writeObject(serializable);//进行序列化
            return true;
        } catch (Throwable e) {
            log.error("Write Object to file[{}], Exception:{}", file.getName(), ExceptionUtil.getAsString(e));
        } finally {
            if (fileLock != null) {
                try {
                    fileLock.release();
                } catch (IOException e) {
                    log.error("File[{}] release lock Exception:{}", file.getPath(), ExceptionUtil.getAsString(e));
                }
            }
            IOUtil.closeQuietly(channel);
            IOUtil.closeQuietly(oos);
        }
//        log.info("Object serialization is successful, cost time>>" + (System.currentTimeMillis() - start));
        return false;
    }

    /**
     * 从文件中读取对象
     *
     * @param fileName
     * @return 对象集合
     */
    public static List<Object> readListFromFile(String fileName) {
        return readListFromFile(new File(fileName));
    }

    /**
     * 从文件中读取对象
     *
     * @param file
     * @return 对象集合
     */
    public static List<Object> readListFromFile(File file) {
        List<Object> dataList = new ArrayList<>();
        readFromFile(file, (o, hasNext) -> dataList.add(o));
        return dataList;
    }

    /**
     * 从文件中读取对象
     *
     * @param fileName
     * @param callBack
     */
    public static void readFromFile(String fileName, ReadCallBack<Object> callBack) {
        readFromFile(new File(fileName), callBack);
    }

    /**
     * 从文件中读取对象
     *
     * @param file
     * @param callBack
     */
    public static void readFromFile(File file, ReadCallBack<Object> callBack) {
        long start = System.currentTimeMillis();
        if (file.exists()) {
            FileInputStream fn;
            ObjectInputStream ois = null;
            try {
                fn = new FileInputStream(file);
                ois = new ObjectInputStream(fn);
                boolean hasNext = fn.available() > 0;
                while (hasNext) {
                    Object o = ois.readObject();
                    hasNext = fn.available() > 0;
                    callBack.callBack(o, hasNext);
                }
            } catch (Throwable e) {
                log.error("Read Object from file[{}], Exception:{}", file.getName(), ExceptionUtil.getAsString(e));
                throw new SystemException(CommonErrCodeEnum.STREAM_TO_OBJECT_ERROR, e);
            } finally {
                IOUtil.closeQuietly(ois);

            }
        }
        log.info("Object deserialization is successful, cost time>>" + (System.currentTimeMillis() - start));
    }

}
