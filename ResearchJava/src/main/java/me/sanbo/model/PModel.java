package me.sanbo.model;

/**
 * @Copyright © 2022 sanbo Inc. All rights reserved.
 * @Description: TODO
 * @Version: 1.0
 * @Create: 2022-08-14 12:54:15
 * @author: Administrator
 */
public class PModel {

    //file path
    public String path = null;
    public String appName = null;
    public String pkgName = null;
    public long versionCode = -1L;
    public String versionName = null;
    public String minSdk = null;
    public String targetSdk = null;
    public long accessLinuxTime = -1L;
    public long modLinuxTime = -1L;
    public long creationLinuxTime = -1L;

    public PModel(String _path, String _appName, String _pkg, long _versionCode, String _versionName, String _minsdk,
                  String _targetsdk, long _accessTime, long _modTime, long _creationTime
    ) {
        this.path = _path;
        this.appName = _appName;
        this.pkgName = _pkg;
        this.versionCode = _versionCode;
        this.versionName = _versionName;
        this.minSdk = _minsdk;
        this.targetSdk = _targetsdk;
        this.accessLinuxTime = _accessTime;
        this.modLinuxTime = _modTime;
        this.creationLinuxTime = _creationTime;
    }

    public static String getTitle() {
        return "app名称,app包名,版本名称,版本值,最小支持版本,目标SDK,access时间,修改时间,创建时间";
    }

    public String toCsvString() {
        return appName + "," + pkgName + "," + versionName + "," + versionCode + "," + minSdk + "," + targetSdk + "," + accessLinuxTime + "," + modLinuxTime + "," + creationLinuxTime;
    }

    @Override
    public String toString() {
        return "PModel{" + "paths='" + path + '\'' + ", appName='" + appName + '\'' + ", pkgName='" + pkgName + '\'' + ", versionCode=" + versionCode + ", versionName='" + versionName + '\'' + ", minSdk='" + minSdk + '\'' + ", targetSdk='" + targetSdk + '\'' + ", accessLinuxTime=" + accessLinuxTime + ", modLinuxTime=" + modLinuxTime + ", creationLinuxTime=" + creationLinuxTime + '}';
    }

    @Override
    public boolean equals(Object obj) {
        return ((PModel) obj).path.equals(path);
    }
}
