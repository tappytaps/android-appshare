package com.tappytaps.android.appsharing;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.List;

import de.cketti.mailto.EmailIntentBuilder;

public class ShareAppFragment extends DialogFragment {

    public static class Builder {
        @Nullable private String simpleMessage = null;
        @Nullable private String emailSubject = null;
        @Nullable private String facebookQuote = null;
        @Nullable private String twitterMessage = null;
        @Nullable private String url = null;
        @Nullable private String qrCodeUrl = null;
        @StyleRes private int styleRes = R.style.Theme_AppCompat_Light;

        public Builder setSimpleMessage(String simpleMessage) {
            this.simpleMessage = simpleMessage;
            return this;
        }

        public Builder setEmailSubject(String emailSubject) {
            this.emailSubject = emailSubject;
            return this;
        }

        public Builder setFacebookQuote(String facebookQuote) {
            this.facebookQuote = facebookQuote;
            return this;
        }

        public Builder setTwitterMessage(String twitterMessage) {
            this.twitterMessage = twitterMessage;
            return this;
        }

        public Builder setUrl(@NonNull String url) {
            this.url = url;
            return this;
        }

        public Builder setQrCodeUrl(String qrCodeUrl) {
            this.qrCodeUrl = qrCodeUrl;
            return this;
        }

        public Builder setStyle(@StyleRes int styleRes) {
            this.styleRes = styleRes;
            return this;
        }

        public void show(FragmentManager fragmentManager) {
            if (url == null) {
                throw new IllegalStateException("At least an URL is required.");
            }

            final String simpleMessage = this.simpleMessage != null ? this.simpleMessage + " " + url : url;
            final String facebookQuote = this.facebookQuote != null ? this.facebookQuote : this.simpleMessage;
            final String twitterMessage = this.twitterMessage != null ? this.twitterMessage.replace("{url}", url) : url;
            final String qrCodeUrl = this.qrCodeUrl != null ? this.qrCodeUrl : url;

            Bundle args = new Bundle();
            args.putString(KEY_SIMPLE_MESSAGE, simpleMessage);
            args.putString(KEY_EMAIL_SUBJECT, emailSubject);
            args.putString(KEY_FACEBOOK_QUOTE, facebookQuote);
            args.putString(KEY_TWITTER_MESSAGE, twitterMessage);
            args.putString(KEY_URL, url);
            args.putString(KEY_QR_CODE_URL, qrCodeUrl);
            args.putInt(KEY_STYLE_RES, styleRes);

            newInstance(args).show(fragmentManager, TAG);
        }
    }

