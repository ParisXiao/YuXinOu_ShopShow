package com.liren.live.bean;

/**
 * Created by Administrator on 2016/4/5.
 */
public class OrderBean {


    private String uid;
    private String total;
    private UserBean userinfo;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public UserBean getUserinfo() {
        return userinfo;
    }

    public void setUserinfo(UserBean userinfo) {
        this.userinfo = userinfo;
    }


}
