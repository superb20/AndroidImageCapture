package com.superb20.imagecapture;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.superb20.imagecapture.Common.SingleFragmentActivity;

public class MainActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new MainFragment();
    }
}

