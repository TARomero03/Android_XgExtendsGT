package com.xgtechnology.xgextendsgt;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import java.util.Calendar;

import android.hardware.GeomagneticField;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;


import com.gotenna.sdk.bluetooth.BluetoothAdapterManager;

import org.spongycastle.util.IPAddress;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

//public class MainActivity_xGextendsGT extends AppCompatActivity implements GTConnectionManager.GTConnectionListener {

public class MainActivity_xGextendsGT extends AppCompatActivity implements SensorEventListener{
    TextView tvStatus;
    TextView tvGID;
    TextView tvIP;
    TextView tvLatitude;
    TextView tvLongitude;
    TextView tvHeading;
    TextView tvUnitName;
    TextView tvCallSign;
    ListView lvMessages;
    Button btnStart;
    RelativeLayout rlRunTime;
    RelativeLayout rlMessages;
    String ip;
    String gid;
    String FakeGID = "";
    boolean bTransmit = false;
    boolean bCallSign = false;
    boolean bAppStarted = false;
    boolean bRestart = false;
    boolean bIamHere=false;
    boolean bGetIamHere=false;
    boolean bSendGlobalMessages=false;
    boolean bGetGlobalMessages=false;
    ArrayList<String> alIncomingIDs;
    ArrayList<String>alIncomingRaw;
    ArrayList<String> alIncomingGlobal;
    ArrayList<String> alOutGoingGlobal;
    ArrayList<Location> alCurLoc;
    ArrayList<Location> alPriorLoc;
    boolean bGetGTStatus =false;
    SendIamHereThread iah99;
    GetGlobalMessagesThread ggm99;
    SendGlobalMessagesThread sgm99;
    GetIamHereThread giah99;
    UpdateGUI ug99;
    Context context;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location location;
    private double lat;
    private double lng;
    private double lastlat = 0.00;
    private double lastlng = 0.00;
    private double Firstlat = 0.00;
    private double Firstlng = 0.00;
    private double bearing = 0.00;
    GTcontrol gtc99 = null;
    BluetoothAdapterManager bam99;
    private static final String LOG_TAG = "xGextendsGT";
    private static final String GOTENNA_APP_TOKEN = "EFgMWgsVDEMPWhZHXFwBRQBVCk5HUlgDABRTVBkQDgccFwlEBVxWUQwPXRkARxlH";// TODO: Insert your token
    ArrayAdapter<String> itemsAdapter;
    LocationsAdapter myAdapter;
    private boolean bGTConnected=false;
    Globals gbl99 = new Globals();
    long distChk = 50;
    long lLastLocSend =0;
    long lStartTime;
    String sSep = "" + (char)191;
    String saMsg[];
    int et=0;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    private float mCurrentDegree = 0f;
    GeomagneticField geoField;
    float degree;
    String sCallSign = "";
    String sUnitName = "";
    ArrayList<ContactLocation> allocationList=new ArrayList<>();
        private class myLocationlistener implements LocationListener {
         @Override
        public void onLocationChanged(Location location) {
            Log.i("Loc Change","Location Change");

             geoField = new GeomagneticField(
                     Double.valueOf(location.getLatitude()).floatValue(),
                     Double.valueOf(location.getLongitude()).floatValue(),
                     Double.valueOf(location.getAltitude()).floatValue(),
                     System.currentTimeMillis()
             );
            try {
                if (location != null) {
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                    bearing = location.getBearing();
                }
                if (lastlat == 0) lastlat = lat;
                if (lastlng == 0) lastlng = lng;
                if (Firstlat == 0) Firstlat = lat;
                if (Firstlng == 0) Firstlng = lng;
            }
            catch (Exception e55)
            {
                Log.i("Loc Change Fail", e55.getMessage());
            }
             Log.i("Loc Change 2","Location Change");
             //gtcc99.sendBroadcastMessage();
         }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    public class GetGTstatusThread implements Runnable {


        public void run()
        {
            while(bGetGTStatus)
            {

                SystemClock.sleep(5000);
            }
        }

        public void stop()

        {
            bGetGTStatus = false;
        }


    }


    public class GetIamHereThread implements Runnable {

        final static String INET_ADDR = "224.0.0.5";
        final static int PORT = 8888;

