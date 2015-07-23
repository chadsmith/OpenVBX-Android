package org.openvbx;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.openvbx.adapters.MessagesAdapter;
import org.openvbx.models.CallerID;
import org.openvbx.models.Folder;
import org.openvbx.models.Inbox;
import org.openvbx.models.Message;

import java.util.ArrayList;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

public class InboxActivity extends AppCompatActivity {

    private Context mContext;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private LinearLayout mainContent;
    private MessagesAdapter messagesAdapter;
	private LinearLayout progress;
	private SwipeRefreshLayout refreshView;

    private Folder mFolder = new Folder();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);

        mContext = getApplicationContext();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainContent = (LinearLayout) findViewById(R.id.main);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                mainContent.setTranslationX(slideOffset * drawerView.getWidth());
                drawerLayout.bringChildToFront(drawerView);
                drawerLayout.requestLayout();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        View headerView = LayoutInflater.from(this).inflate(R.layout.drawer_header, null);
        TextView currentUser = (TextView) headerView.findViewById(R.id.current_user);
        currentUser.setText(OpenVBX.email);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.addHeaderView(headerView);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                switch (itemId) {
                    case R.id.settings:
                        requestDevice();
                        break;
                    case R.id.logout:
                        OpenVBX.signOut();
                        requireLogin();
                        break;
                    default:
                        selectFolder(itemId);
                        menuItem.setChecked(true);
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });

        progress = (LinearLayout) findViewById(R.id.progress);
		refreshView = (SwipeRefreshLayout) findViewById(R.id.refresh);
        ListView listView = (ListView) findViewById(R.id.list);

        messagesAdapter = new MessagesAdapter(this, R.layout.fragment_inbox_item, mFolder.messages.items);

        if(OpenVBX.endpoint == null || OpenVBX.email == null || OpenVBX.password == null)
            requireLogin();
        else if(OpenVBX.device == null)
            requestDevice();
        else
            getOutgoingCallerIDs();

        listView.setAdapter(messagesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Message message = messagesAdapter.get(position);
                Intent i = new Intent(mContext, MessageActivity.class);
                i.putExtra("message_id", message.id);
                startActivity(i);
            }
        });
		refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        progress.setVisibility(View.VISIBLE);
        findViewById(R.id.call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, CallActivity.class);
                startActivity(i);
            }
        });
    }

    public void requireLogin() {
        Intent i = new Intent(mContext, LoginActivity.class);
        startActivity(i);
        finish();
    }

    public void requestDevice() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null);
        final EditText deviceInput = (EditText) dialogView.findViewById(R.id.device);
        deviceInput.setText(OpenVBX.device);
        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String device = deviceInput.getText().toString();
                        if (!device.isEmpty()) {
                            OpenVBX.saveDevice(device);
                            dialog.dismiss();
                        }
                    }
                })
                .show();
    }

    public void getOutgoingCallerIDs() {
        OpenVBX.API.getOutgoingCallerIDs()
                .compose(OpenVBX.<ArrayList<CallerID>>defaultSchedulers())
                .subscribe(new Action1<ArrayList<CallerID>>() {
                    @Override
                    public void call(ArrayList<CallerID> callerIDs) {
                        OpenVBX.callerIDs = callerIDs;
                    }
                });
    }

    public void refresh() {
        OpenVBX.API.getFolders()
                .flatMap(new Func1<Inbox, Observable<Folder>>() {
                    @Override
                    public Observable<Folder> call(Inbox inbox) {
                        OpenVBX.inbox = inbox;
                        if (mFolder.id != -1)
                            return OpenVBX.API.getFolder(mFolder.id);
                        else if (!OpenVBX.inbox.folders.isEmpty())
                            return OpenVBX.API.getFolder(OpenVBX.inbox.folders.get(0).id);
                        else
                            return Observable.just(new Folder());
                    }
                })
                .compose(OpenVBX.<Folder>defaultSchedulers())
                .subscribe(new Action1<Folder>() {
                    @Override
                    public void call(Folder selected) {
                        mFolder = selected;
                        updateNavigationMenu();
                        messagesAdapter.update(mFolder.messages.items);
                        toolbar.setTitle(mFolder.name == null ? getString(R.string.app_name) : mFolder.name);
                        progress.setVisibility(View.GONE);
                        refreshView.setRefreshing(false);
                    }
                });
    }

    private void updateNavigationMenu() {
        Menu menu = navigationView.getMenu();
        menu.removeGroup(R.id.primary_group);
        for(int i = 0; i < OpenVBX.inbox.folders.size(); i++) {
            Folder folder = OpenVBX.inbox.folders.get(i);
            MenuItem menuItem = menu.add(R.id.primary_group, i, i, folder.name);
            menuItem.setIcon("inbox".equals(folder.type) ? R.drawable.ic_inbox_white_48dp : R.drawable.ic_folder_white_48dp);
        }
        invalidateNavigationMenu();
    }

    private void invalidateNavigationMenu() {
        for (int i = 0, count = navigationView.getChildCount(); i < count; i++) {
            final View child = navigationView.getChildAt(i);
            if (child != null && child instanceof ListView) {
                final ListView menuView = (ListView) child;
                final HeaderViewListAdapter adapter = (HeaderViewListAdapter) menuView.getAdapter();
                final BaseAdapter wrapped = (BaseAdapter) adapter.getWrappedAdapter();
                wrapped.notifyDataSetChanged();
            }
        }
    }

    private void selectFolder(int folder_id) {
        if(mFolder.id != folder_id)
            progress.setVisibility(View.VISIBLE);
        OpenVBX.API.getFolder(folder_id)
                .compose(OpenVBX.<Folder>defaultSchedulers())
                .subscribe(new Action1<Folder>() {
                    @Override
                    public void call(Folder folder) {
                        mFolder = folder;
                        messagesAdapter.update(mFolder.messages.items);
                        toolbar.setTitle(folder.name);
                        progress.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inbox, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent i;
        switch(id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_new_sms:
                i = new Intent(mContext, SmsActivity.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}