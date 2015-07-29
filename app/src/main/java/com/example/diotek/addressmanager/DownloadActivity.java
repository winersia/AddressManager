package com.example.diotek.addressmanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by diotek on 2015-07-28.
 */
public class DownloadActivity extends BaseDemoActivity implements BaseVariables {

    private static final String TAG = "DownloadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloadaddressfile);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, CREATE_FILE_NAME))
                .build();
        Drive.DriveApi.query(getGoogleApiClient(), query)
                .setResultCallback(downloadMetadataCallback);
    }

    final private ResultCallback<DriveApi.MetadataBufferResult> downloadMetadataCallback = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        setResult(DOWNLOAD_RESULT_CANCELED);
                        return;
                    }
                    Metadata metadata = getMetaData(result.getMetadataBuffer());
                    if(metadata == null){
                        showMessage("No exist file!!");
                        setResult(DOWNLOAD_RESULT_CANCELED);
                        finish();
                        return;
                    }
                    Drive.DriveApi.fetchDriveId(getGoogleApiClient(), metadata.getDriveId().getResourceId())
                            .setResultCallback(downloadIdCallback);
                }
            };

    final private ResultCallback<DriveApi.DriveIdResult> downloadIdCallback = new ResultCallback<DriveApi.DriveIdResult>() {
        @Override
        public void onResult(DriveApi.DriveIdResult result) {
            DriveFile file = Drive.DriveApi.getFile(getGoogleApiClient(), result.getDriveId());
            new EditContentsAsyncTask(DownloadActivity.this).execute(file);
        }
    };

    public class EditContentsAsyncTask extends ApiClientAsyncTask<DriveFile, Void, Boolean> {

        public EditContentsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected Boolean doInBackgroundConnected(DriveFile... args) {
            DriveFile file = args[0];
            DriveApi.DriveContentsResult driveContentsResult =
                    file.open(getGoogleApiClient(), DriveFile.MODE_READ_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                setResult(DOWNLOAD_RESULT_CANCELED);
                return null;
            }

            DriveContents driveContents = driveContentsResult.getDriveContents();

            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            byte[] memoData  = null;

            try {
                inputStream = driveContents.getInputStream();
                outputStream = new FileOutputStream(getDatabasePath(DB_ADDRESS));
                memoData = new byte[inputStream.available()];

                while (inputStream.read(memoData) != -1) {
                    outputStream.write(memoData);
                }
                return true;

            } catch (IOException e) {
                Log.e(TAG, "IOException while reading from the stream", e);
                return false;
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                setResult(DOWNLOAD_RESULT_CANCELED);
                return;
            }
            setResult(DOWNLOAD_RESULT_OK);
            finish();
        }
    }

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

}
