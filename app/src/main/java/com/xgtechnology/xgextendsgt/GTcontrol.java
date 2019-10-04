package com.xgtechnology.xgextendsgt;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import com.gotenna.sdk.GoTenna;
import com.gotenna.sdk.bluetooth.BluetoothAdapterManager;
import com.gotenna.sdk.bluetooth.GTConnectionManager;
import com.gotenna.sdk.bluetooth.GTConnectionManager.GTConnectionListener;
import com.gotenna.sdk.bluetooth.GTConnectionManager.GTConnectionState;
import com.gotenna.sdk.bluetooth.GTConnectionManager.GTDeviceType;

import com.gotenna.sdk.commands.GTCommand;
import com.gotenna.sdk.commands.GTCommandCenter;
import com.gotenna.sdk.commands.GTCommandCenter.GTMessageListener;
import com.gotenna.sdk.commands.GTError;
import com.gotenna.sdk.exceptions.GTInvalidAppTokenException;
import com.gotenna.sdk.gids.GIDManager;
import com.gotenna.sdk.interfaces.GTErrorListener;
import com.gotenna.sdk.messages.GTBaseMessageData;
import com.gotenna.sdk.messages.GTGroupCreationMessageData;
import com.gotenna.sdk.messages.GTLocationMessageData;
import com.gotenna.sdk.messages.GTLocationOnlyMessageData;
import com.gotenna.sdk.messages.GTMessageData;
import com.gotenna.sdk.messages.GTTextOnlyMessageData;
import com.gotenna.sdk.responses.GTResponse;
import com.gotenna.sdk.user.User;
import com.gotenna.sdk.user.UserDataStore;

import java.util.ArrayList;

/**
 * Created by timr on 12/1/2017.
 */

public class GTcontrol implements GTConnectionListener {

    String sSep = "" + (char)191;

    private GTCommandCenter.GTMessageListener gtMessageListener;
    private GTCommand.GTCommandResponseListener gtResponseListener;
    private GTErrorListener gtErrorListener;

    private static final String GOTENNA_APP_TOKEN = "EFgMWgsVDEMPWhZHXFwBRQBVCk5HUlgDABRTVBkQDgccFwlEBVxWUQwPXRkARxlH";// TODO: Insert your token

    private UserDataStore uds99;
    public GTCommandCenter gtcc99;
    public GTConnectionManager gcm99;

    private GIDManager gidm99;
    private User curUser;
    public boolean bGTConnected=false;
    ArrayList<String> alIncoming = new ArrayList();
    ArrayList<String> alIncomingIDs = new ArrayList();
    ArrayList<String> alResend = new ArrayList();

    BluetoothAdapterManager bam99;
    String gid;
    Context context;

    public void Setup(Context context) {
        this.context = context;
        try {

            // Must call setApplicationToken before using any SDK methods
            Log.i("in OnStart 1", "Set Token");
            GoTenna.setApplicationToken(context, GOTENNA_APP_TOKEN);
        } catch (GTInvalidAppTokenException e) {
            // Normally, this will never happen
            Log.i("in OnStart 1", "E: " + e.getMessage());


        }
        gcm99 = GTConnectionManager.getInstance();

    }