    private static ShareAppFragment newInstance(Bundle args) {
        ShareAppFragment fragment = new ShareAppFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private static final String KEY_SIMPLE_MESSAGE = "simple_message";
    private static final String KEY_EMAIL_SUBJECT = "email_subject";
    private static final String KEY_FACEBOOK_QUOTE = "facebook_quote";
    private static final String KEY_TWITTER_MESSAGE = "twitter_message";
    private static final String KEY_URL = "url";
    private static final String KEY_QR_CODE_URL = "qr_code_url";
    private static final String KEY_STYLE_RES = "style_res";

    private static final String TAG = "ShareAppFragment";

    private static final String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";
    private static final String MESSENGER_PACKAGE_NAME = "com.facebook.orca";
    private static final String TWITTER_PACKAGE_NAME = "com.twitter.android";
    private static final String VKONTAKTE_PACKAGE_NAME = "com.vkontakte.android";
    private static final String WE_CHAT_PACKAGE_NAME = "com.tencent.mm";
    private static final String ODNOKLASSNIKI_PACKAGE_NAME = "ru.ok.android";
    private static final String VIBER_PACKAGE_NAME = "com.viber.voip";
    private static final String WHATS_APP_PACKAGE_NAME = "com.whatsapp";

    private @StyleRes int styleRes;

    private String simpleMessage;
    private String emailSubject;
    private String facebookQuote;
    private String twitterMessage;
    private String url;
    private String qrCodeUrl;

    private ConstraintLayout layoutFacebook;
    private ConstraintLayout layoutMessenger;
    private ConstraintLayout layoutTwitter;
    private ConstraintLayout layoutVKontakte;
    private ConstraintLayout layoutWeChat;
    private ConstraintLayout layoutOdnoklassniki;
    private ConstraintLayout layoutViber;
    private ConstraintLayout layoutWhatsApp;
    private ConstraintLayout layoutEmail;
    private ConstraintLayout layoutSms;
    private ConstraintLayout layoutQrCode;
    private ConstraintLayout layoutCopyLink;
    private ConstraintLayout layoutMore;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();

        if (args != null) {
            simpleMessage = args.getString(KEY_SIMPLE_MESSAGE);
            emailSubject = args.getString(KEY_EMAIL_SUBJECT, "");
            facebookQuote = args.getString(KEY_FACEBOOK_QUOTE);
            twitterMessage = args.getString(KEY_TWITTER_MESSAGE);
            url = args.getString(KEY_URL);
            qrCodeUrl = args.getString(KEY_QR_CODE_URL);
            styleRes = args.getInt(KEY_STYLE_RES);
        }

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
        layoutVKontakte = view.findViewById(R.id.vkontakte);
        layoutWeChat = view.findViewById(R.id.wechat);
        layoutOdnoklassniki = view.findViewById(R.id.odnoklassniki);
        layoutViber = view.findViewById(R.id.viber);
        layoutWhatsApp = view.findViewById(R.id.whatsapp);
        layoutEmail = view.findViewById(R.id.email);
        layoutSms = view.findViewById(R.id.sms);
        layoutQrCode = view.findViewById(R.id.qrcode);
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
        setupItem(layoutVKontakte, ContextCompat.getDrawable(getContext(), R.drawable.ic_vkontakte), getString(R.string.btn_vkontakte));
        setupItem(layoutWeChat, ContextCompat.getDrawable(getContext(), R.drawable.ic_wechat), getString(R.string.btn_wechat));
        setupItem(layoutOdnoklassniki, ContextCompat.getDrawable(getContext(), R.drawable.ic_odnoklassniki), getString(R.string.btn_odnoklassniki));
        setupItem(layoutViber, ContextCompat.getDrawable(getContext(), R.drawable.ic_viber), getString(R.string.btn_viber));
        setupItem(layoutWhatsApp, ContextCompat.getDrawable(getContext(), R.drawable.ic_whatsapp), getString(R.string.btn_whats_app));
        setupItem(layoutEmail, ContextCompat.getDrawable(getContext(), R.drawable.ic_message), getString(R.string.btn_email));
        setupItem(layoutSms, ContextCompat.getDrawable(getContext(), R.drawable.ic_sms), getString(R.string.btn_sms));
        setupItem(layoutQrCode, ContextCompat.getDrawable(getContext(), R.drawable.ic_qr_code), getString(R.string.btn_qr_code));
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

        if (isPackageInstalled(VKONTAKTE_PACKAGE_NAME)) {
            layoutVKontakte.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareViaVKontakte();
                }
            });
        } else {
            layoutVKontakte.setVisibility(View.GONE);
        }

        if (isPackageInstalled(WE_CHAT_PACKAGE_NAME)) {
            layoutWeChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareViaWeChat();
                }
            });
        } else {
            layoutWeChat.setVisibility(View.GONE);
        }

        if (isPackageInstalled(ODNOKLASSNIKI_PACKAGE_NAME)) {
            layoutOdnoklassniki.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareVieOdnoklassniki();
                }
            });
        } else {
            layoutOdnoklassniki.setVisibility(View.GONE);
        }

        if (isPackageInstalled(VIBER_PACKAGE_NAME)) {
            layoutViber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shareViaViber();
                }
            });
        } else {
            layoutViber.setVisibility(View.GONE);
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

        layoutSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareViaSms();
            }
        });

        layoutQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showQrCode();
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
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(url))
                .setQuote(facebookQuote)
                .build();

        ShareDialog.show(this, content);
    }

    private void shareViaMessenger() {
        Intent sharingIntent = new Intent();
        sharingIntent.setAction(Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, simpleMessage);
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
        sharingIntent.setClassName(TWITTER_PACKAGE_NAME, TWITTER_PACKAGE_NAME + ".PostActivity");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, twitterMessage);

        try {
            startActivity(sharingIntent);
        } catch (Exception e) {
            Log.d(TAG, "Twitter not present, cannot share.");
        }
    }

    private void shareViaVKontakte() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, facebookQuote + " " + url);
        List<ResolveInfo> matches = getContext().getPackageManager().queryIntentActivities(intent, 0);

        for (ResolveInfo info : matches) {
            if (info.activityInfo.packageName.toLowerCase().startsWith(VKONTAKTE_PACKAGE_NAME)) {
                intent.setPackage(info.activityInfo.packageName);
                break;
            }
        }

        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, "VKontakte not present, cannot share.");
        }
    }

    private void shareViaWeChat() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.setPackage(WE_CHAT_PACKAGE_NAME);
        i.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(i);
        } catch (Exception e) {
            Log.d(TAG, "WeChat not present, cannot share.");
        }
    }

    private void shareVieOdnoklassniki() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.setPackage(VIBER_PACKAGE_NAME);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, simpleMessage);

        try {
            startActivity(sharingIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Log.d(TAG, "Odnoklassniki not present, cannot share.");
        }
    }

    private void shareViaViber() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.setPackage(ODNOKLASSNIKI_PACKAGE_NAME);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, simpleMessage);

        try {
            startActivity(sharingIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Log.d(TAG, "Viber not present, cannot share.");
        }
    }

    private void shareViaWhatsApp() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.setPackage(WHATS_APP_PACKAGE_NAME);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, simpleMessage);

        try {
            startActivity(sharingIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Log.d(TAG, "WhatsApp not present, cannot share.");
        }
    }

    private void shareViaEmail() {
        Intent sharingIntent = EmailIntentBuilder.from(getContext())
                .subject(emailSubject)
                .body(simpleMessage)
                .build();
        try {
            getContext().startActivity(sharingIntent);
        } catch (Exception e) {
            Log.d(TAG, "Email client not present, cannot share.");
        }
    }

    private void shareViaSms() {
        Intent sharingIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:"));
        sharingIntent.putExtra("sms_body", simpleMessage);
        startActivity(sharingIntent);
    }

    private void showQrCode() {
        QrCodeFragment qrCodeFragment = QrCodeFragment.newInstance(qrCodeUrl, styleRes);
        qrCodeFragment.show(getChildFragmentManager(), QrCodeFragment.TAG);
    }

    private void copyLink() {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(simpleMessage, url);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(getContext(), R.string.clipboard_copied, Toast.LENGTH_SHORT).show();
    }

    private void openShareChooser() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, simpleMessage);
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
