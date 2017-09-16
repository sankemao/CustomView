package com.pronetway.baselib.http.progress;

import com.pronetway.baselib.http.callback.FileCallBack;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by jin on 2017/9/1.
 */
public class ProgressResponseBody extends ResponseBody {
    //实际的响应体
    private ResponseBody mResponseBody;

    private FileCallBack mFileCallBack;

    private BufferedSource mBufferedSource;

    public ProgressResponseBody(ResponseBody responseBody, FileCallBack fileCallBack) {
        this.mResponseBody = responseBody;
        this.mFileCallBack = fileCallBack;
    }

    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            //包装
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    /**
     * 读取，回调进度接口
     * @param source Source
     * @return Source
     */
    private Source source(Source source) {

        return new ForwardingSource(source) {
            //当前读取字节数
            long totalBytesRead = 0L;
            @Override public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                //增加当前读取的字节数，如果读取完成了bytesRead会返回-1
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                //回调，如果contentLength()不知道长度，会返回-1
                if (mFileCallBack!=null) {
                    mFileCallBack.onProgress(totalBytesRead, mResponseBody.contentLength(), bytesRead == -1);
                }
                return bytesRead;
            }
        };
    }
}