    public void Connect()
    {



        Log.i("Blue 1", " Get Blooth Status: ");

        try {
            Log.i("Blue 2", " Get Blooth Status: ");


            bam99 = BluetoothAdapterManager.getInstance();

            Log.i("Blue 2", "bam  null check");
            if (bam99 == null) {
                Log.i("Blue 2", "Blooth Status: BAM is null ");

            }
            Log.i("Blue 2", "gbts  null check");
            if (bam99.getBluetoothStatus() == null) {
                Log.i("Blue 2", "Blooth Status: get Bluetooh statis is null ");

            }

            BluetoothAdapterManager.BluetoothStatus bts = bam99.getBluetoothStatus();

            if (bts == BluetoothAdapterManager.BluetoothStatus.SUPPORTED_AND_ENABLED) {
                Log.i("Blue 2", "Blooth Status: there and enabled");
                try
                {
                    gcm99.addGtConnectionListener(this);
                    Log.i("Remove Listener", "Sucess");
                }
                catch(Exception e90)
                {
                    Log.i("Remove Listener" ,"Fail " + e90.getMessage());
                }
                try
                {
                    gcm99.addGtConnectionListener(this);
                    Log.i("Add Listener", "Sucess");
                }
                catch(Exception e90)
                {
                    Log.i("Add Listener" ,"Fail " + e90.getMessage());
                }

                uds99 = UserDataStore.getInstance();

                if (bam99.bluetoothIsEnabled()) {



                    gtcc99 = GTCommandCenter.getInstance();
                    long lMgc = 010017240022;
                    Log.i("GT Pre Join", "State: " + gcm99.getGtConnectionState());
                    gcm99.setDeviceType(GTConnectionManager.GTDeviceType.MESH);
                    gcm99.disconnect();

                    gcm99.clearConnectedGotennaAddress();
                    uds99.deleteCurrentUser();
                    curUser = new User();
                    Log.i("GID Pre 1", "Get Current GID");

                    long lCurGID = curUser.getGID();
                    Log.i("GID Pre 1A", "Current GID: " + String.valueOf(lCurGID));

                    Log.i("GID Pre 2", "Set Current GID to 1");
                    curUser.setGID(1);
                    Log.i("GID Pre 3", "Set Current GID back");
                    curUser.setGID(lCurGID);

                    uds99.setCurrentUser(curUser);
                    Log.i("GID 1", "****");
                    long gidPriv = -1;
                    try {
                        gidPriv = GIDManager.generateRandomizedPersonalGID();


                        Log.i("GID 2", "GID: " + String.valueOf(gidPriv));
                    } catch (Exception e55) {
                        Log.i("GID 3", "Ex: " + e55.getMessage());

                    }
                    Log.i("GID 4", "GID: " + String.valueOf(gidPriv));
                    curUser.setGID(gidPriv);
                    gid = String.valueOf(gidPriv);
                    long lgGID = 1001724022;

                    curUser.addMulticastGroupGID(GIDManager.SHOUT_GID);
                    //          curUser.setName("Tim Romero");
                    uds99.setCurrentUser(curUser);
                    if (!uds99.hasValidUser()) {
                        Log.i("GID 5", "Invalid Usee");

                    }
                    curUser.setName("SAM");
   //                 gcm99.setDeviceType(GTConnectionManager.GTDeviceType.MESH);
                    Log.i("GT Join","Start Connection");
                    gcm99.scanAndConnect(GTConnectionManager.GTDeviceType.MESH);
                    Log.i("GT Joiu wait 0","State " + gcm99.getGtConnectionState());
/*
                    while(!gcm99.isConnected())
                    {
                        Log.i("GT Joiu wait","State " + gcm99.getGtConnectionState());
                        SystemClock.sleep(1000);
                    }

*/
                    Log.i("GT Join","Past Connection");
                    Log.i("GT Joiu wait",gcm99.getGtConnectionState().name());

                } else {
                }
                gtMessageListener = new myIncomingMessagesManager();

                GTCommandCenter.getInstance().setMessageListener(gtMessageListener);



                gtResponseListener = new myResponseManager();
                gtErrorListener = new myErrorManager();

            }
        } catch (Exception e88) {
            Log.i("Blue 2E", "Exception onBlooth Status: " + e88.getMessage());

        }

    }

    public void Disconnect()
    {
        try
        {
            BluetoothAdapterManager.BluetoothStatus  bts = bam99.getBluetoothStatus();

            if(bts == BluetoothAdapterManager.BluetoothStatus.SUPPORTED_AND_ENABLED ) {

                gcm99.disconnect();
                gcm99.clearConnectedGotennaAddress();
                gcm99.stopScan();
            }

        }
        catch(Exception e77)
        {

        }
    }

    public void SendLocation()
    {
     //   GTLocationOnlyMessageData gtlom99 = new GTLocationOnlyMessageData();

    }
   public void SendMessage(String sMsg)
   {
        Log.i("GtC Send Msg ",sMsg);
       byte[] sBytes = sMsg.getBytes();

           gtcc99.sendBroadcastMessage(sBytes, gtResponseListener, gtErrorListener);
       Log.i("GtC Sent Msg ",sMsg);


   }

        // ================================================================================
        // GTConnectionListener Implementation
        // ================================================================================

        @Override
        public void onConnectionStateUpdated(GTConnectionState gtConnectionState)
        {
            Log.i("GT Connect Listener", "State " + gtConnectionState.name());

            if(gtConnectionState.name().trim().compareTo("CONNECTED")==0)
            {
                Log.i("GT Connect Listener", "Set bGTConnnected");
                    bGTConnected = true;
                Log.i("GT Connect Listener", "Set bGTConnnected iss now true");
            }
        }


    private class myErrorManager implements GTErrorListener
    {
        private myErrorManager()
        {

        }

        @Override
        public void onError(GTError gtError)
        {
            Log.i("GT Error", gtError.toString());
        }
    }


    private class myResponseManager implements GTCommand.GTCommandResponseListener
    {
        private myResponseManager()
        {

        }

        //==============================================================================================
        // Singleton Methods
        //==============================================================================================


        private class SingletonHelper
        {
            private final myResponseManager INSTANCE = new myResponseManager();
        }



        @Override
        public void onResponse(GTResponse gtResponse)
        {

            String sSent = new String(gtResponse.getMessageData());
            Log.i("GT Response Listener 2", gtResponse.getResponseCode() + " -- " + sSent + " ^^^ " + gtResponse.getMessageData());

        }
    }

    public class myIncomingMessagesManager implements GTCommandCenter.GTMessageListener
    {


        //==============================================================================================
        // Class Properties
        //==============================================================================================


