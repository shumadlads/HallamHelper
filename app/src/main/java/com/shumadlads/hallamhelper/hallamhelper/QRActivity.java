package com.shumadlads.hallamhelper.hallamhelper;

import android.app.ActivityOptions;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;



import com.google.zxing.Result;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.shumadlads.hallamhelper.hallamhelper.Models.Module;
import com.shumadlads.hallamhelper.hallamhelper.Models.Module_Table;
import com.shumadlads.hallamhelper.hallamhelper.Models.Room;
import com.shumadlads.hallamhelper.hallamhelper.Models.Room_Table;
import com.shumadlads.hallamhelper.hallamhelper.Models.Session;
import com.shumadlads.hallamhelper.hallamhelper.Models.User;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Session;
import com.shumadlads.hallamhelper.hallamhelper.Models.User_Table;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;
import static com.shumadlads.hallamhelper.hallamhelper.MainActivity.TIMETABLE_FRAGMENT;

public class QRActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    private static final int REQUEST_CAMERA = 1;
    private ActionBar toolbar;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;

    private SharedPreferences SharedPrefs;
    private SharedPreferences.Editor Editor;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Editor = SharedPrefs.edit();

        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
        int currentApiVersion = Build.VERSION.SDK_INT;

        if(currentApiVersion >=  Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                Toast.makeText(getApplicationContext(), "Permission already granted!", Toast.LENGTH_LONG).show();
            }
            else
            {
                requestPermission();
            }
        }
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(QRActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void redirect(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("LoadDefaultFragment", TIMETABLE_FRAGMENT);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    @Override
    public void handleResult(Result result) {
        final String myResult = result.getText();
        Log.d("QRCodeScanner", result.getText());
        Log.d("QRCodeScanner", result.getBarcodeFormat().toString());

        String currentString = myResult;
        String[] separated = currentString.split(";");
        final String HallamHelperCheck = separated[0];
        final String Lecturer = separated[1];
        final String SessionName = separated[2];
        final String TimeStart = separated[3];
        final String TimeEnd = separated[4];
        final String Type = separated[5];
        final int RoomId = Integer.parseInt(separated[6]);
        final int ModuleId = Integer.parseInt(separated[7]);
        final int Semester1 = Integer.parseInt(separated[8]);
        final int Semester2 = Integer.parseInt(separated[9]);
        final String SessionDate = separated[10];
        final int Christmas = Integer.parseInt(separated[11]);
        final int Easter = Integer.parseInt(separated[12]);



        if (HallamHelperCheck.equals("SHU")){

            //SHU;Peter O'Neil;Mobile Application Development;11:00;12:00;It;1;1;1;1;01/03/2019;0;0

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(HallamHelperCheck);
            builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    scannerView.resumeCameraPreview(QRActivity.this);
                }
            });
            builder.setNeutralButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    int UserID = SharedPrefs.getInt(getString(R.string.SP_UserId), -1);
                    User user = SQLite.select().from(User.class).where(User_Table.UserId.eq(UserID)).querySingle();
                    Module setmodule = SQLite.select().from(Module.class).where(Module_Table.ModuleId.eq(ModuleId)).querySingle();
                    Room setroom = SQLite.select().from(Room.class).where(Room_Table.RoomId.eq(RoomId)).querySingle();

                    Session newsession = new Session();
                    User_Session user_session = new User_Session();
                    newsession.setDate(SessionDate);
                    newsession.setStartTime(TimeStart);
                    newsession.setEndTime(TimeEnd);
                    newsession.setType(Type);

                    if(setroom != null) {
                        newsession.setRoom(setroom);
                    }
                    newsession.setRoom(setroom);
                    if(setmodule != null) {
                        newsession.setModule(setmodule);
                    }
                    newsession.setSemester1(Semester1);
                    newsession.setSemester2(Semester2);
                    newsession.setChristmas(Christmas);
                    newsession.setEaster(Easter);
                    newsession.save();
                    user_session.setSession(newsession);
                    user_session.setUser(user);
                    user_session.save();
                    redirect();


                }
            });
            builder.setMessage(Lecturer + " - " + SessionName + " - " + TimeStart + " till " + TimeEnd);
            AlertDialog alert1 = builder.create();
            alert1.show();
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Invalid QR");
            builder.setMessage("Please only use Sheffield Hallam University QR Codes");
            AlertDialog alert1 = builder.create();
            alert1.show();


        }

    }
}