        public void run()
        {
            bGetIamHere = true;
            Log.i("GIAH", "Step 1");
            try {
                InetAddress addr = InetAddress.getByName(INET_ADDR);

                byte[] buf = new byte[256];

                try (MulticastSocket mcsWhoIsHere = new MulticastSocket(PORT))
                {
                    mcsWhoIsHere.joinGroup(addr);
                    while(bGetIamHere)
                    {
                        Log.i("GIAH","Get Maesage");
                        DatagramPacket msg = new DatagramPacket(buf,buf.length);
                        mcsWhoIsHere.receive(msg);
                        String sRcvd = new String(buf,0,buf.length);
                        Log.i("TAR Rcvd",sRcvd);
                        String saParts[] = sRcvd.split(sSep);
                        if(saParts[3].trim().compareTo(ip.trim())==0) continue;
                        try {

                            Log.i("GT Send A", "Con: " + bGTConnected + "  ** " + sRcvd);
                            if (gtc99.bGTConnected && saParts[2].trim().indexOf("NG") ==0) {

                                String sRSend = saParts[0] + sSep + saParts[1] + sSep + "XG" + saParts[2].substring(2);
                                for(int iu=3; iu < saParts.length; iu++)
                                {
                                    sRSend = sRSend + sSep + saParts[iu];
                                }

                                gtc99.SendMessage(sRSend);
                                Log.i("GT Send a2", sRSend);
                            }

                        } catch (Exception e88) {
                            Log.i("GT Send ", "Fail " + e88.getMessage());
                        }
                        String sGIDf = saParts[2];
                        String sIPf = saParts[3];
                        if(sIPf.compareTo("999.999.999.999")==0)
                        {
                            sIPf = "0.0.0.0";

                        }
                        if (sGIDf.indexOf("XG") ==0) {

                            sGIDf = "NG" + sGIDf.substring(3);
                        }
                        String sMsg = saParts[0] + sSep + saParts[1] + sSep + sGIDf + sSep + sIPf;
                        for(int iY=4; iY<saParts.length; iY++)
                        {
                            sMsg = sMsg + sSep + saParts[iY];
                        }
                        String sKey = saParts[0]+saParts[1] + sGIDf+sIPf;
                        sKey = sKey.trim();

                                int iKey = alIncomingIDs.indexOf(sKey);
                        Log.i("UpDate GUI", "Key N: " + sKey + " --- " + iKey + " of " + alIncomingIDs.size());
                        Location locCur = new Location("");
                        locCur.setLatitude(Double.valueOf(saParts[4]));
                        locCur.setLongitude(Double.valueOf(saParts[5]));
                        locCur.setBearing(Float.valueOf(saParts[6]));
                        if( iKey< 0) {
                            alIncomingRaw.add(sMsg);
                            alIncomingIDs.add(sKey);
                            alCurLoc.add(locCur);
                            alPriorLoc.add(locCur);
                        }
                        else
                        {
                            String sWas = alIncomingRaw.get(iKey);
                            Log.i("UpDate GUI N", "Old Key N: " + sKey + " --- " + sMsg);
                            Log.i("UpDate GUI O", "Old Key W: " + sKey + " --- " + sWas);
                            alIncomingRaw.get(iKey);
                            alIncomingRaw.set(iKey,sMsg);
                            alPriorLoc.set(iKey,alCurLoc.get(iKey));
                            alCurLoc.set(iKey,locCur);

                        }
                        SystemClock.sleep(100);
                    }


                }
                catch(IOException e44)
                {
                    Log.i("GIAH","Get Maesage X " + e44.getMessage());
                }
            }
            catch (Exception e77)
            {
                Log.i("GIAH","Exc " + e77.getMessage());

            }
        }

        public void stop()

        {
            bGetIamHere = false;
        }


    }

    public class SendIamHereThread implements Runnable {
        final static String INET_ADDR = "224.0.0.5";
        final static int PORT = 8888;

        public void run () {


               try {

                   InetAddress addr = InetAddress.getByName(INET_ADDR);

                   try {
                       DatagramSocket dsIamHere = new DatagramSocket();

                       while (bIamHere) {
                           if(!bTransmit) continue;
                           if(!bCallSign) continue;
                           //if((SystemClock.elapsedRealtime() - lLastLocSend) >= 30000)
                           {
                               Log.i("SIAH 1a", "Send 15 sec chk");
                               if(lastlat == 0) lastlat = lat;
                               if(lastlng == 0) lastlng = lng;

                               Log.i("SIAH 2a", "Send 15 sec chk");
                                  Location loc2 = new Location("");
                               Location loc1 = new Location("");
                               loc2.setLatitude(lastlat);
                               loc2.setLongitude(lastlng);
                               loc1.setLatitude(lat);
                               loc1.setLongitude(lng);
                               float bearing = loc1.bearingTo(loc2);
                               if(bearing < 0) bearing = bearing + 360;
                               float distanceInMeters = loc1.distanceTo(loc2);
                               String sSep = "" + (char)191;
                               Log.i("SIAH 3a", "Send 15 sec chk");
                               String sGID = "";
                               if(bGTConnected)
                               {
                                   sGID = gid;
                               }
                               else
                               {
                                   sGID = FakeGID;
                     //              tvGID.setText("GID:");
                               }

                               Log.i("SIAH 4a", "Send 15 sec chk");
                               String sMsg2="";
                               try {
                                   sMsg2 = sUnitName + sSep + sCallSign + sSep + sGID + sSep + ip + sSep + lat + sSep + lng + sSep + String.valueOf(loc1.getBearing()) + sSep + String.valueOf(Calendar.getInstance().getTimeInMillis());
                               }
                               catch(Exception e77)
                               {
                                   Log.i("SIAH 5a", "fail " + e77.getMessage());

                               }
                               try {
                                    Log.i("SIAH on Timer Chk", sMsg2);
                                   byte[] sBytes = sMsg2.getBytes();
                                   Log.i("GT Send B", "Con: " + bGTConnected + "  ** " + sMsg2 + " --- " + new String(sBytes));
                                   if(bGTConnected && ((SystemClock.elapsedRealtime() - lLastLocSend) >= 30000)) {
                                       gtc99.SendMessage(sMsg2);
                                       Log.i("GT Send B2", sMsg2 + " --- " + new String(sBytes));
                                       lLastLocSend = SystemClock.elapsedRealtime();
                                   }
                                   if(ip.compareTo("0.0.0.0") != 0)
                                   {
                                       alOutGoingGlobal.add(sMsg2);
                                   }
                               }
                               catch(Exception e88)
                               {
                                   Log.i("GT Send ", "Fail " + e88.getMessage());
                               }



                          }
                            if(!bTransmit)
                            {
                                SystemClock.sleep(100);
                            }
                            else {
                                SystemClock.sleep(30000);
                            }
                       }
                   } catch (IOException ex) {

                   }
               } catch (Exception e44) {

               }
        }