        //==============================================================================================
        // Singleton Methods
        //==============================================================================================

        private myIncomingMessagesManager()
        {
        }

        private class SingletonHelper
        {
            private final myIncomingMessagesManager INSTANCE = new myIncomingMessagesManager();
        }


        //==============================================================================================
        // Class Instance Methods
        //==============================================================================================

        public void startListening()
        {
            GTCommandCenter.getInstance().setMessageListener(this);

        }



        //==============================================================================================
        // GTMessageListener Implementation
        //==============================================================================================

        @Override
        public void onIncomingMessage(GTMessageData messageData)
        {
            // We do not send any custom formatted messages in this app,
            // but if you wanted to send out messages with your own format, this is where
            // you would receive those messages.


            String sRcvd = new String(messageData.getDataToProcess()).trim();
            Log.i("GT Incomming A ***", " Date Sent" + messageData.getMessageSentDate() + " Msg " + sRcvd);
            String saParts[] = sRcvd.split(sSep);
            String sGIDf = saParts[2];
            String sIPf = saParts[3];
            if(sIPf.compareTo("0.0.0.0") == 0)
            {
                alResend.add(saParts[0] + sSep + saParts[1] + sSep + saParts[2] + sSep + "999.999.999.999" + sSep + saParts[4] + sSep
                        + saParts[5] + sSep + saParts[6] + sSep + saParts[7]);
            }
            if(sIPf.compareTo("999.999.999.999")==0)
            {
                sIPf = "0.0.0.0";

            }
            if (sGIDf.indexOf("XG") ==0) {

                sGIDf = "NG" + sGIDf.substring(2);
            }

            String sMsg = saParts[0]+ sSep + saParts[1]+ sSep + sGIDf + sSep +sIPf;
            for(int iY = 4; iY < saParts.length; iY++)
            {
                sMsg = sMsg + sSep + saParts[iY];
            }
            int iKey = alIncomingIDs.indexOf(saParts[0]+saParts[1]+sGIDf+sIPf);
            if( iKey< 0) {
                Log.i("ADDED TO iNCOMING GT",sMsg);
                alIncoming.add(sMsg);
                alIncomingIDs.add(saParts[0]+saParts[1]+sGIDf+sIPf);
            }
            else
            {
                Log.i("Updated iNCOMING GT N ",sMsg);
                Log.i("Updated iNCOMING GT w ",alIncoming.get(iKey));
                alIncoming.set(iKey,sMsg);
            }




        }
            @Override
        public void onIncomingMessage(GTBaseMessageData gtBaseMessageData)
        {


            Log.i("GT Incomming "," ********* " + gtBaseMessageData.getMessageSentDate() + " Type: " + gtBaseMessageData.getMessageType());


                if (gtBaseMessageData instanceof GTTextOnlyMessageData)
            {
                String sRcvd = new String(gtBaseMessageData.getText()).trim();
                Log.i("GT Incomming B ***", " Date Sent" + gtBaseMessageData.getMessageSentDate() + " Msg " + sRcvd);
                String saParts[] = sRcvd.split(sSep);
                String sGIDf = saParts[1];
                String sIPf = saParts[2];
                if(sIPf.compareTo("0.0.0.0") == 0)
                {
                    alResend.add(saParts[0] + sSep + saParts[1] + sSep + saParts[2] + sSep + "999.999.999.999" + sSep + saParts[4] + sSep
                            + saParts[5] + sSep + saParts[6] + sSep + saParts[7]);
                }

                if(sIPf.compareTo("999.999.999.999")==0)
                {
                    sIPf = "0.0.0.0";

                }
                if (sGIDf.indexOf("XG") ==0) {

                    sGIDf = "NG" + sGIDf.substring(2);
                }

                int iKey = alIncomingIDs.indexOf(saParts[0]+sGIDf+sIPf);
                String sMsg = saParts[0] + sSep + saParts[1] + sSep + sGIDf + sSep +sIPf;
                for(int iY = 4; iY < saParts.length; iY++)
                {
                    sMsg = sMsg + sSep + saParts[iY];
                }
                if( iKey< 0) {
                    Log.i("ADDED TO iNCOMING GT",sMsg);
                    alIncoming.add(sMsg);
                    alIncomingIDs.add(saParts[0]+saParts[1]+sGIDf+sIPf);
                }
                else
                {
                    Log.i("Updated iNCOMING GT N ",sMsg);
                    Log.i("Updated iNCOMING GT w ",alIncoming.get(iKey));
                    alIncoming.get(iKey);
                    alIncoming.set(iKey,sMsg);
                }




            }
            else if (gtBaseMessageData instanceof GTGroupCreationMessageData)
            {
                // Somebody invited us to a group!
                GTGroupCreationMessageData gtGroupCreationMessageData = (GTGroupCreationMessageData) gtBaseMessageData;
            }

        }

        //==============================================================================================
        // IncomingMessageListener Interface
        //==============================================================================================


    }

}
