package me.sanbo.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class DataConver {



    /**
     * 解析网络请求的返回值
     *
     * @param is
     * @return
     */
    public static String parserInputStreamToString(InputStream is) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[1024];
            int n = 0;
            while (-1 != (n = is.read(buffer))) {
                bos.write(buffer, 0, n);
            }

            return bos.toString("UTF-8");
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            SafeClose.close(bos);
        }
        return "";
    }


    /**
     * 输出流转化为字符串
     *
     * @param output
     * @return
     */
    public static String parserOutputStreamToString(OutputStream output) {
        ByteArrayOutputStream baos = null;
        ByteArrayInputStream swapStream = null;
        String result = null;
        try {
            baos = new ByteArrayOutputStream();
            baos = (ByteArrayOutputStream) output;
            swapStream = new ByteArrayInputStream(baos.toByteArray());
            result = swapStream.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SafeClose.close(baos, swapStream);
        }
        return result;
    }

}
