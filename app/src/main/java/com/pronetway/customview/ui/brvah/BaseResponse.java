package com.pronetway.customview.ui.brvah;

import java.util.List;

/**
 * Created by jin on 2017/7/22.
 */
public class BaseResponse<T> {
    private String result;
    private String totalCount;
    private List<T> eimdata;

    private String mesg;
    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMesg() {
        return mesg;
    }
    public void setMesg(String mesg) {
        this.mesg = mesg;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public List<T> getEimdata() {
        return eimdata;
    }

    public void setEimdata(List<T> eimdata) {
        this.eimdata = eimdata;
    }
}
