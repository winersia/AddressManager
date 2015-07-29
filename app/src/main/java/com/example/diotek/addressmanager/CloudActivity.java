package com.example.diotek.addressmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.File;

/**
 * Created by diotek on 2015-07-28.
 */
public class CloudActivity extends BaseDemoActivity implements BaseVariables {
    private static final String TAG = "CloudActivity";

    Button mBackBtn = null;
    Button mUpLoadBtn = null;
    Button mDownLoadBtn = null;
    Button mSynchronizationBtn = null;
    Button mDeleteBtn = null;
    TextView mCloudText = null;

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);

        mBackBtn = (Button)findViewById(R.id.backButton);
        mUpLoadBtn = (Button)findViewById(R.id.uploadButton);
        mDownLoadBtn = (Button)findViewById(R.id.downloadButton);
        mSynchronizationBtn = (Button)findViewById(R.id.synchronizationButton);
        mCloudText = (TextView)findViewById(R.id.cloudTextView);
        mDeleteBtn = (Button)findViewById(R.id.deleteButton);

        mBackBtn.setOnClickListener(mBackClickListener);
        mUpLoadBtn.setOnClickListener(mUpLoadClickListener);
        mDownLoadBtn.setOnClickListener(mDownLoadClickListener);
        mSynchronizationBtn.setOnClickListener(mSynchronizationClickListener);
        mDeleteBtn.setOnClickListener(mDeleteClickListener);
    }

    private View.OnClickListener mUpLoadClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(),UploadActivity.class);
            startActivityForResult(intent, UPLOAD_ADDRESS_ACTIVITY);
        }
    };

    private View.OnClickListener mDownLoadClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(),DownloadActivity.class);
            startActivityForResult(intent, DOWNLOAD_ADDRESS_ACTIVITY);
        }
    };

    private View.OnClickListener mSynchronizationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Query query = new Query.Builder()
                    .addFilter(Filters.eq(SearchableField.TITLE, CREATE_FILE_NAME))
                    .build();
            Drive.DriveApi.query(getGoogleApiClient(), query)
                    .setResultCallback(SynchronizationMetadataCallback);
        }

    };

    final private ResultCallback<DriveApi.MetadataBufferResult> SynchronizationMetadataCallback = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while retrieving results");
                        return;
                    }

                    Metadata metadata = getMetaData(result.getMetadataBuffer());
                    if(metadata == null){
                        mCloudText.setText("There is no file in Drive!!");
                        return;
                    }
                    Drive.DriveApi.fetchDriveId(getGoogleApiClient(), metadata.getDriveId().getResourceId())
                            .setResultCallback(SynchronizationIdCallback);
                }
            };

    final ResultCallback<DriveApi.DriveIdResult> SynchronizationIdCallback = new ResultCallback<DriveApi.DriveIdResult>() {
        @Override
        public void onResult(DriveApi.DriveIdResult result) {
            if (!result.getStatus().isSuccess()) {
                setResult(SYNCHRONIZE_RESULT_CANCELED);
                return;
            }
            DriveFile file = Drive.DriveApi.getFile(getGoogleApiClient(), result.getDriveId());
            file.getMetadata(getGoogleApiClient()).setResultCallback(synchronizationMetadataCallback);
        }
    };

    final ResultCallback<DriveResource.MetadataResult> synchronizationMetadataCallback = new ResultCallback<DriveResource.MetadataResult>() {
        @Override
        public void onResult(DriveResource.MetadataResult result) {
            if (!result.getStatus().isSuccess()) {
                setResult(SYNCHRONIZE_RESULT_CANCELED);
                return;
            }

            File appFile = new File(getDatabasePath(DB_ADDRESS).toString());
            result.getMetadata().getDriveId().getResourceId();

            if (appFile.lastModified() < result.getMetadata().getModifiedDate().getTime()) {
                Intent intent = new Intent(getBaseContext(),DownloadActivity.class);
                startActivityForResult(intent, SYNCHRONIZE_ADDRESS_ACTIVITY);
            } else {
                Intent intent = new Intent(getBaseContext(),UploadActivity.class);
                startActivityForResult(intent, SYNCHRONIZE_ADDRESS_ACTIVITY);
            }
            setResult(SYNCHRONIZE_RESULT_OK);
        }
    };

    public Metadata getMetaData(MetadataBuffer metadataBuffer){
        Metadata metadata = null;
        for(int i = 0; i< metadataBuffer.getCount(); i++){
            metadata = metadataBuffer.get(i);
            if(metadata.getTitle().equals(CREATE_FILE_NAME)){
                break;
            }
        }
        return metadata;
    }

    private View.OnClickListener mBackClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setResult(CLOUD_RESULT_OK);
            finish();
        }
    };

    private View.OnClickListener mDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getBaseContext(),DeleteActivity.class);
            startActivityForResult(intent, DELETE_ADDRESS_ACTIVITY);
        }
    };

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent intent){
        if(requestCode == UPLOAD_ADDRESS_ACTIVITY){
            if(resultCode == UPLOAD_RESULT_OK){
                mCloudText.setText("업로드 완료");
            }
            else if(resultCode == UPLOAD_RESULT_CANCELED){
                mCloudText.setText("업로드 실패");
            }
        }
        else if(requestCode == DOWNLOAD_ADDRESS_ACTIVITY){
            if(resultCode == DOWNLOAD_RESULT_OK){
                mCloudText.setText("다운로드 완료");
            }
            else if(resultCode == DOWNLOAD_RESULT_CANCELED){
                mCloudText.setText("다운로드 실패");
            }
        }
        else if(requestCode == SYNCHRONIZE_ADDRESS_ACTIVITY){
            if(resultCode == UPLOAD_RESULT_OK || resultCode == DOWNLOAD_RESULT_OK){
                mCloudText.setText("동기화 완료");
            }
            else if(resultCode == UPLOAD_RESULT_CANCELED || resultCode == DOWNLOAD_RESULT_CANCELED){
                mCloudText.setText("동기화 실패");
            }
        }
        else if(requestCode == DELETE_ADDRESS_ACTIVITY){
            if(resultCode == DELETE_RESULT_OK){
                mCloudText.setText("삭제 완료");
            }
        }
    }

}
