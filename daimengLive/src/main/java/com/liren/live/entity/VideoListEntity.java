package com.liren.live.entity;

/**
 * Created by Administrator on 2018/5/6 0006.
 */

public class VideoListEntity {
    private String ID;
    private String releaseid;
    private String whetherhot;
    private String releaseaddress;
    private String VideoImagePath;
    private String VideoPath;
    private String VideoName;
    private String CommentsNum;
    private String ClickLikeNum;
    private String clicknum;
    private String forwardingnum;
    private String releasetime;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getReleaseid() {
        return releaseid;
    }

    public void setReleaseid(String releaseid) {
        this.releaseid = releaseid;
    }

    public String getWhetherhot() {
        return whetherhot;
    }

    public void setWhetherhot(String whetherhot) {
        this.whetherhot = whetherhot;
    }

    public String getReleaseaddress() {
        return releaseaddress;
    }

    public void setReleaseaddress(String releaseaddress) {
        this.releaseaddress = releaseaddress;
    }

    public String getVideoImagePath() {
        return VideoImagePath;
    }

    public void setVideoImagePath(String videoImagePath) {
        VideoImagePath = videoImagePath;
    }

    public String getVideoPath() {
        return VideoPath;
    }

    public void setVideoPath(String videoPath) {
        VideoPath = videoPath;
    }

    public String getVideoName() {
        return VideoName;
    }

    public void setVideoName(String videoName) {
        VideoName = videoName;
    }


    public String getCommentsNum() {
        return CommentsNum;
    }

    public void setCommentsNum(String commentsNum) {
        CommentsNum = commentsNum;
    }

    public String getClickLikeNum() {
        return ClickLikeNum;
    }

    public void setClickLikeNum(String clickLikeNum) {
        ClickLikeNum = clickLikeNum;
    }

    public String getClicknum() {
        return clicknum;
    }

    public void setClicknum(String clicknum) {
        this.clicknum = clicknum;
    }

    public String getForwardingnum() {
        return forwardingnum;
    }

    public void setForwardingnum(String forwardingnum) {
        this.forwardingnum = forwardingnum;
    }

    public String getReleasetime() {
        return releasetime;
    }

    public void setReleasetime(String releasetime) {
        this.releasetime = releasetime;
    }
}
