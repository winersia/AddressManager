package com.example.diotek.addressmanager;

import android.os.Bundle;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

/**
 * Created by diotek on 2015-07-28.
 */
public class DeleteActivity extends BaseDemoActivity implements BaseVariables{
    @Override
    public void onConnected(Bundle connectionHint) {
        super.onConnected(connectionHint);
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, CREATE_FILE_NAME))
                .build();
        Drive.DriveApi.query(getGoogleApiClient(), query)
                .setResultCallback(metadataCallback);
    }
    final private ResultCallback<DriveApi.MetadataBufferResult> metadataCallback = new
            ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    if (!result.getStatus().isSuccess()) {
                        showMessage("Problem while retrieving results");
                        return;
                    }
                    MetadataBuffer m = result.getMetadataBuffer();
                    Metadata metadata = null;

                    for(int i = 0; i< m.getCount(); i++){
                        metadata = m.get(i);
                        Drive.DriveApi.getFile(getGoogleApiClient(), metadata.getDriveId()).delete(getGoogleApiClient());
                    }
                    setResult(DELETE_RESULT_OK);
                    finish();
                }
            };

}
