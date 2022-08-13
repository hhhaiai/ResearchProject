//package me.sanbo.utils.neeme;
//
//import me.sanbo.utils.SafeClose;
//import me.sanbo.utils.TextUtils;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class PhoneShell {
//
//    public static List<String> getResultArray(String cmd) {
//        return (List<String>) getRealResult(true, cmd);
//    }
//
//    public static void shellCmd(String cmd) {
//        getResultString(cmd);
//    }
//
//    public static void shellCmds(String... cmd) {
//        getResultArrs(cmd);
//    }
//
//    public static String getResultString(String cmd) {
//        return getResultArrs(cmd);
//    }
//
//    public static String getResultArrs(String... cmd) {
//        return (String) getRealResult(false, cmd);
//    }
//
//    public static Object getRealResult(boolean isArrayResult, String... cmd) {
//        String strResult = "";
//        List<String> arrResult = new ArrayList<String>();
//        if (cmd == null || cmd.length == 0) {
//            if (isArrayResult) {
//                return arrResult;
//            } else {
//                return strResult;
//            }
//        }
//        Process proc = null;
//        BufferedInputStream in = null;
//        BufferedReader br = null;
//        InputStreamReader is = null;
//        InputStream ii = null;
//        StringBuilder sb = new StringBuilder();
//        DataOutputStream os = null;
//        OutputStream pos = null;
//        try {
//            proc = Runtime.getRuntime().exec("sh");
//            pos = proc.getOutputStream();
//            os = new DataOutputStream(pos);
//
//            for (int i = 0; i < cmd.length; i++) {
//                os.write(cmd[i].getBytes());
//                os.writeBytes("\n");
//                os.flush();
//            }
//
//            // exitValue
//            os.writeBytes("exit\n");
//            os.flush();
//            ii = proc.getInputStream();
//            in = new BufferedInputStream(ii);
//            is = new InputStreamReader(in);
//            br = new BufferedReader(is);
//            String line = "";
//            while ((line = br.readLine()) != null) {
//                if (!isArrayResult) {
//                    sb.append(line).append("\n");
//                } else {
//                    if (!TextUtils.isEmpty(line)) {
//                        arrResult.add(line);
//                    }
//                }
//            }
//            if (!isArrayResult) {
//                if (sb.length() > 0) {
//                    return sb.substring(0, sb.length() - 1);
//                }
//                strResult = String.valueOf(sb);
//                if (!TextUtils.isEmpty(strResult)) {
//                    strResult = strResult.trim();
//                }
//            }
//
//        } catch (Throwable e) {
//            e.printStackTrace();
//        } finally {
//            SafeClose.close(pos, ii, br, is, in, os);
//        }
//
//        if (isArrayResult) {
//            return arrResult;
//        } else {
//            return strResult;
//        }
//    }
//
//
//}
//package me.sanbo.utils.neeme;
//
//import me.sanbo.utils.SafeClose;
//import me.sanbo.utils.TextUtils;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//
//public class PhoneShell {
//
//    public static List<String> getResultArray(String cmd) {
//        return (List<String>) getRealResult(true, cmd);
//    }
//
//    public static void shellCmd(String cmd) {
//        getResultString(cmd);
//    }
//
//    public static void shellCmds(String... cmd) {
//        getResultArrs(cmd);
//    }
//
//    public static String getResultString(String cmd) {
//        return getResultArrs(cmd);
//    }
//
//    public static String getResultArrs(String... cmd) {
//        return (String) getRealResult(false, cmd);
//    }
//
//    public static Object getRealResult(boolean isArrayResult, String... cmd) {
//        String strResult = "";
//        List<String> arrResult = new ArrayList<String>();
//        if (cmd == null || cmd.length == 0) {
//            if (isArrayResult) {
//                return arrResult;
//            } else {
//                return strResult;
//            }
//        }
//        Process proc = null;
//        BufferedInputStream in = null;
//        BufferedReader br = null;
//        InputStreamReader is = null;
//        InputStream ii = null;
//        StringBuilder sb = new StringBuilder();
//        DataOutputStream os = null;
//        OutputStream pos = null;
//        try {
//            proc = Runtime.getRuntime().exec("sh");
//            pos = proc.getOutputStream();
//            os = new DataOutputStream(pos);
//
//            for (int i = 0; i < cmd.length; i++) {
//                os.write(cmd[i].getBytes());
//                os.writeBytes("\n");
//                os.flush();
//            }
//
//            // exitValue
//            os.writeBytes("exit\n");
//            os.flush();
//            ii = proc.getInputStream();
//            in = new BufferedInputStream(ii);
//            is = new InputStreamReader(in);
//            br = new BufferedReader(is);
//            String line = "";
//            while ((line = br.readLine()) != null) {
//                if (!isArrayResult) {
//                    sb.append(line).append("\n");
//                } else {
//                    if (!TextUtils.isEmpty(line)) {
//                        arrResult.add(line);
//                    }
//                }
//            }
//            if (!isArrayResult) {
//                if (sb.length() > 0) {
//                    return sb.substring(0, sb.length() - 1);
//                }
//                strResult = String.valueOf(sb);
//                if (!TextUtils.isEmpty(strResult)) {
//                    strResult = strResult.trim();
//                }
//            }
//
//        } catch (Throwable e) {
//            e.printStackTrace();
//        } finally {
//            SafeClose.close(pos, ii, br, is, in, os);
//        }
//
//        if (isArrayResult) {
//            return arrResult;
//        } else {
//            return strResult;
//        }
//    }
//
//
//}
