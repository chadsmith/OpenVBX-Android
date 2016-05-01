package org.openvbx;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.openvbx.models.ClientResult;
import org.openvbx.models.Inbox;

import rx.Subscriber;

public class LoginActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_PHONE_STATE = 0;
    private Activity mActivity;
    private Context mContext;

    private LinearLayout setupView;
    private EditText endpointInput;
    private String endpoint;

    private LinearLayout loginView;
	private EditText emailInput;
	private EditText passwordInput;
    private String email;
    private String password;

    private Boolean canCheckPhoneState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mActivity = this;
        mContext = getApplicationContext();
        setupView = (LinearLayout) findViewById(R.id.setup);
        endpointInput = (EditText) findViewById(R.id.endpoint);

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                endpoint = endpointInput.getText().toString().trim();
                if(!endpoint.isEmpty()) {
                    OpenVBX.setEndpoint(endpoint);
                    checkEndpoint();
                }
            }
        });

        endpointInput.setText(OpenVBX.endpoint);

        if(ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
            canCheckPhoneState = true;
        else
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_PHONE_STATE }, PERMISSION_REQUEST_PHONE_STATE);

        loginView = (LinearLayout) findViewById(R.id.sign_in);
        emailInput = (EditText) findViewById(R.id.email);
        passwordInput = (EditText) findViewById(R.id.password);

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                email = emailInput.getText().toString().trim();
                password = passwordInput.getText().toString();
                if(!email.isEmpty() && !password.isEmpty()) {
                    OpenVBX.setLogin(email, password);
                    OpenVBX.API.getFolders()
                            .compose(OpenVBX.<Inbox>defaultSchedulers())
                            .subscribe(new Subscriber<Inbox>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    OpenVBX.setLogin(email, null);
                                    passwordInput.setText(null);
                                    OpenVBX.error(mActivity, R.string.login_failed);
                                }

                                @Override
                                public void onNext(Inbox inbox) {
                                    OpenVBX.saveLogin();
                                    if (canCheckPhoneState) {
                                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                                        String device = telephonyManager.getLine1Number();
                                        if (!device.isEmpty() && !Build.PRODUCT.equals("sdk") && !Build.PRODUCT.equals("google_sdk"))
                                            OpenVBX.saveDevice(device);
                                    }
                                    Intent i = new Intent(mContext, InboxActivity.class);
                                    i.putExtra("inbox", inbox);
                                    startActivity(i);
                                    finish();
                                }
                            });
                }
            }
        });

        emailInput.setText(OpenVBX.email);
        passwordInput.setText(OpenVBX.password);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_PHONE_STATE:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    canCheckPhoneState = true;
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void checkEndpoint() {
        OpenVBX.API.checkEndpoint()
                .compose(OpenVBX.<ClientResult>defaultSchedulers())
                .subscribe(new Subscriber<ClientResult>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (endpoint.endsWith("index.php")) {
                            OpenVBX.setEndpoint(null);
                            endpointInput.setText(null);
                            OpenVBX.error(mActivity, R.string.invalid_endpoint);
                        } else {
                            if (!endpoint.endsWith("/"))
                                endpoint += "/";
                            endpoint += "index.php";
                            OpenVBX.setEndpoint(endpoint);
                            checkEndpoint();
                        }
                    }

                    @Override
                    public void onNext(ClientResult result) {
                        OpenVBX.saveEndpoint();
                        setupView.setVisibility(View.GONE);
                        loginView.setVisibility(View.VISIBLE);
                    }
                });
    }

}