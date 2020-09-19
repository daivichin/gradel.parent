package com.gradel.parent.common.util.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FileType {
	
	public static final String JPG        = "JPG";
	public static final String JPEG        = "JPEG";
	public static final String GIF        = "GIF";
	public static final String ICO        = "ICO";
	public static final String ICON        = "ICON";
    public static final String PNG        = "PNG";
    public static final String ZIP        = "ZIP";
    public static final String TXT        = "TXT";
    public static final Long MB1		= 1L * 1024 * 1024;
    public static final Long MB2		= 2L * 1024 * 1024;
    public static final Long MB3		= 3L * 1024 * 1024;
    public static final Long MB4        = 4L * 1024 * 1024;
    public static final Long MB5        = 5L * 1024 * 1024;
    public static final Long MB10        = 10L * 1024 * 1024;
    
    public final static Map<String, String> FILE_TYPE_MAP = new HashMap<String, String>(); 
    static{     
        initAllFileType(); //初始化文件类型信息     
    } 
    private static void initAllFileType()     
    {     
        FILE_TYPE_MAP.put(      "FFD8FF", JPG);
        FILE_TYPE_MAP.put(    "89504E47", PNG);
        FILE_TYPE_MAP.put(    "504B0304", ZIP);    
    }
    private FileType(){}     
    
    
    
    public static String getFileType(byte[] bytes){
        byte[] header = Arrays.copyOfRange(bytes, 0, 10);
        String fileCode = ByteUtil.bytesToHexString(header);
        
        //这种方法在字典的头代码不够位数的时候可以用但是速度相对慢一点
        for (String code : FILE_TYPE_MAP.keySet()) {
            assert fileCode != null;
            if (fileCode.startsWith(code)) {
                return FILE_TYPE_MAP.get(code);
            }
        }
        return null;
    }
    
    public static boolean isFileType(byte[] bytes, String type) {
    	return null !=type && type.equals(getFileType(bytes));
    }
    
    
    
}