package com.example.folio;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;


import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import org.w3c.dom.Text;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
//import sun.applet.Main;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    private DatabaseReference databaseRef;

    private static final int REQUEST_CAMERA=1;
    private ZXingScannerView scannerView;
    private  Integer images[]={
            R.drawable.pussinboots,
            R.drawable.mickeymouse,
            R.drawable.garfield,
            R.drawable.ben10,
            R.drawable.kungfupanda,
            R.drawable.snowwhite,
            R.drawable.mrgringe,
            R.drawable.scooby,
            R.drawable.shaggy,
            R.drawable.shrek,
            R.drawable.superman,
            R.drawable.lionking

    };
    private String imageNames[]={
            "Puss in Boots",
            "Mickey Mouse",
            "Garfield",
            "Ben 10",
            "Kungfu Panda",
            "Snow White",
            "Mr Grinch",
            "Scooby",
            "Shaggy",
            "Shrek",
            "Super Man",
            "Lion King"
    };
    MediaPlayer mediaPlayer;
    private void mediaplayer(String index)
    {

        switch(index)
        {
            case "Puss in Boots" : mediaPlayer=MediaPlayer.create(this, R.raw.pussinboots);
                break;
            case "Mickey Mouse" : mediaPlayer=MediaPlayer.create(this, R.raw.mickeymouse);
                break;
            case "Garfield" : mediaPlayer=MediaPlayer.create(this, R.raw.garfield);
                break;
            case "Ben 10" :mediaPlayer=MediaPlayer.create(this, R.raw.ben10);
                break;
            case "Kungfu Panda" : mediaPlayer=MediaPlayer.create(this, R.raw.kungfupanda);
                break;
            case "Snow White" : mediaPlayer=MediaPlayer.create(this, R.raw.snowwhite);
                break;
            case "Mr Grinch" : mediaPlayer=MediaPlayer.create(this, R.raw.mrgrinch);
                break;
            case "Scooby" : mediaPlayer=MediaPlayer.create(this, R.raw.scooby);
                break;
            case "Shaggy" : mediaPlayer=MediaPlayer.create(this, R.raw.scooby);
                break;
            case "Shrek" : mediaPlayer=MediaPlayer.create(this, R.raw.shrek);
                break;
            case "Super Man" : mediaPlayer=MediaPlayer.create(this, R.raw.superman);
                break;
            case "Lion King" : mediaPlayer=MediaPlayer.create(this, R.raw.lionking);
                break;
        }
    }
    private int arraySize=images.length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView=new ZXingScannerView(this);
        setContentView(scannerView);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                Toast.makeText(MainActivity.this, "Permission granted!", Toast.LENGTH_LONG).show();
            }
            else
            {
                requestPermission();
            }
        }


    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED);
    }
    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
    }

    public void onRequestPermissionsResult(int requestCode, String permission[], int grantResults[])
    {
        switch(requestCode)
        {
            case REQUEST_CAMERA:
                if(grantResults.length>0)
                {
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted)
                    {
                        Toast.makeText(MainActivity.this, "Permission Granted!", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_LONG).show();
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        {
                            if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
                            {
                                displayAlertMessage("Please allow access!", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
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
    @Override
    public void onResume()
    {
        super.onResume();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {
                if(scannerView==null)
                {
                    scannerView=new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }
            else
            {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void displayAlertMessage(String message, DialogInterface.OnClickListener listener)
    {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("Ok", listener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    @Override
    public void handleResult(Result result) {

        final String scanResult = result.getText();

        if(!TextUtils.isEmpty(scanResult) && TextUtils.isDigitsOnly(scanResult))
        {
            int number = Integer.parseInt(scanResult);

            if(number<arraySize) {
                setContentView(R.layout.activity_main);
                ImageView imageView=(ImageView)findViewById(R.id.mainImageView);
                TextView textView=(TextView)findViewById(R.id.text_view_character_name);

                imageView.setImageResource(images[number]);
                textView.setText(imageNames[number]);

                mediaplayer(imageNames[number]);
                mediaPlayer.start();



                final Button buttonGoBack=(Button)findViewById(R.id.buttonGoBack);
                buttonGoBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayer.stop();
                        setContentView(scannerView);
                        scannerView.resumeCameraPreview(MainActivity.this);
                    }
                });
            }
            else
            {
                feedback("Invalid QR Code");
            }
        }
        else
        {
            feedback("Invalid QR code!");
        }


    }


private void feedback(String status)
{
    AlertDialog.Builder builder=new AlertDialog.Builder(this);
    builder.setTitle("Scan result");
    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            scannerView.resumeCameraPreview(MainActivity.this);

        }
    });
    builder.setMessage(status);
    AlertDialog alert=builder.create();
    alert.show();
}











}
