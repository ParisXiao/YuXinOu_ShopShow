package com.liren.live.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2018/5/12 0012.
 */

public class LiveEntity implements Parcelable {
    public int n;
    public String uid;
    public  String user_nicename;
    public String avatar;
    public String pull;
    public boolean type;
    public String type_val;
    public int islive;
    public int nums;

    protected LiveEntity(Parcel in) {
        n = in.readInt();
        uid = in.readString();
        user_nicename = in.readString();
        avatar = in.readString();
        pull = in.readString();
        type = in.readByte() != 0;
        type_val = in.readString();
        islive = in.readInt();
        nums = in.readInt();
    }

    public static final Creator<LiveEntity> CREATOR = new Creator<LiveEntity>() {
        @Override
        public LiveEntity createFromParcel(Parcel in) {
            return new LiveEntity(in);
        }

        @Override
        public LiveEntity[] newArray(int size) {
            return new LiveEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(n);
        dest.writeString(uid);
        dest.writeString(user_nicename);
        dest.writeString(avatar);
        dest.writeString(pull);
        dest.writeByte((byte) (type ? 1 : 0));
        dest.writeString(type_val);
        dest.writeInt(islive);
        dest.writeInt(nums);
    }
}
