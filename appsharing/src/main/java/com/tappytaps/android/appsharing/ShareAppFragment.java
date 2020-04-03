package com.tappytaps.android.appsharing;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import de.cketti.mailto.EmailIntentBuilder;

public class ShareAppFragment extends DialogFragment {

    public static class Builder {
        private String description;
        private String url;
        @StringRes private int styleRes = 0;

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setStyle(@StyleRes int styleRes) {
            this.styleRes = styleRes;
            return this;
        }

        public void show(FragmentManager fragmentManager) {
            ShareAppFragment instance = newInstance(description, url);

            if (styleRes != 0) {
                instance.styleRes = styleRes;
            }

            instance.show(fragmentManager, TAG);
        }
    }

    private static ShareAppFragment newInstance(String description, String url) {
        ShareAppFragment fragment = new ShareAppFragment();
        Bundle args = new Bundle();
        args.putString(EXTRA_DESCRIPTION, description);
        args.putString(EXTRA_URL, url);
        fragment.setArguments(args);

        return fragment;
    }

    private static final String EXTRA_DESCRIPTION = "description";
    private static final String EXTRA_URL = "url";

    private static final String TAG = "ShareAppFragment";

    private static final String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";
    private static final String MESSENGER_PACKAGE_NAME = "com.facebook.orca";
    private static final String TWITTER_PACKAGE_NAME = "com.twitter.android";
    private static final String WHATS_APP_PACKAGE_NAME = "com.whatsapp";

    private @StyleRes int styleRes = R.style.Theme_AppCompat_Light;

    private String description;
    private String url;

    private ConstraintLayout layoutFacebook;
    private ConstraintLayout layoutMessenger;
    private ConstraintLayout layoutTwitter;
    private ConstraintLayout layoutWhatsApp;
    private ConstraintLayout layoutEmail;
    private ConstraintLayout layoutCopyLink;
    private ConstraintLayout layoutMore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(STYLE_NO_FRAME, styleRes);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_share_app, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            description = args.getString(EXTRA_DESCRIPTION);
            url = args.getString(EXTRA_URL);
        }

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_cross);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        layoutFacebook = view.findViewById(R.id.facebook);
        layoutMessenger = view.findViewById(R.id.messenger);
        layoutTwitter = view.findViewById(R.id.twitter);
        layoutWhatsApp = view.findViewById(R.id.whatsapp);
        layoutEmail = view.findViewById(R.id.email);
        layoutCopyLink = view.findViewById(R.id.copylink);
        layoutMore = view.findViewById(R.id.more);

        setupViews();
        prepareListenersOrHideItems();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    private void setupViews() {
        setupItem(layoutFacebook, ContextCompat.getDrawable(getContext(), R.drawable.ic_facebook), getString(R.string.btn_facebook));
        setupItem(layoutMessenger, ContextCompat.getDrawable(getContext(), R.drawable.ic_messenger), getString(R.string.btn_messenger));
        setupItem(layoutTwitter, ContextCompat.getDrawable(getContext(), R.drawable.ic_twitter), getString(R.string.btn_twitter));
        setupItem(layoutWhatsApp, ContextCompat.getDrawable(getContext(), R.drawable.ic_whatsapp), getString(R.string.btn_whats_app));
        setupItem(layoutEmail, ContextCompat.getDrawable(getContext(), R.drawable.ic_message), getString(R.string.btn_email));
        setupItem(layoutCopyLink, ContextCompat.getDrawable(getContext(), R.drawable.ic_copy), getString(R.string.btn_copy_link));
        setupItem(layoutMore, ContextCompat.getDrawable(getContext(), R.drawable.ic_more), getString(R.string.btn_more));
    }

    private void setupItem(View item, Drawable icon, String description) {
        ImageView imageView = item.findViewById(R.id.ivIcon);
        TextView textView = item.findViewById(R.id.tvName);

        imageView.setImageDrawable(icon);
        textView.setText(description);
    }

    private void prepareListenersOrHideItems() {
        if (isPackageInstalled(FACEBOOK_PACKAGE_NAME)) {
            layoutFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareViaFacebook();
                }
            });
        } else {
            layoutFacebook.setVisibility(View.GONE);
        }

        if (isPackageInstalled(MESSENGER_PACKAGE_NAME)) {
            layoutMessenger.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareViaMessenger();
                }
            });
        } else {
            layoutMessenger.setVisibility(View.GONE);
        }

        if (isPackageInstalled(TWITTER_PACKAGE_NAME)) {
            layoutTwitter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareViaTwitter();
                }
            });
        } else {
            layoutTwitter.setVisibility(View.GONE);
        }

        if (isPackageInstalled(WHATS_APP_PACKAGE_NAME)) {
            layoutWhatsApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareViaWhatsApp();
                }
            });
        } else {
            layoutWhatsApp.setVisibility(View.GONE);
        }

        layoutEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareViaEmail();
            }
        });

        layoutCopyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copyLink();
            }
        });

        layoutMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openShareChooser();
            }
        });
    }

    private void shareViaFacebook() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setClassName(FACEBOOK_PACKAGE_NAME,
                FACEBOOK_PACKAGE_NAME + ".activity.composer.ImplicitShareIntentHandler");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, description);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, url);

        try {
            startActivity(sharingIntent);
        } catch (Exception e) {
            Log.d(TAG, "Facebook not present, cannot share.");
        }
    }

    private void shareViaMessenger() {
        Intent sharingIntent = new Intent();
        sharingIntent.setAction(Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, description);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, url);
        sharingIntent.setType("text/plain");
        sharingIntent.setPackage(MESSENGER_PACKAGE_NAME);

        try {
            startActivity(sharingIntent);
        }
        catch (android.content.ActivityNotFoundException ex) {
            Log.d(TAG, "Messenger not present, cannot share.");
        }
    }

    private void shareViaTwitter() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setClassName(TWITTER_PACKAGE_NAME,
                TWITTER_PACKAGE_NAME + ".PostActivity");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, description);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, url);

        try {
            startActivity(sharingIntent);
        } catch (Exception e) {
            Log.d(TAG, "Twitter not present, cannot share.");
        }
    }

    private void shareViaWhatsApp() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.setPackage(WHATS_APP_PACKAGE_NAME);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, description);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, url);

        try {
            startActivity(sharingIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Log.d(TAG, "WhatsApp not present, cannot share.");
        }
    }

    private void shareViaEmail() {
        Intent sharingIntent = EmailIntentBuilder.from(getContext())
                .subject(description)
                .body(url)
                .build();
        try {
            getContext().startActivity(sharingIntent);
        } catch (Exception e) {
            Log.d(TAG, "Email client not present, cannot share.");
        }
    }

    private void copyLink() {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(description, url);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(getContext(), R.string.clipboard_copied, Toast.LENGTH_SHORT).show();
    }

        /* share SMS
        String message = description + "\r\n" + url;
        Intent sharingIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"));
        sharingIntent.putExtra("sms_body", message);
        //sharingIntent.setType("vnd.android-dir/mms-sms");
        startActivity(sharingIntent);*/

    private void openShareChooser() {
        String message = description + "\r\n" + url;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private boolean isPackageInstalled(String packageName) {
        try {
            return getContext().getPackageManager().getApplicationInfo(packageName, 0).enabled;
        } catch (Exception e) {
            return false;
        }
    }
}