        public void stop()

        {
            bIamHere = false;
        }

    }

    public class GetGlobalMessagesThread implements Runnable {

        final static String INET_ADDR = "224.0.0.10";
        final static int PORT = 8880;

        public void run()
        {
            bGetGlobalMessages = true;
            Log.i("GIAH", "Step 1");
            try {
                InetAddress addr = InetAddress.getByName(INET_ADDR);

                byte[] buf = new byte[256];

                try (MulticastSocket mcsWhoIsHere = new MulticastSocket(PORT))
                {
                    mcsWhoIsHere.joinGroup(addr);
                    while(bGetGlobalMessages)
                    {
                        Log.i("GIAH","Get Maesage");
                        DatagramPacket msg = new DatagramPacket(buf,buf.length);
                        mcsWhoIsHere.receive(msg);
                        String sRcvd ="";
                        try {
                            sRcvd = new String(buf, 0, buf.length).trim();
                        }
                        catch(Exception e77)
                        {

                            continue;
                        }
                        Log.i("TAR Rcvd G",sRcvd);
                        Log.i("TAR Rcvd G","Length: " + sRcvd.trim().length());
                        if(sRcvd.trim().length()==0)continue;
                        String saParts[] = sRcvd.split(sSep);

                        if(Long.valueOf(saParts[7].trim()) < lStartTime) continue;
                        if(saParts[3].trim().compareTo(ip)==0) continue;

                        try {
                            if(saParts[2].trim().toUpperCase().indexOf("NG")==0)
                            {
                                String sNew = saParts[0] + sSep + saParts[1] + sSep + "XG" + saParts[2].substring(2) + sSep + saParts[3] + sSep + saParts[4] + sSep + saParts[5] + sSep + saParts[6] + sSep + saParts[7];
                                if(bGTConnected ) {
                                    byte[] sBytes = sNew.getBytes();
                                    gtc99.SendMessage(sNew);
                                    Log.i("GT Send C2 ", sNew + " --- " + new String(sBytes));
                                }
                            }

                        } catch (Exception e88) {


                            Log.i("GT Send ", "Fail " + e88.getMessage());
                        }
                        String sGIDf = saParts[2];
                        String sIPf = saParts[3];
                        if(sIPf.compareTo("999.999.999.999")==0)
                        {
                            sIPf = "0.0.0.0";

                        }

                        Log.i("GM  IN 1",sRcvd);
                        if (sGIDf.indexOf("XG") ==0) {

                            sGIDf = "NG" + sGIDf.substring(2);
                        }

/*                        if(alIncomingGlobal.size() == 1) {
                            if (alIncomingGlobal.get(0).indexOf("No data") == 0) {
                                alIncomingGlobal.remove(0);
                            }
                        }
*/
                        String sMsg = saParts[0]+ sSep + saParts[1]+ sSep + sGIDf + sSep + sIPf;
                        for(int iY=4; iY < saParts.length; iY++)
                        {
                            sMsg = sMsg + sSep +saParts[iY];

                        }
                        String sKey = saParts[0]+ saParts[1]+sGIDf+sIPf;
                        sKey = sKey.trim();
                        int iKey = alIncomingIDs.indexOf(sKey);
                        Log.i("UpDate GUI", "Key N: " + sKey + " --- " + iKey + " of " + alIncomingIDs.size());
                        Location locNow = new Location("");
                        locNow.setLatitude(Double.valueOf(saParts[4]));
                        locNow.setLongitude(Double.valueOf(saParts[5]));
                        locNow.setBearing(Float.valueOf(saParts[6]));

                        if( iKey< 0) {
                            Log.i("Add to alIncoming Raw",sRcvd);
                            alCurLoc.add(locNow);
                            alPriorLoc.add(locNow);
                            alIncomingRaw.add(sMsg);
                            alIncomingIDs.add(sKey);
                        }
                        else
                        {
                            String sWas = alIncomingRaw.get(iKey);
                            Log.i("UpDate GUI N", "Old Key N: " + sKey + " --- " + sMsg);
                            Log.i("UpDate GUI O", "Old Key W: " + sKey + " --- " + sWas);
                            alPriorLoc.set(iKey,alCurLoc.get(iKey));
                            alCurLoc.set(iKey,locNow);
                            alIncomingRaw.get(iKey);
                            alIncomingRaw.set(iKey,sMsg);
                        }
                        SystemClock.sleep(100);
                    }


                }
                catch(IOException e44)
                {
                    Log.i("GIAH","Get Maesage " + e44.getMessage());
                }


            }
            catch (Exception e77)
            {
                Log.i("GIAH","Exc " + e77.getMessage());

            }
        }

        public void stop()

        {
            bGetGlobalMessages = false;
        }

    }

    public class SendGlobalMessagesThread implements Runnable {
        final static String INET_ADDR = "224.0.0.10";
        final static int PORT = 8880;

