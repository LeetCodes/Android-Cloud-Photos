package com.cloud.cloudphotos.provider.rackspace;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cloud.cloudphotos.R;

public class ContainerListAdapter extends BaseAdapter {

    private final Activity activity;
    private final ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;

    public ContainerListAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.provider_rackspace_choose_container_item, null);

        TextView name = (TextView) vi.findViewById(R.id.name);
        TextView count = (TextView) vi.findViewById(R.id.count);

        HashMap<String, String> container = new HashMap<String, String>();
        container = data.get(position);

        name.setText(container.get("name"));
        String ocount = container.get("count");
        String o = " objects";
        if (ocount == "1") {
            o = " object";
        }
        count.setText(container.get("count") + o);
        return vi;
    }
}