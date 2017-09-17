
package com.Temoc.demo.bots;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.Temoc.R;
import com.Temoc.demo.DemoFragmentBase;

import java.util.Locale;

import static com.Temoc.demo.bots.ConversationalBotDemoFragment.BotType.TEXT;

public class ConversationalBotDemoFragment extends DemoFragmentBase {

    private static final String ARGUMENT_DEMO_CONVERSATIONAL_BOT = "ARGUMENT_DEMO_CONVERSATIONAL_BOT_NAME";
    private static final String BOT_DEMO_NAME = "conversational_bots";

    private Bot currentBot;

    public static ConversationalBotDemoFragment newInstance(final Bot bot) {
        ConversationalBotDemoFragment fragment = new ConversationalBotDemoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ConversationalBotDemoFragment.ARGUMENT_DEMO_CONVERSATIONAL_BOT, bot);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_conversational_bots_demo, container, false);
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Bundle args = getArguments();
        currentBot = (Bot) args.getSerializable("tag");

        final ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(currentBot.getBotName());
        }

        final TextView tvDescription = (TextView) view.findViewById(
                R.id.text_conversational_bot_demo_feature_overview);

        tvDescription.setText(currentBot.getDescription());

        final TextView tvPoweredBy = (TextView) view.findViewById(
                R.id.text_conversational_bot_demo_feature_powered_by);
        final TextView tvBotIntentsHeader = (TextView) view
                .findViewById(R.id.text_demo_feature_description_heading);

        if(currentBot.getHelpCommands()== null || currentBot.getHelpCommands().length == 0){
            tvBotIntentsHeader.setVisibility(View.INVISIBLE);
        }

        final ListView listBotUtterances = (ListView) view.findViewById(R.id.botUtterances);

        final ArrayAdapter<String> utteranceAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.conversational_bot_sample_utterance_layout) {
            @Override
            public View getView(final int position, final View convertView,
                    final ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    view = getActivity().getLayoutInflater()
                            .inflate(R.layout.conversational_bot_sample_utterance_layout, parent,
                                    false);
                }
                final String item = String.format(Locale.US, "\"%s\"", getItem(position));

                final TextView title = (TextView) view.findViewById(R.id.txtViewBotUtterance);
                title.setText(item);

                return view;
            }
        };
        utteranceAdapter.addAll(currentBot.getHelpCommands());
        listBotUtterances.setAdapter(utteranceAdapter);

        tvPoweredBy.setText(R.string.feature_app_conversational_bots_powered_by);

        final ArrayAdapter<BotType> adapter = new ArrayAdapter<BotType>(
                getActivity(), R.layout.list_item_icon_text_with_subtitle) {
            @Override
            public View getView(final int position, final View convertView,
                    final ViewGroup parent) {
                View view = convertView;
                if (view == null) {
                    view = getActivity().getLayoutInflater()
                            .inflate(R.layout.list_item_demo_button_icon_text, parent, false);
                }
                final BotType item = getItem(position);
                final ImageView imageView = (ImageView) view.findViewById(R.id.list_item_icon);
                imageView.setVisibility(View.GONE);
                final TextView title = (TextView) view.findViewById(R.id.list_item_title);
                title.setText(item.name);
                int padding = dpToPixel(10);
                title.setPadding(padding, padding, padding, padding);
                return view;
            }
        };

        adapter.addAll(BotType.values());

        final ListView listView = (ListView) view.findViewById(R.id.botsDemoOptions);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                final BotType item = adapter.getItem(position);
                final AppCompatActivity activity = (AppCompatActivity) getActivity();
                if (activity != null) {
                    BotType botType = adapter.getItem(position);
                    if (TEXT.equals(botType)) {
                        final Fragment fragment = ConversationalBotTextFragment
                                .newInstance(currentBot);

                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_fragment_container, fragment, TEXT.name)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .addToBackStack(null)
                                .commit();

                        // Set the title for the fragment.
                        final ActionBar actionBar = activity.getSupportActionBar();
                        if (actionBar != null) {
                            actionBar.setTitle(R.string.feature_app_conversational_bots_text_title);
                        }
                    } else {
                        final Fragment fragment = ConversationalBotVoiceFragment
                                .newInstance(currentBot);

                        activity.getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.main_fragment_container, fragment, TEXT.name)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .addToBackStack(null)
                                .commit();

                        // Set the title for the fragment.
                        final ActionBar actionBar = activity.getSupportActionBar();
                        if (actionBar != null) {
                            actionBar.setTitle(R.string.feature_app_conversational_bots_voice_title);
                        }
                    }
                }
            }
        });

        listView.setBackgroundColor(Color.WHITE);

    }

    private int dpToPixel(final int sizeInDp) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (sizeInDp * scale + 0.5f);
    }

    enum BotType {
        VOICE("Voice chat"), TEXT("Text chat");

        private String name;

        BotType(String name) {
            this.name = name;
        }

    }
}
