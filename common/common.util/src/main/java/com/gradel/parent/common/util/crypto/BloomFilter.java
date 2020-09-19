package com.gradel.parent.common.util.crypto;

import com.gradel.parent.common.util.util.StringUtil;
import com.gradel.parent.common.util.util.RandomUtil;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
* User: sdeven.chen.dongwei@gmail.com
 * @date: 2016/8/25
 * @Description:
 */
public class BloomFilter {
    /* BitSet初始分配2^20个bit = 128KB */
    private static final int DEFAULT_SIZE = 1 << 21;
    /* 不同哈希函数的种子，一般应取质数 */
    private static final int[] SEEDS = new int[]{5, 7, 11, 13, 31, 37, 61};

    private BitSet bits = new BitSet(DEFAULT_SIZE);
    /* 哈希函数对象 */
    private SimpleHash[] func = new SimpleHash[SEEDS.length];

    public BloomFilter() {
        for (int i = 0; i < SEEDS.length; i++) {
            func[i] = new SimpleHash(DEFAULT_SIZE, SEEDS[i]);
        }
    }

    // 将字符串标记到bits中
    public void add(String value) {
        for (SimpleHash f : func) {
            bits.set(f.hash(value), true);
        }
    }

    //判断字符串是否已经被bits标记
    public boolean contains(String value) {

        if (value == null) {
            return false;
        }
        boolean ret = true;
        for (SimpleHash f : func) {
            ret = ret && bits.get(f.hash(value));
        }
        return ret;
    }

    /* 哈希函数类 */
    public static class SimpleHash {
        private int cap;
        private int seed;

        public SimpleHash(int cap, int seed) {
            this.cap = cap;
            this.seed = seed;
        }

        //hash函数，采用简单的加权和hash
        public int hash(String value) {
            int result = 0;
            int len = value.length();
            for (int i = 0; i < len; i++) {
                result = seed * result + value.charAt(i);
            }
            return (cap - 1) & result;
        }
    }

    public BitSet getBits() {
        return bits;
    }

    public static void main(String[] ar){
        /*int count = 1;
        while(count-- > 0){
            test();
        }*/
        test();
    }

    static void test(){
        BloomFilter bloomFilter = new BloomFilter();
        System.out.println("=-----------------------=");
        long start = System.currentTimeMillis();
        int count = 10000;
        List<String> list = new ArrayList<>(count);
        while((count--) > 0){
            String uid = StringUtil.getUUID();
            bloomFilter.add(uid);
            list.add(uid);
        }
        System.out.println("=------cost time >> -----=" + (System.currentTimeMillis() - start)+"ms");

        long[] longs = bloomFilter.getBits().toLongArray();

        start = System.currentTimeMillis();
        System.out.println("=------not found -----= long len="+longs.length);
        count = 1000;

        while((count--) > 0){
            String uid = StringUtil.getUUID();
            boolean exist = bloomFilter.contains(uid);
            if(exist){
                System.out.println(uid +"="+exist);
            }
        }
        System.out.println("=------not found cost time >> -----=" + (System.currentTimeMillis() - start)+"ms");

        start = System.currentTimeMillis();
        System.out.println("=------found -----=");
        count = 100;
        while((count--) > 0){
            String uid = list.get(RandomUtil.nextInt(10000));
            boolean exist = bloomFilter.contains(uid);
            if(!exist){
                System.out.println(uid +"="+exist);
            }
        }
        System.out.println("=------found cost time >> -----=" + (System.currentTimeMillis() - start)+"ms");
    }
}
