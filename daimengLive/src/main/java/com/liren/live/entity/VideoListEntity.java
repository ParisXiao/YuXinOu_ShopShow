package com.liren.live.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2018/5/6 0006.
 */

public class VideoListEntity  implements Parcelable{
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
    public VideoListEntity(){}
    public VideoListEntity(Parcel in) {
        ID = in.readString();
        releaseid = in.readString();
        whetherhot = in.readString();
        releaseaddress = in.readString();
        VideoImagePath = in.readString();
        VideoPath = in.readString();
        VideoName = in.readString();
        CommentsNum = in.readString();
        ClickLikeNum = in.readString();
        clicknum = in.readString();
        forwardingnum = in.readString();
        releasetime = in.readString();
    }

    public static final Creator<VideoListEntity> CREATOR = new Creator<VideoListEntity>() {
        @Override
        public VideoListEntity createFromParcel(Parcel in) {
            return new VideoListEntity(in);
        }

        @Override
        public VideoListEntity[] newArray(int size) {
            return new VideoListEntity[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(releaseid);
        dest.writeString(whetherhot);
        dest.writeString(releaseaddress);
        dest.writeString(VideoImagePath);
        dest.writeString(VideoPath);
        dest.writeString(VideoName);
        dest.writeString(CommentsNum);
        dest.writeString(ClickLikeNum);
        dest.writeString(clicknum);
        dest.writeString(forwardingnum);
        dest.writeString(releasetime);
    }
}
