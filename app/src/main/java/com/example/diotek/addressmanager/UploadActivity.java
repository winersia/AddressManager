package com.example.diotek.addressmanager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by diotek on 2015-07-28.
 */
public class UploadActivity extends BaseDemoActivity implements BaseVariables {

    private static final String TAG = "UploadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadaddressfile);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, CREATE_FILE_NAME))
                .build();
        Drive.DriveApi.query(getGoogleApiClient(), query)
                .setResultCallback(uploadMetadataCallback);
    }

    final private ResultCallback<DriveApi.MetadataBufferResult> uploadMetadataCallback = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while retrieving results");
                        return;
                    }
                    Metadata metadata = getMetaData(result.getMetadataBuffer());
                    if(metadata == null) {
                        // create new contents resource
                        Drive.DriveApi.newDriveContents(getGoogleApiClient())
                                .setResultCallback(uploadNewDriveContentsCallback);
                    }
                    else {
                        //showMessage("Exist File!!!");
                        Drive.DriveApi.fetchDriveId(getGoogleApiClient(),
                                metadata.getDriveId().getResourceId())
                                .setResultCallback(uploadEditIdCallback);
                    }
                }
            };

    final private ResultCallback<DriveApi.DriveIdResult> uploadEditIdCallback = new ResultCallback<DriveApi.DriveIdResult>() {
        @Override
        public void onResult(DriveApi.DriveIdResult result) {
            DriveFile file = Drive.DriveApi.getFile(getGoogleApiClient(), result.getDriveId());
            new EditContentsAsyncTask(UploadActivity.this).execute(file);
        }
    };
    public class EditContentsAsyncTask extends ApiClientAsyncTask<DriveFile, Void, Boolean> {

        public EditContentsAsyncTask(Context context) {
            super(context);
        }

        @Override
        protected Boolean doInBackgroundConnected(DriveFile... args) {
            DriveFile file = args[0];
            FileInputStream inputStream = null;
            OutputStream outputStream = null;
            byte[] memoData  = null;
            try {
                DriveApi.DriveContentsResult driveContentsResult = file.open(
                        getGoogleApiClient(), DriveFile.MODE_WRITE_ONLY, null).await();
                if (!driveContentsResult.getStatus().isSuccess()) {
                    return false;
                }
                DriveContents driveContents = driveContentsResult.getDriveContents();

                inputStream = new FileInputStream(getDatabasePath(DB_ADDRESS));
                outputStream = driveContents.getOutputStream();
                memoData = new byte[inputStream.available()];

                while (inputStream.read(memoData) != -1) {
                    outputStream.write(memoData);
                }

                com.google.android.gms.common.api.Status status =
                        driveContents.commit(getGoogleApiClient(), null).await();
                return status.getStatus().isSuccess();

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
                setResult(UPLOAD_RESULT_CANCELED);
                return;
            }
            setResult(UPLOAD_RESULT_OK);
            finish();
        }
    }

    final private ResultCallback<DriveApi.DriveContentsResult> uploadNewDriveContentsCallback
            = new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        setResult(UPLOAD_RESULT_CANCELED);
                        return;
                    }

                    final DriveContents driveContents = result.getDriveContents();
                    // Perform I/O off the UI thread.
                    new Thread() {
                        @Override
                        public void run() {
                            // write content to DriveContents
                            FileInputStream inputStream = null;
                            OutputStream outputStream = null;
                            try {
                                inputStream = new FileInputStream(getDatabasePath(DB_ADDRESS));
                                outputStream = driveContents.getOutputStream();

                                byte[] memoData = new byte[inputStream.available()];
                                while (inputStream.read(memoData) != -1) {
                                    outputStream.write(memoData);
                                }
                            } catch (IOException e) {
                                Log.e(TAG, e.getMessage());
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

                            String mimeType = MimeTypeMap.getSingleton()
                                    .getExtensionFromMimeType("db");
                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(CREATE_FILE_NAME)
                                    .setMimeType(mimeType)
                                    .setStarred(true).build();

                            // create a file on root folder
                            Drive.DriveApi.getRootFolder(getGoogleApiClient())
                                    .createFile(getGoogleApiClient(), changeSet, driveContents)
                                    .setResultCallback(uploadFileCallback);
                        }
                    }.start();
                }
            };

    final private ResultCallback<DriveFolder.DriveFileResult> uploadFileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        setResult(UPLOAD_RESULT_CANCELED);
                        return;
                    }
                    setResult(UPLOAD_RESULT_OK);
                    finish();
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

}
