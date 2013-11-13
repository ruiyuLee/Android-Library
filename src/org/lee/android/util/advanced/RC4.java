/*
 * Copyright (C) 2012 Baidu Inc. All rights reserved.
 */
package org.lee.android.util.advanced;

/**
 * RC4 (ARC4) 加密算法 java 实现。
 * 
 * RC4更多信息参考： <a href="http://zh.wikipedia.org/wiki/RC4">http://zh.wikipedia.org/wiki/RC4</a>
 *  
 * 
 * @since 2012-12-26
 */
public class RC4 {

    /** s-box 长度一般256 */
    private static final int STATE_LENGTH = 256;

    /**
     * variables to hold the state of the RC4 engine during encryption and
     * decryption
     */
    private byte[] engineState = null;
    
    /** 运算过程中临时变量 */
    private int x = 0;
    
    /** 运算过程中临时变量*/
    private int y = 0;
    
    /** 当前密钥  */
    private byte[] workingKey = null;

    /**
     * 构造函数。
     * @param key  加解密密钥。
     */
    public RC4(String key) {
        workingKey = key.getBytes();
    }

    /**
     * 加解密过程，rc4 加解密算法是个及其对称的算法，加解密使用同一套代码。
     * 
     * @param in  需要加（解）密数据
     * @param inOff  数据开始 offset
     * @param len 需要加解密的长度
     * @param out 输出
     * @param outOff 输出 offsite
     */
    private void processBytes(byte[] in, int inOff, int len, byte[] out, int outOff) {
        if ((inOff + len) > in.length) {
            throw new RuntimeException("input buffer too short");
        }

        if ((outOff + len) > out.length) {
            throw new RuntimeException("output buffer too short");
        }

        for (int i = 0; i < len; i++) {
            x = (x + 1) & 0xff; //SUPPRESS CHECKSTYLE
            y = (engineState[x] + y) & 0xff; //SUPPRESS CHECKSTYLE

            // swap
            byte tmp = engineState[x];
            engineState[x] = engineState[y];
            engineState[y] = tmp;

            // xor
            out[i + outOff] = (byte) (in[i + inOff] ^ engineState[(engineState[x] + engineState[y]) & 0xff]); //SUPPRESS CHECKSTYLE
        }
    }

    /**
     * 重新设置 key，并重置一些临时变量，加解密前需要调用该函数进行重置。
     * 
     * @param keyBytes 密钥
     */
    private void setKey(byte[] keyBytes) {

        x = 0;
        y = 0;

        if (engineState == null) {
            engineState = new byte[STATE_LENGTH];
        }

        // reset the state of the engine
        for (int i = 0; i < STATE_LENGTH; i++) {
            engineState[i] = (byte) i;
        }

        int i1 = 0;
        int i2 = 0;

        for (int i = 0; i < STATE_LENGTH; i++) {
            i2 = ((keyBytes[i1] & 0xff) + engineState[i] + i2) & 0xff; //SUPPRESS CHECKSTYLE
            // do the byte-swap inline
            byte tmp = engineState[i];
            engineState[i] = engineState[i2];
            engineState[i2] = tmp;
            i1 = (i1 + 1) % keyBytes.length;
        }
    }
    
    /**
     * 对运算过程中一些成员变量进行重置。加解密前需要调用该函数进行重置
     */
    private void reset() {
        setKey(workingKey);
    }
    
    /**
     * 加密。
     * @param data 需要加密的原始数据
     * @return 加密后数据
     */
    public byte[] encrypt(byte[] data) {
        
        reset();
        
        byte[] out = new byte[data.length];
        processBytes(data, 0, data.length, out, 0);
        
        return out;
    }
    
    /**
     * 解密。
     * @param data 需要解密的密文数据
     * @return 解密后的数据
     */
    public byte[] decrypt(byte[] data) {
        
        reset();
        
        byte[] out = new byte[data.length];
        processBytes(data, 0, data.length, out, 0);
        
        return out;
    }
}