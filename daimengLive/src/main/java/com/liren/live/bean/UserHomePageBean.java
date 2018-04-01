package com.liren.live.bean;

import java.util.List;

/**
 * 他人信息中心数据模型
 */
public class UserHomePageBean{



    public String id;
    public String user_nicename;
    public String avatar;
    public String avatar_thumb;
    public String sex;
    public String signature;
    public String consumption;
    public String votestotal;
    public String province;
    public String city;
    public String islive;
    public String birthday;
    public String issuper;
    public String level;
    public String follows;
    public String fans;
    public String isattention;
    public String isblack;
    public String isblack2;
    public LiveJson liveinfo;
    public List<ContributeBean> contribute;
    public List<LiveRecordBean> liverecord;



    public static class ContributeBean {
        /**
         * uid : 122
         * total : 628674
         * avatar : http://ogf4bdlca.bkt.clouddn.com/20170207_589933e5bf4ab.jpeg
         */

        private String uid;
        private String total;
        private String avatar;

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

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

}
