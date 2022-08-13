package me.sanbo.utils;

public class PkgHelper {

    /**
     * 有效包名验证
     *
     * @param pkgName
     * @return true: 有效包名
     *  false:无效包名
     */
    public static boolean isEfficientPkg(String pkgName) {
        if (!TextUtils.isEmpty(pkgName) && !pkgName.startsWith(".") && pkgName.contains(".")) {
            return true;
        } else {
            return false;
        }
    }


}
