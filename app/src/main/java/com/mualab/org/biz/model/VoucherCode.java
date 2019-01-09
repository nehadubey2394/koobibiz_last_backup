package com.mualab.org.biz.model;

import android.os.Parcel;
import android.os.Parcelable;

public class VoucherCode implements Parcelable {
    public  String voucherCode,discountType,amount,startDate,endDate,artistId,status,deleteStatus,_id,__v;

    protected VoucherCode(Parcel in) {
        voucherCode = in.readString();
        discountType = in.readString();
        amount = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        artistId = in.readString();
        status = in.readString();
        deleteStatus = in.readString();
        _id = in.readString();
        __v = in.readString();
    }

    public static final Creator<VoucherCode> CREATOR = new Creator<VoucherCode>() {
        @Override
        public VoucherCode createFromParcel(Parcel in) {
            return new VoucherCode(in);
        }

        @Override
        public VoucherCode[] newArray(int size) {
            return new VoucherCode[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(voucherCode);
        parcel.writeString(discountType);
        parcel.writeString(amount);
        parcel.writeString(startDate);
        parcel.writeString(endDate);
        parcel.writeString(artistId);
        parcel.writeString(status);
        parcel.writeString(deleteStatus);
        parcel.writeString(_id);
        parcel.writeString(__v);
    }
}
