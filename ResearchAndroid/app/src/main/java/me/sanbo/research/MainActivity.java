package me.sanbo.research;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Closeable;
import java.net.HttpURLConnection;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ff.jnezha.jnt.cs.GithubHelper;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "sanbo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    /**
     * 打开USM设置
     */
    public void openUSMSetting(View view) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                // fix need activity.startActivity
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    intent.setData(Uri.fromParts("package", getPackageName(), null));
//                }
                startActivity(intent);
            }
        } catch (Throwable e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }
    }

    /**
     * 开始工作
     * @param view
     */
    public void usmWork(View view) {
//        getAllDataPlanB(0, System.currentTimeMillis());
        getAllDataUs(0, System.currentTimeMillis());
    }


    private void getAllDataPlanB(long beginTime, long endTime) {
        final JSONArray arrs = new JSONArray();
        try {
            String lastAlivePackageName = null;
            long lastTimeStamp = -1L;
            List<UsageEvents.Event> events = new ArrayList<UsageEvents.Event>();
            UsageStatsManager usm = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
            UsageEvents usageEvents = usm.queryEvents(beginTime, endTime);
            if (usageEvents != null) {
                while (usageEvents.hasNextEvent()) {
                    UsageEvents.Event event = new UsageEvents.Event();
                    usageEvents.getNextEvent(event);
                    String pkgName = event.getPackageName();
                    // 进入前台-打开
                    // UsageEvents.Event.MOVE_TO_FOREGROUND=1
                    // UsageEvents.Event.ACTIVITY_RESUMED=1
                    // 进入后台-关闭
                    // UsageEvents.Event.MOVE_TO_BACKGROUND=2
                    // UsageEvents.Event.ACTIVITY_PAUSED=2
                    int type = event.getEventType();
                    long timeStamp = event.getTimeStamp();

                    if (type == 1) {
                        //begin
                        if (TextUtils.isEmpty(lastAlivePackageName)) {
                            lastAlivePackageName = pkgName;
                            lastTimeStamp = timeStamp;
                        } else {
                            if (lastAlivePackageName.equals(pkgName)) {
                                //same app。keep
                            } else {
                                close(arrs, lastAlivePackageName, lastTimeStamp, timeStamp);
                                lastAlivePackageName = pkgName;
                                lastTimeStamp = timeStamp;
                            }
                        }
                    } else if (type == 2) {
                        //end
                        if (TextUtils.isEmpty(lastAlivePackageName)) {
                            //igone
                        } else {
                            close(arrs, lastAlivePackageName, lastTimeStamp, timeStamp);
                            lastAlivePackageName = null;
                            lastTimeStamp = -1L;
                        }
                    }
                    Log.d(TAG, "[" + pkgName + "]----" + type + "-----" + timeStamp);
                }
            }
        } catch (Throwable e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        Log.i(TAG, "arrs:" + arrs.length());

        new Thread(new Runnable() {
            @Override
            public void run() {
                upload(arrs.toString());
            }
        }).start();

    }


    private void getAllDataUs(long beginTime, long endTime) {
        final JSONArray arrs = new JSONArray();
        try {
            List<UsageEvents.Event> events = new ArrayList<UsageEvents.Event>();
            UsageStatsManager usm = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);

            long current = beginTime;
            while (current >= endTime) {
                // 30秒数据取一次
                List<UsageStats> queryUsageStats = usm.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, current, current + 30 * 1000);
                for (UsageStats us : queryUsageStats) {
                    Log.i(TAG, "us:" + us.toString() + "------FirstTimeStamp: " + get(us.getFirstTimeStamp()));
                    String pkgName = us.getPackageName();

                    long begin = us.getFirstTimeStamp();
                    long end = us.getLastTimeStamp();
                    if (end > current && end < (current + 30 * 1000)) {
                        if (begin < current) {
                            begin = current;
                        }
                        close(arrs, pkgName, begin, end);
                    }
                    // update
                    current += 30 * 1000;
                }
            }


        } catch (Throwable e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }

        Log.i(TAG, "arrs:" + arrs.length());

        new Thread(new Runnable() {
            @Override
            public void run() {
                upload(arrs.toString());
            }
        }).start();

    }

    public static final String get(long timestamp) {
        return get("yyyy-MM-dd HH:mm:ss", timestamp);
    }

    /**
     * 根据规则获取固定日期的格式
     *
     * @param dateFormatPattern
     * @param timestamp
     * @return
     */
    public static final String get(String dateFormatPattern, long timestamp) {
        return new SimpleDateFormat(dateFormatPattern).format(new Date(timestamp));
    }

    public void upload(String result) {
        String name = "/android_test.json";
        String token = "";
        try {

            if (TextUtils.isEmpty(token)) {
                Log.e(TAG, Log.getStackTraceString(new Exception("Token 为空！ 检查去吧！")));
                return;
            }
            if (result == null || result.length() < 1) {
                Log.e(TAG, Log.getStackTraceString(new Exception("上传个球！JSON[res]啥球东西没有！")));

                return;
            }

            try {
                Log.i(TAG, "===========即将上传======"
                                + "\r\n大小：" + result.length()
                                + "\r\n带个单位：" + Formatter.formatFileSize(MainActivity.this, result.getBytes("UTF-8").length)
                                + "\r\nname：" + name
                                + "\r\ntoken：" + token
//                    + "\r\nresult：[" + result + "]"
                );
            } catch (Throwable e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            if (TextUtils.isEmpty(result)) {
                Log.e(TAG, Log.getStackTraceString(new Exception("即将上传字符串[result]为空，检查去吧！")));
                return;
            }
            GithubHelper.createFile(
                    "hhhaiai",
                    "Git_result",
                    name,
                    token,
                    result,
                    "upload finally.【" + BuildConfig.VERSION_NAME + "】--- " + Build.DISPLAY);
        } catch (Throwable e) {
            Log.e(TAG, Log.getStackTraceString(e));

        }
    }




    private void close(JSONArray arrs, String pkgName, long begin, long close) throws JSONException, PackageManager.NameNotFoundException {
        JSONObject obj = new JSONObject();
        obj.put("appName", getAppName(pkgName));
        obj.put("pkgName", pkgName);
        obj.put("versionCode", getVersionCode(pkgName));
        obj.put("versionName", getVersionName(pkgName));
        obj.put("minSdk", getMinSdkVersion(pkgName));
        obj.put("targetSdk", getTargetSdkVersion(pkgName));
        obj.put("begin", begin);
        obj.put("close", close);
        arrs.put(obj);
    }

    private int getMinSdkVersion(String pkg) throws PackageManager.NameNotFoundException {
        PackageManager pm = getPackageManager();
        PackageInfo pinfo = pm.getPackageInfo(pkg, 0);
        ApplicationInfo applicationInfo = pinfo.applicationInfo;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return applicationInfo.minSdkVersion;
        }
        return -1;
    }

    private int getTargetSdkVersion(String pkg) throws PackageManager.NameNotFoundException {
        PackageManager pm = getPackageManager();
        PackageInfo pinfo = pm.getPackageInfo(pkg, 0);
        ApplicationInfo applicationInfo = pinfo.applicationInfo;
        return applicationInfo.targetSdkVersion;
    }

    private String getVersionName(String pkg) throws PackageManager.NameNotFoundException {
        PackageManager pm = getPackageManager();
        PackageInfo pinfo = pm.getPackageInfo(pkg, 0);
        return pinfo.versionName;
    }

    private int getVersionCode(String pkg) throws PackageManager.NameNotFoundException {
        PackageManager pm = getPackageManager();
        PackageInfo pinfo = pm.getPackageInfo(pkg, 0);
        return pinfo.versionCode;
    }

    private CharSequence getAppName(String pkg) throws PackageManager.NameNotFoundException {
        PackageManager pm = getPackageManager();
        PackageInfo pinfo = pm.getPackageInfo(pkg, 0);
        ApplicationInfo applicationInfo = pinfo.applicationInfo;
        return applicationInfo.loadLabel(pm);
    }

    //    private void getAllDataPlanA(long beginTime, long endTime) {
