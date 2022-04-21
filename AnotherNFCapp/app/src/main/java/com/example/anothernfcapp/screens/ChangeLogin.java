package com.example.anothernfcapp.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.anothernfcapp.R;
import com.example.anothernfcapp.json.JsonFactory;
import com.example.anothernfcapp.utility.BadStatusCodeProcess;
import com.example.anothernfcapp.utility.StaticVariables;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class ChangeLogin extends Activity {
    Button changeLoginButton;
    EditText login;
    EditText confirmLogin;
    EditText password;
    Button goBack;
    AsyncHttpClient asyncHttpClient;
    JsonFactory jsonFactory;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_login);
        asyncHttpClient = new AsyncHttpClient();
        jsonFactory = new JsonFactory();
        login.findViewById(R.id.old_login);
        confirmLogin.findViewById(R.id.new_login);
        password.findViewById(R.id.passwordForChangeLogin);
        changeLoginButton.findViewById(R.id.buttonForChangeLogin);
        goBack.findViewById(R.id.goBackButtonChangeLogin);
        changeLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    changeLogin();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

    }

    private void changeLogin() throws UnsupportedEncodingException {
        if (login.getText().toString().equals(confirmLogin.getText().toString()) && login.getText().toString().equals(StaticVariables.login)){
            String url = StaticVariables.ipServerUrl + StaticVariables.JWT + "/changeLogin";
            String request = jsonFactory.makeJsonForChangeLoginRequest(login.getText().toString(), password.getText().toString());
            StringEntity stringEntity = new StringEntity(request);
            asyncHttpClient.post(this, url, stringEntity, request, new TextHttpResponseHandler() {
                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    BadStatusCodeProcess badStatusCodeProcess = new BadStatusCodeProcess();
                    badStatusCodeProcess.parseBadStatusCode(statusCode, responseString, ChangeLogin.this);
                    Log.e("CHANGELOGIN", "Status code: " + statusCode + " response: " + responseString);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.d("CHANGELOGIN", "Successfully change login");
                    printOnSuccess();
                    try {
                        wait(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logout();
                }
            });
        }
        else if (login.getText().toString().equals("") || confirmLogin.getText().toString().equals("")){
            Toast.makeText(this, "Enter login", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Logins doesn't match", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }

    private void printOnSuccess() {
        Toast.makeText(this, "Successfully changed your login", Toast.LENGTH_SHORT).show();
    }

    private void goBack() {
        Intent intent = new Intent(this, SettingsScreen.class);
        startActivity(intent);
    }


}
