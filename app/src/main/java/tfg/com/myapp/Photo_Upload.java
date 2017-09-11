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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by adria on 30/07/2017.
 */

public class Photo_Upload extends Activity implements ConnectionCallbacks,
        OnConnectionFailedListener {

    private static final String TAG = "TFG:Upload:Activity";

    private String path;
    private String name;
    private int numComp;
    private GoogleApiClient mGoogleApiClient;

    private String pathSett;
    private String textSett;
    private String nameSett;

    private String nameGrid;
    private String pathGrid;

    private static final int RESOLVE_CONNECTION_REQUEST_CODE = 1;
    private static final int REQUEST_CODE_CREATOR_SETT = 4;
    private static final int REQUEST_CODE_CREATOR_IMG = 2;
    private static final int REQUEST_CODE_CREATOR_GRID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        path = extras.getString("photoPath");
        name = extras.getString("photoName");

        setContentView(R.layout.layout_photo_upload);

        TextView tv = (TextView) findViewById(R.id.textViewUpload);
        tv.setText("Uploading..." + name);

        //Grid
        String endPhoto = name.substring(name.length()-5);
        nameGrid = path.split("/")[5] + "_Grid_" + endPhoto;
        pathGrid = path.substring(0, path.indexOf("Erased Derivations")) + "Derivaciones/Grid_"+ endPhoto;

        //Settings
        pathSett = path.substring(0, path.indexOf("Erased Derivations"));
        textSett = readFile();

        //Compresion
        String [] parts = textSett.split("\n");
        numComp = Integer.parseInt(parts[parts.length -9].split(" ")[1]);
        nameSett = path.split("/")[5] + "_" + "Settings.txt";

        //Foto
        name = path.split("/")[5] + "_" + name;

        Log.i(TAG, "path: " + path);
        Log.i(TAG, "pathGrid: " + pathGrid);
        Log.i(TAG, "nameGrid: " + nameGrid);

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
            case REQUEST_CODE_CREATOR_IMG:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Image successfully saved.");
                    Toast.makeText(getApplicationContext(),	getString(R.string.doneUpload),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),	getString(R.string.errorUpload),Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_CREATOR_GRID:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Image Grid successfully saved.");
                    Toast.makeText(getApplicationContext(),	getString(R.string.doneUpload),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(),	getString(R.string.errorUpload),Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_CREATOR_SETT:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Settings successfully saved.");
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
        saveFileToDrive(name, path, true, REQUEST_CODE_CREATOR_IMG, "");
        saveFileToDrive(nameGrid, pathGrid, true, REQUEST_CODE_CREATOR_GRID, "");
        saveFileToDrive(nameSett, pathSett, false, REQUEST_CODE_CREATOR_SETT, textSett);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }


    private void saveFileToDrive(final String namePhoto, String pathPhoto, final boolean isImage, final int code, final String text) {

        Log.i(TAG, "Starting Saving File.");

        final Bitmap image = BitmapFactory.decodeFile(pathPhoto);

        Drive.DriveApi.newDriveContents(mGoogleApiClient)
            .setResultCallback(new ResultCallback<DriveContentsResult>() {

                @Override
                public void onResult(DriveContentsResult result) {

                if (!result.getStatus().isSuccess()) {
                    Log.i(TAG, "Failed to create new contents.");
                    return;
                }

                Log.i(TAG, "New contents created.");
                String mimeType = "";

                if (isImage){
                    OutputStream outputStream = result.getDriveContents().getOutputStream();
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
                    mimeType = "image/jpeg";
                }else{

                    OutputStream outputStream = result.getDriveContents().getOutputStream();
                    try {
                        outputStream.write(text.getBytes());
                    } catch (IOException e1) {
                        Log.i(TAG, "Unable to write file contents.");
                    }
                    mimeType = "text/plain";
                }

                // Create the initial metadata - MIME type and title.
                // Note that the user will be able to change the title later.
                MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                        .setMimeType(mimeType).setTitle(namePhoto).build();
                // Create an intent for the file chooser, and start it.
                IntentSender intentSender = Drive.DriveApi
                        .newCreateFileActivityBuilder()
                        .setInitialMetadata(metadataChangeSet)
                        .setInitialDriveContents(result.getDriveContents())
                        .build(mGoogleApiClient);
                try {
                    startIntentSenderForResult(
                            intentSender, code, null, 0, 0, 0);
                } catch (IntentSender.SendIntentException e) {
                    Log.i(TAG, "Failed to launch file chooser.");
                }
                }
            });
    }

    public String readFile(){

        File file = new File(path.substring(0, path.indexOf("Erased Derivations/" + name)), "settings.txt");
        //Read text from file
        String line;
        String text = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                text = text + line + "\n";
            }
            br.close();
        }
        catch (IOException e) {
            //You'll need to add proper error handling here
            text = "";
        }
        return text;
    }

}
