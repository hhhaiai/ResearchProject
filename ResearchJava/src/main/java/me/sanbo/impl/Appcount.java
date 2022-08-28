package me.sanbo.impl;

import me.sanbo.json.JSONException;
import me.sanbo.json.JSONObject;
import me.sanbo.utils.FileUtils;

import java.io.File;
import java.util.List;

public class Appcount {

    private static String baseFilepath = "../data/allpkginfos-71833e14.json";

    public static void main(String[] args) throws JSONException {

        File f = new File(baseFilepath);

        if (!f.exists()) {
            System.err.println(f.getAbsolutePath() + " is not exists!" );
        }
       String content= FileUtils.readContent(f.getAbsolutePath());
        JSONObject obj =new JSONObject(content);
        System.out.println(obj.length());
    }
}
