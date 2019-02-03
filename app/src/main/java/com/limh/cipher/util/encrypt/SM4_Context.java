package com.limh.cipher.util.encrypt;

/**
 * Title：SM4_Context
 * Package：com.ks.msgencrypt.utils
 * Description：
 * <p>
 * Author：Algorithm_Terminator
 * Date：2017/10/17
 * <p>
 * Copyright @2017 Corpration Name
 */

class SM4_Context {
    int mode;

    long[] sk;

    boolean isPadding;

    SM4_Context() {
        this.mode = 1;
        this.isPadding = true;
        this.sk = new long[32];
    }
}
