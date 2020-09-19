package com.gradel.parent.common.util.util;

import com.gradel.parent.common.util.constants.CommonConstants;
import com.gradel.parent.common.util.exception.CodecException;

import java.io.*;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/6/29
 * @Description:
 */
public abstract class CodecUtil {

    private static final int BUFFER_SIZE = 512;

    public static final String PREFERRED_ENCODING = CommonConstants.UTF8;

    public static byte[] toBytes(char[] chars) {
        return toBytes(new String(chars), PREFERRED_ENCODING);
    }

    public static byte[] toBytes(char[] chars, String encoding) throws CodecException {
        return toBytes(new String(chars), encoding);
    }

    public static byte[] toBytes(String source) {
        return toBytes(source, PREFERRED_ENCODING);
    }

    public static byte[] toBytes(String source, String encoding) throws CodecException {
        try {
            return source.getBytes(encoding);
        } catch (UnsupportedEncodingException e) {
            String msg = "Unable to convert source [" + source + "] to byte array using " +
                    "encoding '" + encoding + "'";
            throw new CodecException(msg, e);
        }
    }

    public static String toString(byte[] bytes) {
        return toString(bytes, PREFERRED_ENCODING);
    }

    public static String toString(byte[] bytes, String encoding) throws CodecException {
        try {
            return new String(bytes, encoding);
        } catch (UnsupportedEncodingException e) {
            String msg = "Unable to convert byte array to String with encoding '" + encoding + "'.";
            throw new CodecException(msg, e);
        }
    }

    public static char[] toChars(byte[] bytes) {
        return toChars(bytes, PREFERRED_ENCODING);
    }

    public static char[] toChars(byte[] bytes, String encoding) throws CodecException {
        return toString(bytes, encoding).toCharArray();
    }


    protected String toString(Object o) {
        if (o == null) {
            String msg = "Argument for String conversion cannot be null.";
            throw new IllegalArgumentException(msg);
        }
        if (o instanceof byte[]) {
            return toString((byte[]) o);
        } else if (o instanceof char[]) {
            return new String((char[]) o);
        } else if (o instanceof String) {
            return (String) o;
        } else {
            return objectToString(o);
        }
    }

    protected byte[] toBytes(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File argument cannot be null.");
        }
        try {
            return toBytes(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            String msg = "Unable to acquire InputStream for file [" + file + "]";
            throw new CodecException(msg, e);
        }
    }

    protected byte[] toBytes(InputStream in) {
        if (in == null) {
            throw new IllegalArgumentException("InputStream argument cannot be null.");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        try {
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            return out.toByteArray();
        } catch (IOException ioe) {
            throw new CodecException(ioe);
        } finally {
            try {
                in.close();
            } catch (IOException ignored) {
            }
            try {
                out.close();
            } catch (IOException ignored) {
            }
        }
    }

    protected String objectToString(Object o) {
        return o.toString();
    }
}
