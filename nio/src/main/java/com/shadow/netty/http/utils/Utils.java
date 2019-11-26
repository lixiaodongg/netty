package com.shadow.netty.http.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public static String now() {
        return sdf.format(new Date());
    }
}
