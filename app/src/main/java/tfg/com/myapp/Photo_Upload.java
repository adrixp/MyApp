package tfg.com.myapp;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi.DriveContentsResult;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;


/**
 * Created by adria on 30/07/2017.
 */


//Subir Settings
    //Subir grid?
    

public class Photo_Upload extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener {

    private static final String TAG = "TFG:Upload:Activity";

    private String path;
    private String name;
    private int numComp;
    private GoogleApiClient mGoogleApiClient;

    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 3;
    private static final int REQUEST_CODE_CREATOR = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        path = extras.getString("photoPath");
        name = extras.getString("photoName");
        numComp = extras.getInt("photoComp");

        setContentView(R.layout.layout_photo_upload);

        TextView tv = (TextView) findViewById(R.id.textViewUpload);
        tv.setText("Uploading..." + name);

        name = path.split("/")[5] + "_" + name;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection failed: " + connectionResult.toString());
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, RESOLVE_CONNECTION_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {

            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
            case REQUEST_CODE_CREATOR:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Image successfully saved.");
                    Toast.makeText(getApplicationContext(),	getString(R.string.doneUpload),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),	getString(R.string.errorUpload),Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "API client connected.");
        saveFileToDrive();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }


    private void saveFileToDrive() {

        Log.i(TAG, "Starting Saving File.");

        final Bitmap image = BitmapFactory.decodeFile(path);

        Drive.DriveApi.newDriveContents(mGoogleApiClient)
            .setResultCallback(new ResultCallback<DriveContentsResult>() {

                @Override
                public void onResult(DriveContentsResult result) {

                if (!result.getStatus().isSuccess()) {
                    Log.i(TAG, "Failed to create new contents.");
                    return;
                }

                // Otherwise, we can write our data to the new contents.
                Log.i(TAG, "New contents created.");
                // Get an output stream for the contents.
                OutputStream outputStream = result.getDriveContents().getOutputStream();
                // Write the bitmap data from it.
                ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();

                if (name.endsWith(".jpg")){
                    image.compress(Bitmap.CompressFormat.JPEG, numComp, bitmapStream);
                }else{
                    image.compress(Bitmap.CompressFormat.PNG, numComp, bitmapStream);
                }


                try {
                    outputStream.write(bitmapStream.toByteArray());
                } catch (IOException e1) {
                    Log.i(TAG, "Unable to write file contents.");
                }

                // Create the initial metadata - MIME type and title.
                // Note that the user will be able to change the title later.
                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                        .setMimeType("image/jpeg").setTitle(name).build();
                // Create an intent for the file chooser, and start it.
                IntentSender intentSender = Drive.DriveApi
                        .newCreateFileActivityBuilder()
                        .setInitialMetadata(metadataChangeSet)
                        .setInitialDriveContents(result.getDriveContents())
                        .build(mGoogleApiClient);
                try {
                    startIntentSenderForResult(
                            intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "Failed to launch file chooser.");
                }
                }
            });
    }

}
