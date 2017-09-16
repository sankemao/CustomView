package com.pronetway.baselib.http.progress;

import android.os.Environment;
import android.text.TextUtils;

import com.blankj.utilcode.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;

/**
 * Created by jin on 2017/8/31.
 *
 */
public class FileConvert {

    public static final String DM_TARGET_FOLDER = File.separator + "download" + File.separator; //下载目标文件夹
    private String folder;                  //目标文件存储的文件夹路径
    private String fileName;                //目标文件存储的文件名

    public FileConvert(String folder, String fileName) {
        this.folder = folder;
        this.fileName = fileName;
    }

    public File convertResponse(Response response) throws Throwable {

        if(TextUtils.isEmpty(folder))
            folder = Environment.getExternalStorageDirectory() + DM_TARGET_FOLDER;

        File dir = new File(folder);
        FileUtils.createOrExistsFile(dir);
        File file = new File(dir, fileName);
        FileUtils.createFileByDeleteOldFile(file);

        InputStream bodyStream = null;
        byte[] buffer = new byte[8192];
        FileOutputStream fileOutputStream = null;
        try {
            ResponseBody body = response.body();
            if (body == null) return null;

            bodyStream = body.byteStream();

            int len;
            fileOutputStream = new FileOutputStream(file);
            while ((len = bodyStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
            }
            fileOutputStream.flush();
            return file;
        } finally {
            Util.closeQuietly(bodyStream);
            Util.closeQuietly(fileOutputStream);
        }
    }

}
