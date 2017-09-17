package com.Temoc.demo.bots;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amazonaws.mobile.auth.core.IdentityManager;
import com.amazonaws.mobileconnectors.lex.interactionkit.Response;
import com.amazonaws.mobileconnectors.lex.interactionkit.config.InteractionConfig;
import com.Temoc.R;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobileconnectors.lex.interactionkit.ui.InteractiveVoiceView;
import com.amazonaws.mobile.util.ViewHelper;

import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConversationalBotVoiceFragment extends Fragment implements InteractiveVoiceView.InteractiveVoiceListener{

    final private String TAG = "ConversationalBotVoice";
    private Context context;
    private Bot currentBot ;
    private InteractiveVoiceView voiceView;
    private static String ARGUMENT_DEMO_CONVERSATIONAL_BOT = "BOT";
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 100;
    private Boolean mAudioPermissionGranted = false;
    private Boolean mShowRationale = true;
    private AWSCredentialsProvider credentialsProvider;

    public static ConversationalBotVoiceFragment newInstance(Bot curentBot) {
        ConversationalBotVoiceFragment fragment = new ConversationalBotVoiceFragment();
        Bundle args = new Bundle();
        args.putSerializable(ConversationalBotVoiceFragment.ARGUMENT_DEMO_CONVERSATIONAL_BOT, curentBot);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_conversational_bot_voice, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        final Bundle args = getArguments();
        currentBot = (Bot) args.getSerializable(ARGUMENT_DEMO_CONVERSATIONAL_BOT);
        voiceView = (InteractiveVoiceView) view.findViewById(R.id.voiceInterface);
        voiceView.setEnabled(false);
        //request microphone permissions
        requestPermission();
    }

    private void requestPermission(){
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.RECORD_AUDIO) && mShowRationale) {
                mShowRationale = false;
                ViewHelper.showDialog(getActivity(),
                        getString(R.string.feature_app_conversational_bots_permissions_header),
                        getString(R.string.feature_app_conversational_bots_permissions_string),
                        "Proceed", new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermission();
                            }
                        },
                        "Cancel", new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        });
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);

            }
        }else{
            mAudioPermissionGranted = true;
            voiceView.setEnabled(true);
            initializeLexSDK();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    mAudioPermissionGranted = true;
                    voiceView.setEnabled(true);
                    initializeLexSDK();
                } else {
                    mAudioPermissionGranted = false;
                    mShowRationale = true;
                }
                return;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentBot == null) {
            final Bundle args = getArguments();
            currentBot = (Bot) args.getSerializable(ARGUMENT_DEMO_CONVERSATIONAL_BOT);
        }
        credentialsProvider = IdentityManager.getDefaultIdentityManager().getUnderlyingProvider();
    }

    /**
     * Initializes Lex client.
     */
    private void initializeLexSDK() {
        if (mAudioPermissionGranted) {
            Log.d(TAG, "Lex Client");
            credentialsProvider = IdentityManager.getDefaultIdentityManager().getUnderlyingProvider();
            voiceView.setInteractiveVoiceListener(this);
            voiceView.getViewAdapter().setAwsRegion(currentBot.getRegion());
            voiceView.getViewAdapter().setCredentialProvider(credentialsProvider);
            voiceView.getViewAdapter().setInteractionConfig(new InteractionConfig(currentBot.getBotName(), currentBot.getBotAlias()));
        }
    }

    @Override
    public void dialogReadyForFulfillment(final Map<String, String> slots, final String intent) {
        Log.d(TAG, String.format(
                Locale.US,
                "Dialog ready for fulfillment:\n\tIntent: %s\n\tSlots: %s",
                intent,
                slots.toString()));
    }

    @Override
    public void onResponse(final Response response) {
        Log.d(TAG, "Bot response: " + response.getTextResponse());
    }

    @Override
    public void onError(final String responseText, final Exception e) {
        Log.e(TAG, "Error: " + responseText, e);
    }
}