        public void run () {
            try {
                Log.i("SGM"," Started");
                InetAddress addr = InetAddress.getByName(INET_ADDR);
                bSendGlobalMessages = true;
                try {
                    DatagramSocket dsIamHere = new DatagramSocket();

                    while (bSendGlobalMessages) {
                        if(!bTransmit) continue;
                        if(bGTConnected) {
                            while (gtc99.alResend.size() > 0) {
                                alOutGoingGlobal.add(gtc99.alResend.get(0));
                                gtc99.alResend.remove(0);
                            }
                        }
                        if(alOutGoingGlobal.size() > 0)
                        {
                            while(alOutGoingGlobal.size() > 0) {
                                if (alOutGoingGlobal.get(0).trim().length() > 0) {
                                    String sMsg = alOutGoingGlobal.get(0).trim();
                                    Log.i("SGMT", "Msg: " + sMsg);
                                    DatagramPacket msgPacket = new DatagramPacket   (sMsg.getBytes(), sMsg.getBytes().length, addr, PORT);
                                    dsIamHere.send(msgPacket);
                             //       SystemClock.sleep(5000);
                                }
                                alOutGoingGlobal.remove(0);
                            }

                        }

                        SystemClock.sleep(100);
                    }
                }
                catch (IOException ex) {
                    Log.i("SGMT ex 1", ex.getMessage());
                }
            }
            catch(Exception e44)
            {
                Log.i("SGMT ex 1", e44.getMessage());
            }

        }

        public void stop()

        {
            bSendGlobalMessages = false;
        }

    }


