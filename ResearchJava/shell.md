
# 部分指令整理

## 应用信息获取

### 1. 获取安装列表

``` shell
$ adb shell pm list package
```

### 2. 获取应用的版本号

``` shell
$ adb shell dumpsys package  ${应用包名}|grep version

#### 示例如下
$adb shell dumpsys package  com.ss.android.ugc.aweme|grep version
    versionCode=210001 minSdk=19 targetSdk=29
    versionName=21.0.0
```

### 2. 获取应用的安装路径

``` shell
adb shell pm path  ${应用包名}

#### 示例如下
$adb shell pm path com.ss.android.ugc.aweme
    package:/data/app/com.ss.android.ugc.aweme-QEL3gdpoIrDmAOZdhc_m8g==/base.apk
```

### 3. 获取应用的名字及更多详情

这个方案稍微复杂，需要使用appt命令. aapt是android sdk的一个工具,如需要手机执行，需要编译android平台可用的可执行文件。
这里直接使用[@JonForShort编译好的支持移动端使用的aapt](https://github.com/JonForShort/android-tools/tree/master/build/android-11.0.0_r33)

``` shell
$ aapt dump badging ${apk完整路径} | grep application-label
#### 示例如下
$pm path com.ss.android.ugc.aweme
    package:/data/app/com.ss.android.ugc.aweme-QEL3gdpoIrDmAOZdhc_m8g==/base.apk
$export tmpP=/data/app/com.ss.android.ugc.aweme-QEL3gdpoIrDmAOZdhc_m8g==/base.apk 
$aapt dump badging ${tmpP} | grep application-label
    application-label:'抖音'
    application-label-zh:'抖音'
```

## 设备信息获取

## 1. 获取手机的序列号

``` shell
$ adb get-serialno
$ adb shell getprop ro.serialno
```

## 2. 获取厂商信息

``` shell
$ adb -d shell getprop ro.product.brand
```
## 2. 获取手机设备型号

``` shell
$ adb -d shell getprop ro.product.brand
```
## 2. 获取手机版本

``` shell
$ adb -d shell getprop ro.build.version.release
```

## 3. 获取cpu架构

``` shell
$ adb shell getprop ro.product.cpu.abi
$ adb shell getprop ro.product.cpu.abilist
$ adb shell getprop ro.product.cpu.abilist32
$ adb shell getprop ro.product.cpu.abilist64
```

## 4. 获取手机内存

``` shell
$ adb shell cat /proc/meminfo
```


## 5. 获取手机cpu详情

``` shell
$ adb shell cat /proc/cpuinfo
```

## 6. 获取手机存储情况

``` shell
$ adb shell df /data
```

## 6. 获取手机分辨率

``` shell
$ adb shell wm size
######更多详情
$ adb shell dumpsys window displays
```
## 6. 获取手机DPI

``` shell
$ adb shell wm density
```


## 鸣谢

* [JonForShort](https://github.com/JonForShort/android-tools)