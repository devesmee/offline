package com.example.offline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.offline.backend.apps.SystemManager;
import com.example.offline.backend.appusage.Statistics;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class SystemAppAdapter extends BaseAdapter {
    private final ArrayList<Statistics> systemApps;
    LayoutInflater inflater;
    private SystemManager systemManager;
    private Context mContext;

    public SystemAppAdapter(Context context, ArrayList systemApps) {
        this.systemApps = systemApps;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        systemManager = SystemManager.getInstance(context);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return systemApps.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_app_usage_content, null);
        }

        Statistics systemApp = systemApps.get(position);

        //set icon
        ImageView image = (ImageView) convertView.findViewById(R.id.appUsageContentIMG);
        image.setImageDrawable(systemApp.getIcon());
        //set app name
        TextView appNameText = (TextView) convertView.findViewById(R.id.appUsageName);
        appNameText.setText(systemApp.getAppName());
        //set app usage
        TextView appTimeText = convertView.findViewById(R.id.appUsageTime);

        @SuppressLint("DefaultLocale") String totalSecondsOffScreenFormattedValue = String.format("%d m %d s",
                TimeUnit.MILLISECONDS.toMinutes(systemApp.getUsage()),
                TimeUnit.MILLISECONDS.toSeconds(systemApp.getUsage()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(systemApp.getUsage()))
        );

        appTimeText.setText(totalSecondsOffScreenFormattedValue);

        return convertView;
    }
}
