package utsav2.researchproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;

import com.getpebble.android.kit.Constants;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import org.json.JSONException;

import java.util.UUID;

/**
 *
 * Created by utsav on 4/10/15.
 */
public class ResearchDataHandler extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Constants.INTENT_APP_RECEIVE)) {
            final UUID receivedUuid = (UUID) intent.getSerializableExtra(Constants.APP_UUID);

            // Pebble-enabled apps are expected to be good citizens and only inspect broadcasts containing their UUID
            if (!MainActivity.APP_UUID.equals(receivedUuid)) {
                return;
            }

            final int transactionId = intent.getIntExtra(Constants.TRANSACTION_ID, -1);

            final String jsonData = intent.getStringExtra(Constants.MSG_DATA);
            if (jsonData == null || jsonData.isEmpty()) {
                return;
            }

            try {
                final PebbleDictionary data = PebbleDictionary.fromJson(jsonData);
                MyApplication ctx = ((MyApplication)context.getApplicationContext());
                if(ctx != null){
                    MainActivity mainActivity = ctx.mainActivity;
                    if(mainActivity != null){
                        mainActivity.speak(data);
                    }
                }
                PebbleKit.sendAckToPebble(context, transactionId);
            } catch (JSONException e) {
                PebbleKit.sendNackToPebble(context, transactionId);
            }
        }
    }
}
