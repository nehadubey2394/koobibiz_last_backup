package com.mualab.org.biz.modules.new_booking.model;

import java.util.List;

/**
 * Created by mindiii on 16/1/19.
 */

public final class BookingDetail {


    /**
     * status : success
     * message : ok
     * data : {"_id":26,"discountPrice":"0","bookingDate":"2019-01-18","customerType":"online","bookingType":1,"bookingTime":"10:50 AM","bookStatus":"0","location":"MINDIII Systems Pvt. Ltd. 502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension","paymentType":2,"paymentStatus":0,"voucher":{"discountType":"2","status":"1","_id":"4","startDate":"2019-01-16","endDate":"2019-02-16","voucherCode":"TE41@62","artistId":"5","__v":"0","amount":"12","deleteStatus":"1"},"timeCount":650,"artistId":8,"totalPrice":"20","userDetail":[{"_id":10,"userName":"jamesfb","profileImage":"http://koobi.co.uk:3000/uploads/profile/1547559625230.jpg"}],"artistDetail":[{"_id":8,"userName":"hemant","profileImage":""}],"bookingInfo":[{"_id":30,"bookingPrice":"20","serviceId":1,"subServiceId":1,"artistServiceId":11,"bookingDate":"2019-01-18","startTime":"10:50 AM","endTime":"11:10 AM","artistServiceName":"f1","staffId":8,"staffName":"hemant","staffImage":"http://koobi.co.uk:3000/uploads/profile/","companyId":8,"companyName":"Sharma Saloon","companyImage":"http://koobi.co.uk:3000/uploads/profile/"}]}
     */

    private String status;
    private String message;
    private DataBean data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * _id : 26
         * discountPrice : 0
         * bookingDate : 2019-01-18
         * customerType : online
         * bookingType : 1
         * bookingTime : 10:50 AM
         * bookStatus : 0
         * location : MINDIII Systems Pvt. Ltd. 502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension
         * paymentType : 2
         * paymentStatus : 0
         * voucher : {"discountType":"2","status":"1","_id":"4","startDate":"2019-01-16","endDate":"2019-02-16","voucherCode":"TE41@62","artistId":"5","__v":"0","amount":"12","deleteStatus":"1"}
         * timeCount : 650
         * artistId : 8
         * totalPrice : 20
         * userDetail : [{"_id":10,"userName":"jamesfb","profileImage":"http://koobi.co.uk:3000/uploads/profile/1547559625230.jpg"}]
         * artistDetail : [{"_id":8,"userName":"hemant","profileImage":""}]
         * bookingInfo : [{"_id":30,"bookingPrice":"20","serviceId":1,"subServiceId":1,"artistServiceId":11,"bookingDate":"2019-01-18","startTime":"10:50 AM","endTime":"11:10 AM","artistServiceName":"f1","staffId":8,"staffName":"hemant","staffImage":"http://koobi.co.uk:3000/uploads/profile/","companyId":8,"companyName":"Sharma Saloon","companyImage":"http://koobi.co.uk:3000/uploads/profile/"}]
         */

        private int _id;
        private String discountPrice;
        private String bookingDate;
        private String customerType;
        private int bookingType;
        private String bookingTime;
        private String bookStatus;
        private String location;
        private int paymentType;
        private int paymentStatus;
        private VoucherBean voucher;
        private int timeCount;
        private int artistId;
        private String totalPrice;
        private List<UserDetailBean> userDetail;
        private List<ArtistDetailBean> artistDetail;
        private List<BookingInfoBean> bookingInfo;

        public int get_id() {
            return _id;
        }

        public void set_id(int _id) {
            this._id = _id;
        }

        public String getDiscountPrice() {
            return discountPrice;
        }

        public void setDiscountPrice(String discountPrice) {
            this.discountPrice = discountPrice;
        }

        public String getBookingDate() {
            return bookingDate;
        }

