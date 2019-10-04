package com.xgtechnology.xgextendsgt;

/**
 * Created by timr on 11/1/2017.
 */
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.gotenna.sdk.commands.GTCommandCenter;
import com.gotenna.sdk.messages.GTBaseMessageData;
import com.gotenna.sdk.messages.GTGroupCreationMessageData;
import com.gotenna.sdk.messages.GTLocationOnlyMessageData;
import com.gotenna.sdk.messages.GTMessageData;
import com.gotenna.sdk.messages.GTTextOnlyMessageData;
import java.util.ArrayList;
import com.xgtechnology.xgextendsgt.Globals;


public class IncomingMessageManager implements GTCommandCenter.GTMessageListener
{
    //==============================================================================================
    // Class Properties
    //==============================================================================================

    private final ArrayList<IncomingMessageListener> incomingMessageListeners;;
    //==============================================================================================
    // Singleton Methods
    //==============================================================================================

    private IncomingMessageManager()
    {
        incomingMessageListeners = new ArrayList<>();

    }

    private static class SingletonHelper
    {
        private static final IncomingMessageManager INSTANCE = new IncomingMessageManager();
    }

    public static IncomingMessageManager getInstance()
    {
        return SingletonHelper.INSTANCE;
    }

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    public void startListening()
    {
        GTCommandCenter.getInstance().setMessageListener(this);
    }

    public void addIncomingMessageListener(IncomingMessageListener incomingMessageListener)
    {
        synchronized (incomingMessageListeners)
        {
            if (incomingMessageListener != null)
            {
                incomingMessageListeners.remove(incomingMessageListener);
                incomingMessageListeners.add(incomingMessageListener);
            }
        }
    }

    public void removeIncomingMessageListener(IncomingMessageListener incomingMessageListener)
    {
        synchronized (incomingMessageListeners)
        {
            if (incomingMessageListener != null)
            {
                incomingMessageListeners.remove(incomingMessageListener);
            }
        }
    }

    private void notifyIncomingMessage(final Message incomingMessage)
    {
        synchronized (incomingMessageListeners)
        {
            for (IncomingMessageListener incomingMessageListener : incomingMessageListeners)
            {
                incomingMessageListener.onIncomingMessage(incomingMessage);
            }
        }
    }

    private void showGroupInvitationToast(long groupGID)
    {

  //      String message = context.getString(R.string.invited_to_group_toast_text, groupGID);
        String message="";
        Toast toast = Toast.makeText(Globals.getContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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
    }

    @Override
    public void onIncomingMessage(GTBaseMessageData gtBaseMessageData)
    {
        if (gtBaseMessageData instanceof GTLocationOnlyMessageData)
        {
            GTLocationOnlyMessageData gtLocMsg = (GTLocationOnlyMessageData)gtBaseMessageData;

        }
        if (gtBaseMessageData instanceof GTTextOnlyMessageData)
        {
            // Somebody sent us a message, try to parse it
            GTTextOnlyMessageData gtTextOnlyMessageData = (GTTextOnlyMessageData) gtBaseMessageData;
            Message incomingMessage = Message.createMessageFromData(gtTextOnlyMessageData);
            notifyIncomingMessage(incomingMessage);
        }
        else if (gtBaseMessageData instanceof GTGroupCreationMessageData)
        {
            // Somebody invited us to a group!
            GTGroupCreationMessageData gtGroupCreationMessageData = (GTGroupCreationMessageData) gtBaseMessageData;
            showGroupInvitationToast(gtGroupCreationMessageData.getGroupGID());
        }
    }

    //==============================================================================================
    // IncomingMessageListener Interface
    //==============================================================================================

    public interface IncomingMessageListener
    {
        void onIncomingMessage(Message incomingMessage);
    }
}