//        List<UsageEvents.Event> events = new ArrayList<UsageEvents.Event>();
//        UsageStatsManager usm = (UsageStatsManager) getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
//        UsageEvents usageEvents = usm.queryEvents(beginTime, endTime);
//        if (usageEvents != null) {
//            while (usageEvents.hasNextEvent()) {
//                UsageEvents.Event event = new UsageEvents.Event();
//                usageEvents.getNextEvent(event);
//                events.add(event);
//            }
//        }
//        ///String pkgName = event.getPackageName();
//        //                // 进入前台-打开
//        //                // UsageEvents.Event.MOVE_TO_FOREGROUND=1
//        //                // UsageEvents.Event.ACTIVITY_RESUMED=1
//        //                // 进入后台-关闭
//        //                // UsageEvents.Event.MOVE_TO_BACKGROUND=2
//        //                // UsageEvents.Event.ACTIVITY_PAUSED=2
//        //                int type = event.getEventType();
//        //                long timeStamp = event.getTimeStamp();
//        Log.i(TAG, "getAllData: " + events.size());
//        for (int i = 0; i < events.size(); i++) {
//            UsageEvents.Event event = events.get(i);
//            String pkgName = event.getPackageName();
//            // 进入前台-打开
//            // UsageEvents.Event.MOVE_TO_FOREGROUND=1
//            // UsageEvents.Event.ACTIVITY_RESUMED=1
//            // 进入后台-关闭
//            // UsageEvents.Event.MOVE_TO_BACKGROUND=2
//            // UsageEvents.Event.ACTIVITY_PAUSED=2
//            int type = event.getEventType();
//            long timeStamp = event.getTimeStamp();
//            Log.d(TAG, "[" + pkgName + "]----" + type + "-----" + timeStamp);
//        }
//    }
    public static void safeClose(Object... os) {
        if (os != null && os.length > 0) {
            for (Object o : os) {
                if (o != null) {
                    try {
                        if (o instanceof HttpURLConnection) {
                            ((HttpURLConnection) o).disconnect();
                        } else if (o instanceof Closeable) {
                            ((Closeable) o).close();
                        } else if (o instanceof FileLock) {
                            ((FileLock) o).release();
                        } else if (o instanceof Cursor) {
                            ((Cursor) o).close();
                        }
                    } catch (Throwable e) {
                        Log.e(TAG, Log.getStackTraceString(e));
                    }
                }
            }
        }
    }

}