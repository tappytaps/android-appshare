package com.tappytaps.android.appsharing;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QrCodeFragment extends DialogFragment {

    static final String TAG = "QrCodeFragment";

    private static final String KEY_URL = "url";
    private static final String KEY_STYLE_RES = "style_res";

    static QrCodeFragment newInstance(String url, @StyleRes int styleRes) {
        QrCodeFragment fragment = new QrCodeFragment();
        Bundle args = new Bundle();
        args.putString(KEY_URL, url);
        args.putInt(KEY_STYLE_RES, styleRes);
        fragment.setArguments(args);

        return fragment;
    }

    private String url;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        @StyleRes int styleRes = R.style.Theme_AppCompat_Light;

        Bundle args = getArguments();
        if (args != null) {
            url = args.getString(KEY_URL);
            styleRes = args.getInt(KEY_STYLE_RES);
        }

        setStyle(STYLE_NO_FRAME, styleRes);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_code, container, false);
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

        final ImageView ivQrCode = view.findViewById(R.id.ivQrCode);
        final ProgressBar progressBar = view.findViewById(R.id.progressBar);
        final QRCodeWriter writer = new QRCodeWriter();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final BitMatrix bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, 512, 512);
                    final int width = bitMatrix.getWidth();
                    final int height = bitMatrix.getHeight();
                    final Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                    for (int x = 0; x < width; x++) {
                        for (int y = 0; y < height; y++) {
                            bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.TRANSPARENT);
                        }
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ivQrCode.setImageBitmap(bmp);
                            progressBar.setVisibility(View.GONE);
                        }
                    });

                } catch (WriterException e) {
                    Log.d(TAG, "QR code generator failed.");
                }
            }
        }).start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }
}
