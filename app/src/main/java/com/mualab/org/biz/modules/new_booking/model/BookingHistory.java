package com.mualab.org.biz.modules.new_booking.model;

import java.util.List;

/**
 * Created by mindiii on 15/1/19.
 */

public final class BookingHistory {

    /**
     * status : success
     * message : ok
     * data : [{"_id":2,"bookingDate":"2019-01-16","bookingType":1,"bookingTime":"10:00 AM","bookStatus":"0","location":"MINDIII Systems Pvt. Ltd., 502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India","paymentType":2,"paymentStatus":0,"timeCount":600,"artistId":5,"totalPrice":"68","userDetail":[{"_id":1,"userName":"tester","profileImage":"http://koobi.co.uk:3000/uploads/profile/1547528830567.jpg"}],"bookingInfo":[{"_id":2,"bookingPrice":"34","serviceId":1,"subServiceId":1,"artistServiceId":6,"bookingDate":"2019-01-15","startTime":"05:20 PM","endTime":"06:30 PM","artistServiceName":"mango","staffId":5,"staffName":"deepu","staffImage":"http://koobi.co.uk:3000/uploads/profile/","companyId":5,"companyName":"development","companyImage":"http://koobi.co.uk:3000/uploads/profile/"},{"_id":3,"bookingPrice":"34","serviceId":1,"subServiceId":1,"artistServiceId":5,"bookingDate":"2019-01-16","startTime":"10:00 AM","endTime":"11:10 AM","artistServiceName":"apple","staffId":5,"staffName":"deepu","staffImage":"http://koobi.co.uk:3000/uploads/profile/","companyId":5,"companyName":"development","companyImage":"http://koobi.co.uk:3000/uploads/profile/"}]}]
     */

    private String status;
    private String message;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * _id : 2
         * bookingDate : 2019-01-16
         * bookingType : 1
         * bookingTime : 10:00 AM
         * bookStatus : 0  //0 - pending, 1- accept, 2 - reject or cancel,3 - complete
         * location : MINDIII Systems Pvt. Ltd., 502, 503 & 504 Krishna Tower Above ICICI Bank, Main Rd, Brajeshwari Extension, Pipliyahana, Indore, Madhya Pradesh 452016, India
         * paymentType : 2 //1 - online, 2 - offline,
         * paymentStatus : 0 //0- pending, 1- complete
         * timeCount : 600
         * artistId : 5
         * totalPrice : 68
         * "creationDate":"15/01/2019",
         * "creationTime":"12:51 pm",
         * userDetail : [{"_id":1,"userName":"tester","profileImage":"http://koobi.co.uk:3000/uploads/profile/1547528830567.jpg"}]
         * bookingInfo : [{"_id":2,"bookingPrice":"34","serviceId":1,"subServiceId":1,"artistServiceId":6,"bookingDate":"2019-01-15","startTime":"05:20 PM","endTime":"06:30 PM","artistServiceName":"mango","staffId":5,"staffName":"deepu","staffImage":"http://koobi.co.uk:3000/uploads/profile/","companyId":5,"companyName":"development","companyImage":"http://koobi.co.uk:3000/uploads/profile/"},{"_id":3,"bookingPrice":"34","serviceId":1,"subServiceId":1,"artistServiceId":5,"bookingDate":"2019-01-16","startTime":"10:00 AM","endTime":"11:10 AM","artistServiceName":"apple","staffId":5,"staffName":"deepu","staffImage":"http://koobi.co.uk:3000/uploads/profile/","companyId":5,"companyName":"development","companyImage":"http://koobi.co.uk:3000/uploads/profile/"}]
         */

        private int _id;
        private String bookingDate;
        private int bookingType;
        private String bookingTime;
        private String bookStatus;
        private String location;
        private int paymentType;
        private int paymentStatus;
        private int timeCount;
        private int artistId;
        private String totalPrice;
        private String creationDate;
        private String creationTime;
        private List<UserDetailBean> userDetail;
        private List<BookingInfoBean> bookingInfo;

        public int get_id() {
            return _id;
        }

        public void set_id(int _id) {
            this._id = _id;
        }

        public String getBookingDate() {
            return bookingDate;
        }

        public void setBookingDate(String bookingDate) {
            this.bookingDate = bookingDate;
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

        public String getCreationDate() {
            return creationDate;
        }

        public void setCreationDate(String creationDate) {
            this.creationDate = creationDate;
        }

        public String getCreationTime() {
            return creationTime;
        }

        public void setCreationTime(String creationTime) {
            this.creationTime = creationTime;
        }

        public List<UserDetailBean> getUserDetail() {
            return userDetail;
        }

        public void setUserDetail(List<UserDetailBean> userDetail) {
            this.userDetail = userDetail;
        }

        public List<BookingInfoBean> getBookingInfo() {
            return bookingInfo;
        }

        public void setBookingInfo(List<BookingInfoBean> bookingInfo) {
            this.bookingInfo = bookingInfo;
        }

        public static class UserDetailBean {
            /**
             * _id : 1
             * userName : tester
             * profileImage : http://koobi.co.uk:3000/uploads/profile/1547528830567.jpg
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
             * _id : 2
             * bookingPrice : 34
             * serviceId : 1
             * subServiceId : 1
             * artistServiceId : 6
             * bookingDate : 2019-01-15
             * startTime : 05:20 PM
             * endTime : 06:30 PM
             * artistServiceName : mango
             * staffId : 5
             * staffName : deepu
             * staffImage : http://koobi.co.uk:3000/uploads/profile/
             * companyId : 5
             * companyName : development
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