    public class UpdateGUI implements Runnable
    {
        ArrayList<String> alPastMessages = new ArrayList<String>();
        ArrayList<String> alPastMessageIDs = new ArrayList<String>();
        boolean bUpdateGUI = true;
        boolean bProcess = true;
        int iPos = -1;
        Parcelable parcelableState;
        public void run()
        {

            while(bUpdateGUI) {
                iPos = -1;
                try
                {
                    iPos = lvMessages.getFirstVisiblePosition();
                    Log.i("First Visible ", String.valueOf(iPos));
                }
                catch (Exception e899)
                {
                    Log.i("First Visible ", e899.getMessage());

                }

                parcelableState = lvMessages.onSaveInstanceState();
                et++;
                if((et>300) && (!bTransmit))
                {
                    bTransmit = true;
                    bProcess = false;
                    try
                    {

                        gtc99.gcm99.stopScan();
                    }
                    catch(Exception e88)
                    {

                    }
                }
                runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            if((gtc99.bGTConnected) && (bProcess))
                            {
                                bGTConnected = true;
                                gid = gtc99.gid;
                            }

                            if(sUnitName == null) sUnitName = "****";
                            if(sUnitName.trim().length() == 0) sUnitName = "****";
                            if(sCallSign == null) sCallSign = "****";
                            if(sCallSign.trim().length() == 0) sCallSign = "****";

                            if(tvUnitName != null) tvUnitName.setText(sUnitName);
                            if(tvCallSign != null) tvCallSign.setText(sCallSign);
                            if(ip.compareTo("0.0.0.0")!= 0)
                            {
                                tvIP.setText(ip);
                            }
                            else
                            {
                                tvIP.setText("No Wifi Connection");
                            }
                            if(gid.length() > 0) {
                                tvGID.setText(gid);
                            }
                            else
                            {
                                tvGID.setText(FakeGID);
                                if(!bTransmit)
                                {
                                    tvGID.setText("NO GoTenna Connection SCANNNING");
                                }
                            }
                            String sOut = "ETX: " + et;
                            Log.i("Update GUI", sOut);
                            String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

                            tvStatus.setText(mydate);
                            String sLat =  "" + lat;
                            String sLng =  "" + lng;
                            tvLatitude.setText(sLat);
                            tvLongitude.setText(sLng);
                            if(bGTConnected)
                            {
                                if(gtc99.alIncoming.size() > 0)
                                {
                                    for(int iK=0; iK < gtc99.alIncoming.size(); iK++)
                                    {
                                        int iKey = alIncomingIDs.indexOf(gtc99.alIncomingIDs.get(iK));
                                        Location newLoc = new Location("");
                                        String saSec[] = gtc99.alIncoming.get(iK).split(sSep);
                                        if(Long.valueOf(saSec[7]) < lStartTime) continue;
                                        newLoc.setBearing(Float.valueOf(saSec[6]));
                                        newLoc.setLatitude(Double.valueOf(saSec[4]));
                                        newLoc.setLongitude(Double.valueOf(saSec[5]));
                                        if(iKey < 0)
                                        {
                                            Log.i("Add GT incoming to Main",gtc99.alIncoming.get(iK) );
                                            alIncomingIDs.add(gtc99.alIncomingIDs.get(iK));
                                            alIncomingRaw.add(gtc99.alIncoming.get(iK));
                                            alCurLoc.add(newLoc);
                                            alPriorLoc.add(newLoc);
                                        }
                                        else
                                        {
                                            Log.i("Update GT incomin",gtc99.alIncoming.get(iK) );

                                            alIncomingRaw.set(iK,gtc99.alIncoming.get(iK));
                                            alPriorLoc.set(iK,alCurLoc.get(iK));
                                            alCurLoc.set(iK,newLoc);
                                        }
                                    }
                                }
                            }

                            alIncomingGlobal.clear();
                            ArrayList<String> alIncomingKey = new ArrayList();
                            allocationList.clear();


                            for(int iK=0; iK < alIncomingRaw.size(); iK++)
                            {
                                String sRcvd = alIncomingRaw.get(iK);
                                String saParts[] = sRcvd.split(sSep);

                                double fLat = Double.valueOf(saParts[4].trim());
                                double  fLng = Double.valueOf(saParts[5].trim());
                                Location locThem = new Location("");
                                Location loc2 = new Location("");
                                locThem.setLatitude(fLat);
                                locThem.setLongitude(fLng);
                                loc2.setLatitude(lat);
                                loc2.setLongitude(lng);
                                loc2.distanceTo(locThem);
                                String sUnitf = saParts[0];
                                String sCallf = saParts[1];
                                String sGIDf = saParts[2];
                                String sIPf = saParts[3];
                                if(sIPf.compareTo("999.999.999.999")==0)
                                {
                                    sIPf = "0.0.0.0";

                                }

                                Log.i("GM  IN 1",sRcvd);
                                if (sGIDf.indexOf("XG") ==0) {

                                    sGIDf = "NG" + sGIDf.substring(3);
                                }

                                String sKey = sUnitf+ sCallf+ sGIDf.trim() + sIPf.trim();
                                int iKey = alIncomingIDs.indexOf(sKey);

                                int heading;
                                if(loc2.bearingTo(locThem) < 0)
                                {
                                    heading = Math.round(loc2.bearingTo(locThem) + 360);
                                }
                                else
                                {
                                    heading = Math.round(loc2.bearingTo(locThem));
                                }

                                heading = heading - Math.round(degree);

                                heading = heading / 30;
                                if(heading <= 0)
                                {
                                    heading = heading + 12;
                                }
                                Location locNow = alCurLoc.get(iKey);
                                Location locWas = alPriorLoc.get(iKey);
                                long lLastTime = 0;
                                try {
                                    lLastTime = Calendar.getInstance().getTimeInMillis() - (Long.valueOf(saParts[6].trim()));
                                }
                                catch(Exception e67)
                                {

                                }

                                String sMsg2 = "Unit Name: " + sUnitf + "\nCall Sign: " + sCallf + "\nGID: " + sGIDf + "\nIP: " + sIPf + "\n"
                                        + "Latitude: " + saParts[4] + "\n"
                                        + "Longitude: " + saParts[5] + "\n"
                                        + "Moved " + locNow.distanceTo(locWas) + " Meters @ Bearing " + locNow.bearingTo(locWas) + " since last locatation"
                                        + "\n" + loc2.distanceTo(locThem) + " Meters from here at bearing " + loc2.bearingTo(locThem)
                                        + "\n"  + heading + " oClock";
                                if(lLastTime > 120000)
                                {
                                    sMsg2 = "Lost Connection " + String.valueOf(lLastTime / 1000.000) + "seconds\n " + sMsg2;
                                }


                                Log.i("UpDate GUI", "Key: " + sKey.trim() + "*");
                                Log.i("UpDate GUI", "MSG: " + sMsg2);

                                int iKF = alIncomingKey.indexOf(sKey.trim());
                                Log.i("UpDate GUI", "Key: Indx " + iKF + " **" + sKey + "---");
                                String sLatX = String.format("%.4f", Double.valueOf(saParts[4]));
                                Log.i("UpDate GUI", "sLatX " + sLatX);
                                if(sLatX.indexOf("\\.")<0)
                                {
                                    sLatX = sLatX + ".0000";
                                }
                                String sP[] = sLatX.split("\\.");
                                String sF4 = "    ";
                                String sZ4 = "0000";
                                if(sP[0].length() < 4)
                                {
                                    sP[0] = sF4.substring(0,(3 - sP[0].length())) + sP[0];
                                }
                                if(sP[1].length() < 4)
                                {
                                    sP[1] = sZ4.substring(0,(3 - sP[0].length())) + sP[0];
                                }
                                sLatX = sP[0] + "." + sP[1];
                                String sLngX = String.format("%.4f", Double.valueOf(saParts[5]));
                                Log.i("UpDate GUI", "sLngX " + sLngX);
                                if(sLngX.indexOf("\\.")<0)
                                {
                                    sLngX = sLngX + ".0000";
                                }
                                sP = sLngX.split("\\.");
                                if(sP[0].length() < 4)
                                {
                                    sP[0] = sF4.substring(0,(3 - sP[0].length())) + sP[0];
                                }
                                if(sP[1].length() < 4)
                                {
                                    sP[1] = sZ4.substring(0,(3 - sP[0].length())) + sP[0];
                                }
                                sLngX = sP[0] + "." + sP[1];

                                String sDist =  String.format("%.4f", loc2.distanceTo(locThem));

                                sP = sDist.split("\\.");
                                if(sP[0].length() < 4)
                                {
                                    sP[0] = sF4.substring(0,(3 - sP[0].length())) + sP[0];
                                }
                                if(sP[1].length() < 4)
                                {
                                    sP[1] = sZ4.substring(0,(3 - sP[0].length())) + sP[0];
                                }
                                sDist = sP[0] + "." + sP[1];

                                if(iKF < 0)
                                {
                                    Log.i("UpDate GUI", "New Key: " + sKey + " --- " + sMsg2);
                                    Log.i("UpDate GUI", "New Key: " + sKey + " --- " + allocationList.size() + " locates");
                                    alIncomingKey.add(sKey.trim());
                                    alIncomingGlobal.add(sMsg2.trim());
                                    allocationList.add(new ContactLocation(sUnitf, sCallf, sGIDf, sIPf, sLatX, sLngX, sDist, String.valueOf(heading)));
                                    Log.i("UpDate GUI", "New KeyX: " + sKey + " --- " + allocationList.size() + " locates");
                                }
                                else
                                {
                                    Log.i("UpDate GUI", "Old Key: " + sKey + " --- " + sMsg2);
                                    Log.i("UpDate GUI", "Old Key: " + sKey + " --- " + allocationList.size() + " locates");

                                    alIncomingGlobal.set(iKF,sMsg2.trim());
                                    allocationList.set(iKF,new ContactLocation(sUnitf,sCallf, sGIDf, sIPf, sLatX, sLngX, sDist, String.valueOf(heading)));
                                    Log.i("UpDate GUI", "Old KeyX: " + sKey + " --- " + allocationList.size() + " locates");
                                }

                                loc2 = null;
                                locThem = null;


                            }

                            try
                            {
                                if(alIncomingGlobal.size() == 0)
                                {
                                    alIncomingGlobal.add("No data on " + et);
                                    alIncomingKey.add("1");
                                    allocationList.add(new ContactLocation("***","No Location", "**", "XXX.XXX.XXX.XXX", "   0.0000", "   0.0000", "   0.0000", "00"));
                                }

                                if(alIncomingGlobal.size() == 1) {
                                    if (alIncomingGlobal.get(0).indexOf("No data") == 0) {
                                        alIncomingGlobal.set(0, "No data on " + et);
                                        allocationList.set(0,(new ContactLocation("***","No Location", "**", "XXX.XXX.XXX.XXX", "   0.0000", "   0.0000", "   0.0000", "00")));
                                    }
                                }

                                myAdapter=new LocationsAdapter(context,R.layout.layoutitems,allocationList);
                                lvMessages.setAdapter(myAdapter);
                                try
                                {
                                    iPos = lvMessages.getFirstVisiblePosition();
                                    Log.i("First Visible Reset", String.valueOf(iPos));
                                }
                                catch (Exception e899)
                                {
                                    Log.i("First Visible Reset ", e899.getMessage());

                                }
                                lvMessages.onRestoreInstanceState(parcelableState);
                                try
                                {
                                    iPos = lvMessages.getFirstVisiblePosition();
                                    Log.i("First Visible Reset 2 ", String.valueOf(iPos));
                                }
                                catch (Exception e899)
                                {
                                    Log.i("First Visible Reset 2", e899.getMessage());

                                }

                                for(int i7=0; i7 < alIncomingGlobal.size(); i7++)
                                {
                                    Log.i("Update GUI List", "Line " + i7 + ": " + alIncomingGlobal.get(i7));
                                }
                            }
                            catch(Exception e88)
                            {
                                Log.i("Update GUI", " Fail " + e88.getMessage());
                            }
                       }

                });
                SystemClock.sleep(1000);
            }

        }

        public void stop()
        {
            bUpdateGUI = false;
        }
    }







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         Log.i("cycle","in create at beginnning");
        context = getApplicationContext();
        Globals.setContext(context);
        setContentView(R.layout.activity_main_x_gextends_gt);
        rlRunTime = (RelativeLayout) findViewById(R.id.rlRunning);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvGID  = (TextView)findViewById(R.id.tvGID);
        tvIP = (TextView) findViewById(R.id.tvIP);
        tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        tvLongitude =(TextView) findViewById(R.id.tvLongitude);
        tvHeading =(TextView) findViewById(R.id.tvHeading);
        tvCallSign = (TextView) findViewById(R.id.tvCallSign);
        tvUnitName = (TextView) findViewById(R.id.tvUnitName);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if(!bCallSign) {
                    try {
                        showInputDialog();

                    } catch (Exception e55) {

                    }
                }
                else
                {
                    btnStart.setText("START");
                    bCallSign=false;
                    sCallSign = "";
                    sUnitName = "";
                    tvUnitName.setText("Unit Name");
                    tvCallSign.setText("Call Sign");


                }
            }
        });
        FakeGID = "NG" + String.valueOf(Calendar.getInstance().getTimeInMillis());
        lLastLocSend = SystemClock.elapsedRealtime();
        rlMessages = (RelativeLayout) findViewById(R.id.rlMessages);
        lvMessages = (ListView) findViewById(R.id.lvMessages);
        allocationList = new ArrayList();
        allocationList.add(new ContactLocation("****","No Location", "**", "XXX.XXX.XXX.XXX", "   0.0000", "   0.0000", "   0.0000", "00"));

        myAdapter=new LocationsAdapter(this,R.layout.layoutitems,allocationList);
        lvMessages.setAdapter(myAdapter);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);



        Log.i("OnStart 1","at PM");
        PackageManager pm = context.getPackageManager();
        int hasPermF = pm.checkPermission(
                Manifest.permission.ACCESS_FINE_LOCATION,
                context.getPackageName());
        int hasPermC = pm.checkPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                context.getPackageName());
        Log.i("OnStart 2","at PM 2 ");
        String sR = "at PM 2B  F - " + String.valueOf(hasPermF) + "  C - " + String.valueOf(hasPermC) + " -- "  + String.valueOf(PackageManager.PERMISSION_GRANTED);
        Log.i("OnStart 3",sR);
        if ((hasPermF != PackageManager.PERMISSION_GRANTED) || (hasPermC != PackageManager.PERMISSION_GRANTED)) {

            Toast.makeText(this, "Needs permission for GPS", Toast.LENGTH_LONG).show();
            finish();
        }
            Log.i("OnStart 4","at PM 2d");
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Log.i("OnStart 5","at PM 3");
            locationListener = new myLocationlistener();
            Log.i("OnStart 6","at PM 3B");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);


        Log.i("OnStart 7","at PM 4");
        Log.i("OnStart 8","Past Loctaion listener");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor == mAccelerometer) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerSet = true;
        } else if (event.sensor == mMagnetometer) {
            System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
            mLastMagnetometerSet = true;
        }
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);
            SensorManager.getOrientation(mR, mOrientation);
            float azimuthInRadians = mOrientation[0];

            float azimuthInDegress = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            try {
//                azimuthInDegress = azimuthInDegress + geoField.getDeclination();
            }
            catch(Exception e55)
            {

            }

            if(azimuthInDegress < 0) azimuthInDegress = azimuthInDegress + 360;
            String sHd =  String.format("%.1f", azimuthInDegress);
            float fHd = Float.valueOf(sHd);

            tvHeading.setText((String.valueOf(fHd)) );
            try
            {


                if((fHd > 354.376) || (fHd < 5.626))
                    tvHeading.setText((String.valueOf(fHd)) + " N");
                if((fHd > 5.625) && (fHd < 16.876))
                    tvHeading.setText((String.valueOf(fHd)) + " NbE");
                if((fHd > 16.875) && (fHd < 28.126))
                    tvHeading.setText((String.valueOf(fHd)) + " NNE");
                if((fHd > 28.125) && (fHd < 39.376))
                    tvHeading.setText((String.valueOf(fHd)) + " NEbN");
                if((fHd > 39.375) && (fHd < 50.626))
                    tvHeading.setText((String.valueOf(fHd)) + " NE");
                if((fHd > 50.625) && (fHd < 61.876))
                    tvHeading.setText((String.valueOf(fHd)) + " NEbE");
                if((fHd > 61.875) && (fHd < 73.126))
                    tvHeading.setText((String.valueOf(fHd)) + " ENE");
                if((fHd > 73.125) && (fHd < 84.376))
                    tvHeading.setText((String.valueOf(fHd)) + " EbN");
                if((fHd > 84.375) && (fHd < 95.626))
                    tvHeading.setText((String.valueOf(fHd)) + " E");
                if((fHd > 95.625) && (fHd < 106.876))
                    tvHeading.setText((String.valueOf(fHd)) + " EbS");
                if((fHd > 106.875) && (fHd < 118.126))
                    tvHeading.setText((String.valueOf(fHd)) + " ESE");
                if((fHd > 118.125) && (fHd < 129.376))
                    tvHeading.setText((String.valueOf(fHd)) + " SEbE");
                if((fHd > 129.375) && (fHd < 140.626))
                    tvHeading.setText((String.valueOf(fHd)) + " SE");
                if((fHd > 140.625) && (fHd < 151.876))
                    tvHeading.setText((String.valueOf(fHd)) + " SEbS");
                if((fHd > 151.875) && (fHd < 163.126))
                    tvHeading.setText((String.valueOf(fHd)) + " SSE");
                if((fHd > 163.125) && (fHd < 174.376))
                    tvHeading.setText((String.valueOf(fHd)) + " SbE");
                if((fHd > 174.375) && (fHd < 185.626))
                    tvHeading.setText((String.valueOf(fHd)) + " S");
                if((fHd > 185.625) && (fHd < 196.876))
                    tvHeading.setText((String.valueOf(fHd)) + " SbW");
                if((fHd > 196.875) && (fHd < 208.126))
                    tvHeading.setText((String.valueOf(fHd)) + " SSW");
                if((fHd > 208.125) && (fHd < 219.376))
                    tvHeading.setText((String.valueOf(fHd)) + " SWbS");
                if((fHd > 219.375) && (fHd < 230.636))
                    tvHeading.setText((String.valueOf(fHd)) + " SW");
                if((fHd > 230.625) && (fHd < 241.876))
                    tvHeading.setText((String.valueOf(fHd)) + " SWbW");
                if((fHd > 241.875) && (fHd < 253.126))
                    tvHeading.setText((String.valueOf(fHd)) + " WSW");
                if((fHd > 253.125) && (fHd < 264.376))
                    tvHeading.setText((String.valueOf(fHd)) + " WbS");
                if((fHd > 264.375) && (fHd < 275.626))
                    tvHeading.setText((String.valueOf(fHd)) + " W");
                if((fHd > 275.625) && (fHd < 286.876))
                    tvHeading.setText((String.valueOf(fHd)) + " WbN");
                if((fHd > 286.875) && (fHd < 298.126))
                    tvHeading.setText((String.valueOf(fHd)) + " WNW");
                if((fHd > 298.125) && (fHd < 309.376))
                    tvHeading.setText((String.valueOf(fHd)) + " NWbW");
                if((fHd > 309.375) && (fHd < 320.626))
                    tvHeading.setText((String.valueOf(fHd)) + " NW");
                if((fHd > 320.625) && (fHd < 331.876))
                    tvHeading.setText((String.valueOf(fHd)) + " NWbN");
                if((fHd > 331.875) && (fHd < 343.126))
                    tvHeading.setText((String.valueOf(fHd)) + " NNW");
                if((fHd > 343.125) && (fHd < 354.376))
                    tvHeading.setText((String.valueOf(fHd)) + " NbW");




            }
            catch(Exception e99)
            {

            }
            degree = azimuthInDegress;
                 mCurrentDegree = -azimuthInDegress;

            String sHd2 = tvHeading.getText().toString().trim();
      //
            //      sHd2 = sHd2 + " dec  " + String.valueOf(geoField.getDeclination());
            if(sHd2.toUpperCase().compareToIgnoreCase("heading")==0)
            {
                tvHeading.setText("");

            }
            else
            {
                tvHeading.setText(sHd2);
            }

        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }
    protected void onStart() {

        super.onStart();
//        if(bAppStarted) return;
        Log.i("cycle","in start at beginnning");
        Log.i("in OnStart 1", "*******");
        bAppStarted = true;
        if(!bRestart) {
           lStartTime =  Calendar.getInstance().getTimeInMillis();
// for the system's orientation sensor registered listeners
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
            mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
            gid = "";


            gtc99 = new GTcontrol();
            gtc99.Setup(getApplicationContext());


            Log.i("Wifi 1", "at beginnning");

            WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInf = wifiMan.getConnectionInfo();
            Log.i("BSSID", wifiInf.getBSSID());

            int ipAddress = wifiInf.getIpAddress();
            ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff), (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
            final DhcpInfo dhcp = wifiMan.getDhcpInfo();
            String sRtrGatewayIP = String.format("%d.%d.%d.%d", (dhcp.gateway & 0xff), (dhcp.gateway >> 8 & 0xff), (dhcp.gateway >> 16 & 0xff), (dhcp.gateway >> 24 & 0xff));
            String sRtrIP = String.format("%d.%d.%d.%d", (dhcp.serverAddress & 0xff), (dhcp.serverAddress >> 8 & 0xff), (dhcp.serverAddress >> 16 & 0xff), (dhcp.serverAddress >> 24 & 0xff));

            Log.i("Wifi 2", "IP: " + ip);
            Log.i("Wifi 2", "Rtr Gateway IP: : " + sRtrGatewayIP);
            Log.i("Wifi 2", "Rtr  IP: : " + sRtrIP);

            alOutGoingGlobal = new ArrayList();
            alIncomingGlobal = new ArrayList();
            alIncomingIDs = new ArrayList();
            alIncomingRaw = new ArrayList();
            alCurLoc = new ArrayList();
            alPriorLoc = new ArrayList();
            long sShout = 010017240022;

            bIamHere = true;
            bGetIamHere = true;
            bSendGlobalMessages = true;
            bGetGlobalMessages = true;


            iah99 = new SendIamHereThread();
            Log.i("thread start 1", "Run Send I am here threea");
            new Thread(iah99, "SendIamHereThread").start();

            giah99 = new GetIamHereThread();
            Log.i("thread start 2", "Run get I am here threea");
            new Thread(giah99, "GetIamHereThread").start();

            ggm99 = new GetGlobalMessagesThread();
            Log.i("thread start 3", "Run get Global Messages threea");
            new Thread(ggm99, "GetGlobalMessagesThread").start();

            sgm99 = new SendGlobalMessagesThread();
            Log.i("thread start 4", "Run get Global Messages threea");
            new Thread(sgm99, "SendGlobalMessagesThread").start();
        }
        bRestart = false;
        if(ip.compareTo("0.0.0.0")!= 0)
        {
            tvIP.setText("IP: " + ip);
        }
        else
        {
            tvIP.setText("No Wifi Connection");
        }
        if(gid.length() > 0) {
            tvGID.setText("GID: " + gid);
        }
        else
        {
            tvGID.setText("NO GoTenna Connection");
        }
        tvStatus.setText("get started");
        rlMessages.setVisibility(View.VISIBLE);
        Log.i("Start 7", "Threads started");
        ug99 = new UpdateGUI();
        Log.i("thread start 0", "Run Update GUI threea");
        new Thread(ug99,"UpdateGUI").start();

        BluetoothAdapter ba = BluetoothAdapter.getDefaultAdapter();
        if(ba.getState() != BluetoothAdapter.STATE_ON) {
         bTransmit = true;
        }
        if(ba.getState() == BluetoothAdapter.STATE_ON) {

            gtc99.Connect();


        }


    }

    protected  void onRestart()
    {
        super.onRestart();
        Log.i("cycle","in Restart at beginnning");
        bRestart = true;
    }

    protected  void onResume()
    {
        super.onResume();
        Log.i("cycle","in Resume at beginnning");
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    protected  void onPause()
    {
        super.onPause();
        Log.i("cycle","in Pause at beginnning");
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
    }

    protected  void onStop()
    {
        super.onStop();
        Log.i("cycle","in Stop at beginnning");


    }

    protected  void onDestroy()
    {
        super.onDestroy();
        Log.i("cycle","in Destrou at beginnning");

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i("BaCK","IN NACK");
        try {
            Log.i("bACK", "Stop on bACK");
             bTransmit = false;

            try {
                gtc99.gcm99.clearConnectedGotennaAddress();
            }
            catch(Exception e55) {
            }
            try
            {
                gtc99.gcm99.disconnect();
        }
        catch(Exception e55) {
        }
        try
        {
        gtc99.gcm99.stopScan();
        }
        catch(Exception e55) {
        }
        bGTConnected = false;

            iah99.stop();
            ggm99.stop();
            sgm99.stop();
            ug99.stop();

        } catch (Exception e) {
            Log.i("bACK","Error: " + e.getMessage());

            e.printStackTrace();
        }
        finish();
        bAppStarted = false;
        Log.i("Back", "Finished");
        Log.i("Back", "Finished2");
    }
    protected void showInputDialog() {

        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity_xGextendsGT.this);
        View promptView = layoutInflater.inflate(R.layout.layout, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity_xGextendsGT.this);
        alertDialogBuilder.setView(promptView);

        final EditText etCallName = (EditText) promptView.findViewById(R.id.etCallName);
        final EditText etUnitName = (EditText) promptView.findViewById(R.id.etUnitName);
        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sUnitName = etUnitName.getText().toString().trim();
                        sCallSign = etCallName.getText().toString().trim();
                         btnStart.setText("STOP");
                        bCallSign = true;


                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public Context getAppContext()
    {
        return context;
    }


}
