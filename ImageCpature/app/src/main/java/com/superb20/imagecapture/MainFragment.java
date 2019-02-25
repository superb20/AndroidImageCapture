package com.superb20.imagecapture;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.superb20.imagecapture.Common.PermissionHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by superb20 on 2019-02-25.
 */

public class MainFragment extends Fragment implements View.OnClickListener {
    private final static String TAG = "MainFragment";
    private static final int REQUEST_PHOTO = 0;

    private File mPhotoFile = null;

    @Override
    public void onResume() {
        super.onResume();

        if(!PermissionHelper.hasStoragePermission(getActivity())) {
            Log.d(TAG, "has not storage permission");
            PermissionHelper.requestStoragePermission(getActivity());
            return;
        }

        mPhotoFile = getPhotoFile();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, parent, false);
        v.findViewById(R.id.btn_capture).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.btn_capture:
                imageCapture();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;

        if (requestCode == REQUEST_PHOTO) {
            notifyMediaStoreScanner(mPhotoFile);
        }
    }

    private void imageCapture() {
        Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(getActivity().getPackageManager()) != null;

        if (canTakePhoto) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(captureImage, REQUEST_PHOTO);
        }
    }

    private void notifyMediaStoreScanner(File file) {
        try {
            MediaStore.Images.Media.insertImage(getContext().getContentResolver(), file.getAbsolutePath(), file.getName(), null);
            getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        } catch (FileNotFoundException e) {
            Log.e(TAG, "notifyMediaStoreScanner fail : " + e.toString());
            e.printStackTrace();
        }
    }

    private String getPhotoFileName() {
        String timeStamp = new SimpleDateFormat("yyyy_MM_dd_HHmmss").format(new Date());

        return "IMAGE_" + timeStamp + ".jpg";
    }

    private File getPhotoFile() {
        File externalFilesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);

        if (externalFilesDir == null) {
            Log.e(TAG, "externalFilesDir is null");
            return null;
        }

        Log.d(TAG, "externalFilesDir : " + externalFilesDir.getAbsolutePath());

        return new File(externalFilesDir, getPhotoFileName());
    }
}
