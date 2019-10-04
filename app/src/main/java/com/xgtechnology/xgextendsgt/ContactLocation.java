package com.xgtechnology.xgextendsgt;

/**
 * Created by timr on 1/29/2018.
 */

public class ContactLocation {
        private String UnitName;
        private String CallSign;
        private String ID;
        private String IPaddress;
        private String Latitude;
        private String Longitude;
        private String distance;
        private String clockheading;

        public ContactLocation(String UnitName,String CallSign, String ID, String IPAddress, String Latitude, String Longitude, String distance, String clockheading) {
            this.UnitName = UnitName;
            this.CallSign = CallSign;
            this.ID = ID;
            this.IPaddress = IPAddress;
            this.Latitude = Latitude;
            this.Longitude = Longitude;
            this.distance = distance;
            this.clockheading = clockheading;
        }

        public String getUnitName() {
        return UnitName;
    }

        public void setUnitName (String UnitName)
    {
        this.UnitName = UnitName;
    }

        public String getCallSign() {
            return CallSign;
        }

        public void setCallSign(String callSign)
        {
            this.CallSign = callSign;
        }

        public String getID() {
            return ID;
        }

        public void setID(String ID)
        {
            this.ID = ID;
        }

        public String getIPaddress() {
            return IPaddress;
        }

        public void setIPAddesss(String IPaddress)
        {
            this.IPaddress = IPaddress;
        }


        public String getLatitude() {
            return Latitude;
        }

        public void setLatitude(String Latitude)
        {
            this.Latitude = Latitude;
        }

       public String getLongitude() {
            return Longitude;
        }

        public void setLongitude(String Longitude)
        {
            this.Longitude = Longitude;
        }


        public String getdistance()
        {
            return distance;
        }

        public void setDistance (String distance)
        {
            this.distance = distance;
        }

        public String getClockheading() { return clockheading; }

        public void setClockheading (String clockheading)
        {
            this.clockheading = clockheading;
        }

}
