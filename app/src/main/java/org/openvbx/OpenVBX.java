package org.openvbx;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.widget.Toast;

import org.openvbx.models.CallerID;
import org.openvbx.models.ClientResult;
import org.openvbx.models.Folder;
import org.openvbx.models.Inbox;
import org.openvbx.models.Message;
import org.openvbx.models.MessageResult;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class OpenVBX extends Application {

	private static SharedPreferences settings;

	public static String endpoint = null;
	public static String email = null;
	public static String password = null;
	public static String device = null;
    public static Context mContext;

    public static VbxService API;

	public static ArrayList<CallerID> callerIDs = new ArrayList<>();

	public void onCreate() {

		super.onCreate();

        mContext = getApplicationContext();
		settings = getSharedPreferences("settings", MODE_PRIVATE);
		endpoint = settings.getString("endpoint", null);
		email = settings.getString("email", null);
		password = settings.getString("password", null);
		device = settings.getString("device", null);

        if(endpoint != null && !endpoint.isEmpty())
            API = getApiInstance();
	}

    private static VbxService getApiInstance() {

        // Use HttpLoggingInterceptor.Level.NONE to disable logging
        HttpLoggingInterceptor logger = new HttpLoggingInterceptor();
        logger.setLevel(HttpLoggingInterceptor.Level.BODY);

        Interceptor setAcceptType = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request typedRequest = request
                        .newBuilder()
                        .header("Accept", "application/json")
                        .build();
                return chain.proceed(typedRequest);
            }
        };

        Interceptor setAuthorization = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if(email != null && password != null) {
                    String auth = Base64.encodeToString((email + ":" + password).getBytes(), Base64.NO_WRAP);
                    Request typedRequest = request
                            .newBuilder()
                            .header("Authorization", String.format("Basic %s", auth))
                            .build();
                    return chain.proceed(typedRequest);
                }
                return chain.proceed(request);
            }
        };

        OkHttpClient apiClient = new OkHttpClient.Builder()
                .addInterceptor(logger)
                .addInterceptor(setAcceptType)
                .addInterceptor(setAuthorization)
                .build();

        return new Retrofit.Builder()
                .baseUrl(endpoint)
                .client(apiClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(VbxService.class);
    }

	public static ArrayList<CallerID> getCallerIDs(String type) {
		if("voice".equals(type))
			return callerIDs;
		ArrayList<CallerID> result = new ArrayList<>();
        for(CallerID callerID : callerIDs)
            if(callerID.hasSms())
                result.add(callerID);
		return result;
	}

    public static void setEndpoint(String endpoint) {
        OpenVBX.endpoint = endpoint;
        API = getApiInstance();
    }

	public static void saveEndpoint() {
		settings.edit().putString("endpoint", endpoint).apply();
	}

    public static void setLogin(String email, String password) {
        OpenVBX.email = email;
        OpenVBX.password = password;
    }

	public static void saveLogin() {
		settings.edit().putString("email", email).putString("password", password).apply();
	}

	public static void saveDevice(String device) {
        OpenVBX.device = device;
        settings.edit().putString("device", device).apply();
	}

    public static void signOut() {
        settings.edit().clear().apply();
        endpoint = null;
        email = null;
        password = null;
        device = null;
    }

    public static void error(Activity activity, String message) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    public static void error(Activity activity, int message) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    public static void toast(final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void toast(int id) {
        toast(mContext.getString(id));
    }

    public interface VbxService {

        @GET("/client")
        Observable<ClientResult> checkEndpoint();

        @GET("/messages/inbox")
        Observable<Inbox> getFolders();

        @GET("/messages/inbox/{folder_id}")
        Observable<Folder> getFolder(@Path("folder_id") int folder_id);

        @GET("/messages/details/{message_id}")
        Observable<Message> getMessage(@Path("message_id") int message_id);

        @FormUrlEncoded
        @POST("/messages/details/{message_id}")
        Observable<Void> archiveMessage(@Path("message_id") int message_id, @Field("archived") Boolean archived);

        @FormUrlEncoded
        @POST("/messages/details/{message_id}")
        Observable<Void> updateTicketStatus(@Path("message_id") int message_id, @Field("ticket_status") String status);

        @FormUrlEncoded
        @POST("/messages/details/{message_id}")
        Observable<Void> updateAssignment(@Path("message_id") int message_id, @Field("assigned") int user_id);

        @FormUrlEncoded
        @POST("/messages/details/{message_id}/annotations")
        Observable<Void> addAnnotation(@Path("message_id") int message_id, @Field("annotation_type") String annotation_type, @Field("description") String description);

        @FormUrlEncoded
        @POST("/messages/sms")
        Observable<MessageResult> sendMessage(@Field("from") String from, @Field("to") String to, @Field("content") String content);

        @FormUrlEncoded
        @POST("/messages/sms/{message_id}")
        Observable<MessageResult> sendReply(@Path("message_id") int message_id, @Field("from") String from, @Field("to") String to, @Field("content") String content);

        @FormUrlEncoded
        @POST("/messages/call")
        Observable<MessageResult> sendCall(@Field("callerid") String callerid, @Field("from") String from, @Field("to") String to);

        @FormUrlEncoded
        @POST("/messages/call/{message_id}")
        Observable<MessageResult> sendCallback(@Path("message_id") int message_id, @Field("callerid") String callerid, @Field("from") String from, @Field("to") String to);

        @GET("/numbers/outgoingcallerid")
        Observable<ArrayList<CallerID>> getOutgoingCallerIDs();
    }

    public static <T> Observable.Transformer<T, T> defaultSchedulers() {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> observable) {
                return observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

}