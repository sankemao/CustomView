package com.pronetway.customview.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jin on 2017/3/14.
 */
public class FormatUtil {

    //************************************* 时间相关 ********************************************

    /**
     * 获取年月日20170201
     */
    public static String getYMD(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        int year = ca.get(Calendar.YEAR);
        int month = ca.get(Calendar.MONTH) + 1;
        int day = ca.get(Calendar.DAY_OF_MONTH);
        return String.valueOf(year) + (month < 10 ? "0" + month : month) + (day < 10 ? "0" + day : day);
    }

    public static String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    /**
     * 以友好的方式显示时间
     */
    public static String friendly_time(Long sdate) {
        if (sdate == null) {
            return "Unknown";
        }
        Date time = new Date(sdate);

        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater.get().format(cal.getTime());
        String paramDate = dateFormater.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天 ";
        } else if (days > 2 && days < 31) {
            ftime = days + "天前";
        } else if (days >= 31 && days <= 2 * 31) {
            ftime = "一个月前";
        } else if (days > 2 * 31 && days <= 3 * 31) {
            ftime = "2个月前";
        } else if (days > 3 * 31 && days <= 4 * 31) {
            ftime = "3个月前";
        } else {
            ftime = dateFormater.get().format(time);
        }
        return ftime;
    }

    /**
     * 获取10位数的时间戳
     */
    public static String getTimeStamp10() {
        long time = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳
        String str = String.valueOf(time);
        return str;
    }

    //13位的时间戳
    public static String getTimeStamp13() {
        long time = System.currentTimeMillis();//获取系统时间的10位的时间戳
        return String.valueOf(time);
    }

    //10位或13位时间戳转日期
    public static String stampToDate(String timeStamp) {
        long lt = Long.parseLong(timeStamp.length() != 13 ? timeStamp + "000" : timeStamp);
        Date date = new Date(lt);
        return dateFormater.get().format(date);
    }


    //************************************* 人民币相关 *******************************************

    /**
     * 分->元 保留两位小数.
     */
    public static String getRealMoney(String oldMoney) {
//        return String.format("%.2f", Integer.parseInt(oldMoney) / 100.0f);
        return get2Money(Double.parseDouble(oldMoney) / 100.00);
    }

    public static String getRealMoney(Double oldMoney) {
//        return String.format("%.2f", Integer.parseInt(oldMoney) / 100.0f);
        return get2Money(oldMoney / 100.00);
    }


    /**
     * 元->分
     * 结果为整数. ¥ 100.00 -> 10000
     */
    public static String getFenMoney(String oldMoney) {
        return new DecimalFormat("#").format(Double.parseDouble(oldMoney.substring(2)) * 100) + "";
    }

    /**
     * 保留两位小数.
     */
    public static String get2Money(double money) {
        return new DecimalFormat("0.00").format(money);
    }

    public static String get2Money(String money) {
        return get2Money(Double.parseDouble(money));
    }

    /**
     * 将金额转化为中国的货币格式.
     * 元 --> ¥ 元
     * 1 --> ¥ 1.00
     */
    public static String showMoney(String oldMoney) {
//        Double numDouble = Double.parseDouble(oldMoney);
//        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.CHINA);
//        return format.format(numDouble);
        return "¥ " + get2Money(oldMoney);
    }


    //********************************************* 字符串相关 *************************************
    public static String getUTF8(String content) {
        try {
            return URLDecoder.decode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    //验证手机号格式.
    public static boolean checkPhone(String str) {
        Pattern pattern = Pattern.compile("1[0-9]{10}");
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    //验证身份证号格式
    public static boolean checkIdcard(String idCard) {
        String rgx = "^\\d{15}|^\\d{17}([0-9]|X|x)$";
        return Pattern.matches(rgx, idCard);
    }

    /**
     * 获取图片路径
     */
    @SuppressLint({"NewApi", "Recycle"})
    public static String getImagePath(Uri uri, Context context) {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            String authority = uri.getAuthority();
            if ("com.android.externalstorage.documents".equals(authority)) {
                // isExternalStorageDocument
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if ("com.android.providers.downloads.documents".equals(authority)) {
                // isDownloadsDocument
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if ("com.android.providers.media.documents".equals(authority)) {
                // isMediaDocument
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index);
                    }
                }
            } catch (Exception e) {
                e.fillInStackTrace();
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getString(EditText editText) {
        return editText.getText().toString().trim();
    }

    public static String getString(TextView textView) {
        return textView.getText().toString().trim();
    }

    public static String formatMap(HashMap<String, String> params) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 获取md5工具类
     * @param str
     * @return
     */
    public static String getMd5(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        // md5
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 把要加密的东西传进来

            messageDigest.update(str.getBytes());

            // 返回加密后的东西
            byte[] digest = messageDigest.digest();
            for (byte b : digest) {

                // 怎么把byte转成16进制
                String hexStr = Integer.toHexString(b & 0xff);
                // 只需要后位两位
                stringBuffer.append(hexStr);

                // System.out.println(hexStr+"当前的字节数据");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stringBuffer.toString();
    }

    public static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
            System.out.println("MD5(" + sourceStr + ",32) = " + result);
            System.out.println("MD5(" + sourceStr + ",16) = " + buf.toString().substring(8, 24));
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return result;
    }

}
