package me.sanbo.utils;

import java.io.Closeable;

public class SafeClose {


    public static void close(Closeable... obj) {
        if (obj != null && obj.length > 0) {
            for (Closeable close : obj) {

                try {
                    if (close != null)
                        close.close();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
