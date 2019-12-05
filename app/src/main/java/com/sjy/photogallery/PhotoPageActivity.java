package com.sjy.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;

import androidx.fragment.app.Fragment;

public class PhotoPageActivity extends SingleFragmentActivity {
    private PhotoPageFragment mPhotoPageFragment;

    public static Intent newIntent(Context context, Uri photoPageUri) {
        Intent intent = new Intent(context, PhotoPageActivity.class);
        intent.setData(photoPageUri);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mPhotoPageFragment = PhotoPageFragment.newInstance(getIntent().getData());
        return mPhotoPageFragment;
    }

    @Override
    public void onBackPressed() {
        WebView webView = mPhotoPageFragment.getWebView();
        if (webView.canGoBack()) {
            Log.d("PhotoPageActivity", "Back button can go back");
            webView.goBack();
        } else {
            Log.d("PhotoPageActivity", "Back button has no history");
            super.onBackPressed();
        }
    }
}
