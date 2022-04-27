package com.example.anothernfcapp.screens;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.anothernfcapp.R;
import com.example.anothernfcapp.utility.StaticVariables;

public class MainScreen extends AppCompatActivity {
    boolean mWriteMode = false;
    private NfcAdapter nfcAdapter;
    private PendingIntent nfcPendingIntent;
    private Button getScreen;
    private Button writeScreen;
    private Button tagSettingsScreen;
    private Button setUpTagButton;
    private Button settingsScreen;
    private TextView tagIdTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tagIdTextView.findViewById(R.id.tagId);
        writeScreen.findViewById(R.id.buttonwrite).setOnClickListener(v -> {
            if (StaticVariables.tagId != null){
                writeScreenStart();
            }
            else{
                msgError();
            }
        });
        getScreen.findViewById(R.id.buttonget).setOnClickListener(v -> {
            if (StaticVariables.tagId == null){
                msgError();
            }
            else {
                getDataViaTag();
            }
        });
        tagSettingsScreen.findViewById(R.id.tagSettingsButton).setOnClickListener(v ->{
            if (StaticVariables.tagId == null){
                msgError();
            }
            else{
                Intent intent = new Intent(this, TagSettings.class);
                startActivity(intent);
            }
        });
        setUpTagButton.findViewById(R.id.buttonSetUp).setOnClickListener(v -> {
            nfcAdapter = NfcAdapter.getDefaultAdapter(MainScreen.this);
            nfcPendingIntent = PendingIntent.getActivity(MainScreen.this, 0,
                    new Intent(MainScreen.this, MainScreen.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            enableTagWriteMode();
            new AlertDialog.Builder(MainScreen.this).setTitle("Touch tag to make a get request").setOnCancelListener(dialog -> disableTagWriteMode()).create().show();
        });
        settingsScreen.findViewById(R.id.settings).setOnClickListener(v -> {
            Intent settingsIntent = new Intent(MainScreen.this, SettingsScreen.class);
            startActivity(settingsIntent);
        });

    }

    private void disableTagWriteMode() {
        mWriteMode = false;
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void enableTagWriteMode() {
        mWriteMode = true;
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter[] mWriteTagFilters = new IntentFilter[] { tagDetected };
        nfcAdapter.enableForegroundDispatch(this, nfcPendingIntent, mWriteTagFilters, null);
    }

    private void msgError(){
        Toast.makeText(this, "Tag isn't set up", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    @Override

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (mWriteMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            StaticVariables.setTagId(bytesToHexString(detectedTag.getId()));
            Log.d("TAG", StaticVariables.tagId);
            tagIdTextView.setText("Current tagId: " + StaticVariables.tagId);
            Toast.makeText(this, "Success: POCED THIS TAG", Toast.LENGTH_SHORT).show();
        }
    }

    private void writeScreenStart(){
        Intent intent = new Intent(this, AddNote.class);
        startActivity(intent);

    }

    private void getDataViaTag(){
        Intent getScreen = new Intent(this, GetNotes.class);
        startActivity(getScreen);
    }

    private String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("0x");
        if (src == null || src.length <= 0) {
            return null;
        }
        char[] buffer = new char[2];
        for (int i = 0; i < src.length; i++) {
            buffer[0] = Character.forDigit((src[i] >>> 4) & 0x0F, 16);
            buffer[1] = Character.forDigit(src[i] & 0x0F, 16);
            System.out.println(buffer);
            stringBuilder.append(buffer);
        }

        return stringBuilder.toString();
    }

}