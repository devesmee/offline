package com.example.offline;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.offline.backend.apps.SystemApp;

import java.util.ArrayList;
import java.util.List;

public class WhiteListAppRecyclerViewAdapter extends RecyclerView.Adapter<WhiteListAppRecyclerViewAdapter.ViewHolder> {

    private final List<SystemApp> mValues;
    private IWhiteListAppSelectorInterface selectedAppListener;
    public ArrayList<String> selectedApps = new ArrayList<>();

    public WhiteListAppRecyclerViewAdapter(List<SystemApp> items, Context context) {
        this.mValues = items;
        // automatically add our application as a white listed app
        selectedApps.add(context.getPackageName());

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_whitelisted_apps_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final SystemApp systemApp = mValues.get(position);
        holder.mCheckBox.setOnCheckedChangeListener(null);
        holder.mItem = systemApp;
        holder.mIdView.setText(systemApp.getAppName());
        holder.mAppImageView.setImageDrawable(systemApp.getAppIcon());
        holder.mCheckBox.setChecked(systemApp.isSelected());

        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                systemApp.setSelected(isChecked);
                if (!isChecked) {
                    selectedApps.remove(systemApp.getPackageName());
                    if (selectedAppListener != null) {
                        selectedAppListener.onAppDeselected(selectedApps);
                    }
                } else {
                    selectedApps.add(systemApp.getPackageName());
                    if (selectedAppListener != null) {
                        selectedAppListener.onAppSelected(selectedApps);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void setSelectedAppListener(IWhiteListAppSelectorInterface selectedAppListener) {
        this.selectedAppListener = selectedAppListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final ImageView mAppImageView;
        public final CheckBox mCheckBox;
        public SystemApp mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mAppImageView = view.findViewById(R.id.appIcon);
            mCheckBox = view.findViewById(R.id.appCheckBox);
        }
    }
}