        public void setBookingDate(String bookingDate) {
            this.bookingDate = bookingDate;
        }

        public String getCustomerType() {
            return customerType;
        }

        public void setCustomerType(String customerType) {
            this.customerType = customerType;
        }

        public int getBookingType() {
            return bookingType;
        }

        public void setBookingType(int bookingType) {
            this.bookingType = bookingType;
        }

        public String getBookingTime() {
            return bookingTime;
        }

        public void setBookingTime(String bookingTime) {
            this.bookingTime = bookingTime;
        }

        public String getBookStatus() {
            return bookStatus;
        }

        public void setBookStatus(String bookStatus) {
            this.bookStatus = bookStatus;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public int getPaymentType() {
            return paymentType;
        }

        public void setPaymentType(int paymentType) {
            this.paymentType = paymentType;
        }

        public int getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(int paymentStatus) {
            this.paymentStatus = paymentStatus;
        }

        public VoucherBean getVoucher() {
            return voucher;
        }

        public void setVoucher(VoucherBean voucher) {
            this.voucher = voucher;
        }

        public int getTimeCount() {
            return timeCount;
        }

        public void setTimeCount(int timeCount) {
            this.timeCount = timeCount;
        }

        public int getArtistId() {
            return artistId;
        }

        public void setArtistId(int artistId) {
            this.artistId = artistId;
        }

        public String getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(String totalPrice) {
            this.totalPrice = totalPrice;
        }

        public List<UserDetailBean> getUserDetail() {
            return userDetail;
        }

        public void setUserDetail(List<UserDetailBean> userDetail) {
            this.userDetail = userDetail;
        }

        public List<ArtistDetailBean> getArtistDetail() {
            return artistDetail;
        }

        public void setArtistDetail(List<ArtistDetailBean> artistDetail) {
            this.artistDetail = artistDetail;
        }

        public List<BookingInfoBean> getBookingInfo() {
            return bookingInfo;
        }

        public void setBookingInfo(List<BookingInfoBean> bookingInfo) {
            this.bookingInfo = bookingInfo;
        }

        public static class VoucherBean {
            /**
             * discountType : 2
             * status : 1
             * _id : 4
             * startDate : 2019-01-16
             * endDate : 2019-02-16
             * voucherCode : TE41@62
             * artistId : 5
             * __v : 0
             * amount : 12
             * deleteStatus : 1
             */

            private String discountType;
            private String status;
            private String _id;
            private String startDate;
            private String endDate;
            private String voucherCode = "";
            private String artistId;
            private String __v;
            private String amount = "";
            private String deleteStatus;

            public String getDiscountType() {
                return discountType;
            }

            public void setDiscountType(String discountType) {
                this.discountType = discountType;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String get_id() {
                return _id;
            }

            public void set_id(String _id) {
                this._id = _id;
            }

            public String getStartDate() {
                return startDate;
            }

            public void setStartDate(String startDate) {
                this.startDate = startDate;
            }

            public String getEndDate() {
                return endDate;
            }

            public void setEndDate(String endDate) {
                this.endDate = endDate;
            }

            public String getVoucherCode() {
                return voucherCode;
            }

            public void setVoucherCode(String voucherCode) {
                this.voucherCode = voucherCode;
            }

            public String getArtistId() {
                return artistId;
            }

            public void setArtistId(String artistId) {
                this.artistId = artistId;
            }

            public String get__v() {
                return __v;
            }

            public void set__v(String __v) {
                this.__v = __v;
            }

            public String getAmount() {
                return amount;
            }

            public void setAmount(String amount) {
                this.amount = amount;
            }

            public String getDeleteStatus() {
                return deleteStatus;
            }

            public void setDeleteStatus(String deleteStatus) {
                this.deleteStatus = deleteStatus;
            }
        }

        public static class UserDetailBean {
            /**
             * _id : 10
             * userName : jamesfb
             * profileImage : http://koobi.co.uk:3000/uploads/profile/1547559625230.jpg
             */

            private int _id;
            private String userName;
            private String profileImage;

            public int get_id() {
                return _id;
            }

            public void set_id(int _id) {
                this._id = _id;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getProfileImage() {
                return profileImage;
            }

            public void setProfileImage(String profileImage) {
                this.profileImage = profileImage;
            }
        }

        public static class ArtistDetailBean {
            /**
             * _id : 8
             * userName : hemant
             * profileImage :
             */

            private int _id;
            private String userName;
            private String profileImage;

            public int get_id() {
                return _id;
            }

            public void set_id(int _id) {
                this._id = _id;
            }

            public String getUserName() {
                return userName;
            }

            public void setUserName(String userName) {
                this.userName = userName;
            }

            public String getProfileImage() {
                return profileImage;
            }

            public void setProfileImage(String profileImage) {
                this.profileImage = profileImage;
            }
        }

        public static class BookingInfoBean {
            /**
             * _id : 30
             * bookingPrice : 20
             * serviceId : 1
             * subServiceId : 1
             * artistServiceId : 11
             * bookingDate : 2019-01-18
             * startTime : 10:50 AM
             * endTime : 11:10 AM
             * artistServiceName : f1
             * staffId : 8
             * staffName : hemant
             * staffImage : http://koobi.co.uk:3000/uploads/profile/
             * companyId : 8
             * companyName : Sharma Saloon
             * companyImage : http://koobi.co.uk:3000/uploads/profile/
             */

            private int _id;
            private String bookingPrice;
            private int serviceId;
            private int subServiceId;
            private int artistServiceId;
            private String bookingDate;
            private String startTime;
            private String endTime;
            private String artistServiceName;
            private int staffId;
            private String staffName;
            private String staffImage;
            private int companyId;
            private String companyName;
            private String companyImage;

            public int get_id() {
                return _id;
            }

            public void set_id(int _id) {
                this._id = _id;
            }

            public String getBookingPrice() {
                return bookingPrice;
            }

            public void setBookingPrice(String bookingPrice) {
                this.bookingPrice = bookingPrice;
            }

            public int getServiceId() {
                return serviceId;
            }

            public void setServiceId(int serviceId) {
                this.serviceId = serviceId;
            }

            public int getSubServiceId() {
                return subServiceId;
            }

            public void setSubServiceId(int subServiceId) {
                this.subServiceId = subServiceId;
            }

            public int getArtistServiceId() {
                return artistServiceId;
            }

            public void setArtistServiceId(int artistServiceId) {
                this.artistServiceId = artistServiceId;
            }

            public String getBookingDate() {
                return bookingDate;
            }

            public void setBookingDate(String bookingDate) {
                this.bookingDate = bookingDate;
            }

            public String getStartTime() {
                return startTime;
            }

            public void setStartTime(String startTime) {
                this.startTime = startTime;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public String getArtistServiceName() {
                return artistServiceName;
            }

            public void setArtistServiceName(String artistServiceName) {
                this.artistServiceName = artistServiceName;
            }

            public int getStaffId() {
                return staffId;
            }

            public void setStaffId(int staffId) {
                this.staffId = staffId;
            }

            public String getStaffName() {
                return staffName;
            }

            public void setStaffName(String staffName) {
                this.staffName = staffName;
            }

            public String getStaffImage() {
                return staffImage;
            }

            public void setStaffImage(String staffImage) {
                this.staffImage = staffImage;
            }

            public int getCompanyId() {
                return companyId;
            }

            public void setCompanyId(int companyId) {
                this.companyId = companyId;
            }

            public String getCompanyName() {
                return companyName;
            }

            public void setCompanyName(String companyName) {
                this.companyName = companyName;
            }

            public String getCompanyImage() {
                return companyImage;
            }

            public void setCompanyImage(String companyImage) {
                this.companyImage = companyImage;
            }
        }
    }
}
