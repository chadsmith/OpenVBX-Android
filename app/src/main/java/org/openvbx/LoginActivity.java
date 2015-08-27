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
    private LinearLayout setup;
    private EditText endpoint;
    private LinearLayout sign_in;
	private EditText email;
	private EditText password;
    private Boolean canCheckPhoneState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mActivity = this;
        setup = (LinearLayout) findViewById(R.id.setup);
        endpoint = (EditText) findViewById(R.id.endpoint);

        findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (hasValidEndpoint())
                    checkEndpoint();
            }
        });
        endpoint.setText(OpenVBX.endpoint);

        sign_in = (LinearLayout) findViewById(R.id.sign_in);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        if(ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
            canCheckPhoneState = true;
        else
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.READ_PHONE_STATE }, PERMISSION_REQUEST_PHONE_STATE);

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (hasValidLogin()) {
                    OpenVBX.API.getFolders()
                            .compose(OpenVBX.<Inbox>defaultSchedulers())
                            .subscribe(new Subscriber<Inbox>() {
                                @Override
                                public void onCompleted() {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    password.setText(null);
                                    OpenVBX.password = null;
                                    OpenVBX.error(mActivity, R.string.login_failed);
                                }

                                @Override
                                public void onNext(Inbox inbox) {
                                    OpenVBX.saveLoginCredentials();
                                    OpenVBX.inbox = inbox;
                                    if (canCheckPhoneState) {
                                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                                        String device = telephonyManager.getLine1Number();
                                        if (!device.isEmpty() && !Build.PRODUCT.equals("sdk") && !Build.PRODUCT.equals("google_sdk"))
                                            OpenVBX.saveDevice(device);
                                    }
                                    startActivity(new Intent(getApplicationContext(), InboxActivity.class));
                                    finish();
                                }
                            });
                }
            }
        });
		email.setText(OpenVBX.email);
		password.setText(OpenVBX.password);
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
                        String url = OpenVBX.endpoint;
                        if (url.endsWith("index.php")) {
                            endpoint.setText(null);
                            OpenVBX.error(mActivity, R.string.invalid_endpoint);
                        } else {
                            if (!url.endsWith("/"))
                                url += "/";
                            url += "index.php";
                            OpenVBX.saveEndpoint(url);
                            checkEndpoint();
                        }
                    }

                    @Override
                    public void onNext(ClientResult result) {
                        setup.setVisibility(View.GONE);
                        sign_in.setVisibility(View.VISIBLE);
                    }
                });
    }

    private boolean hasValidEndpoint() {
        String url = endpoint.getText().toString();
        if(!url.isEmpty()) {
            OpenVBX.saveEndpoint(url);
            return true;
        }
        return false;
    }

    private boolean hasValidLogin() {
    	OpenVBX.email = email.getText().toString();
		OpenVBX.password = password.getText().toString();
    	return !OpenVBX.email.isEmpty() && !OpenVBX.password.isEmpty();
    }

}