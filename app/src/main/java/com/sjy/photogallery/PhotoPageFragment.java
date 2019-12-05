package com.sjy.photogallery;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PhotoPageFragment extends VisibleFragment {
    private static final String ARG_URI = "photo_page_url";

    private Uri mUri;
    private WebView mWebView;
    private ProgressBar mProgressBar;

    public WebView getWebView() {
        return mWebView;
    }

    public static PhotoPageFragment newInstance(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);

        PhotoPageFragment fragment = new PhotoPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUri = getArguments().getParcelable(ARG_URI);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_page, container, false);

        mProgressBar = (ProgressBar) v.findViewById(R.id.fragment_photo_page_progress_bar);
        mProgressBar.setMax(100);  // WebChromeClient reports in range 0-100

        mWebView = (WebView) v.findViewById(R.id.fragment_photo_page_web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                if (activity != null) {
                    activity.getSupportActionBar().setSubtitle(title);
                }
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String scheme = request.getUrl().getScheme();
                Intent intent = null;
                Log.d("PhotoPageFragment", "The scheme is " + scheme);
                if (scheme.equalsIgnoreCase("HTTP") || scheme.equalsIgnoreCase("HTTPS")) {
                    return false;
                } else if (scheme.equalsIgnoreCase("tel")) {
                    intent = new Intent(Intent.ACTION_DIAL, request.getUrl());
                } else if (scheme.equalsIgnoreCase("sms")) {
                    intent = new Intent(Intent.ACTION_SENDTO, request.getUrl());
                } else if (scheme.equalsIgnoreCase("mailto")) {
                    String mail = request.getUrl().toString().replaceFirst("mailto:", "");
                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_EMAIL, mail );
                    // intent.putExtra(Intent.EXTRA_SUBJECT, "Subject"); // if you want extra
                    // intent.putExtra(Intent.EXTRA_TEXT, "I'm email body."); // if you want extra

                    intent = Intent.createChooser(intent, "Send Email");
                } else if (scheme.equalsIgnoreCase("intent")) {
                    try {
                        intent = Intent.parseUri(request.getUrl().toString(), Intent.URI_INTENT_SCHEME);
                        PackageManager packageManager = getActivity().getPackageManager();
                        if (packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
                            intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("market://details?id=" + intent.getPackage()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    return true;
                }
                startActivity(intent);
                return true;
            }
        });

        mWebView.loadUrl(mUri.toString());
        return v;
    }
}
