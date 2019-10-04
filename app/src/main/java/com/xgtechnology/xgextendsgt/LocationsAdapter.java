package com.xgtechnology.xgextendsgt;

/**
 * Created by timr on 1/29/2018.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class LocationsAdapter extends ArrayAdapter<ContactLocation> {

    ArrayList<ContactLocation> locationsList = new ArrayList<>();

    public LocationsAdapter(Context context, int textViewResourceId, ArrayList<ContactLocation> objects) {
        super(context, textViewResourceId, objects);
        locationsList = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.layoutitems, null);
        RelativeLayout rlView = (RelativeLayout)v.findViewById(R.id.rlItems);
        TextView tvPos = (TextView) v.findViewById(R.id.tvItemsPos);
        TextView tvItemsUnitName = (TextView) v.findViewById(R.id.tvItemsUnitName);
        TextView tvItemsCallSign = (TextView) v.findViewById(R.id.tvItemsCallSign);
        TextView tvItemGIDLbl = (TextView) v.findViewById(R.id.tvItemsLblGID);
        TextView tvItemsGID = (TextView) v.findViewById(R.id.tvItemsGID);
        TextView tvItemIPLbl = (TextView) v.findViewById(R.id.tvItemsLblIP);
        TextView tvItemsIP = (TextView) v.findViewById(R.id.tvItemsIP);
        TextView tvItemsLat = (TextView) v.findViewById(R.id.tvItemsLat);
        TextView tvItemsLng = (TextView) v.findViewById(R.id.tvItemsLng);
        TextView tvItemsDistance = (TextView) v.findViewById(R.id.tvItemsDistance);
        TextView tvItemsDistanceUnits = (TextView) v.findViewById(R.id.tvItemsLblLocatedUnits);
        TextView tvItemsClockDirectiom = (TextView) v.findViewById(R.id.tvItemsClockDirection);



        if (position % 2 == 0)
            rlView.setBackgroundColor(Color.CYAN);
        else {
            rlView.setBackgroundColor(Color.WHITE);

        }
        tvPos.setText(String.valueOf(position));
        tvItemsUnitName.setText(locationsList.get(position).getUnitName());

        tvItemsCallSign.setText(locationsList.get(position).getCallSign());

        tvItemsGID.setText(locationsList.get(position).getID());
        tvItemsGID.setHeight(0);
        tvItemGIDLbl.setHeight(0);
        tvItemIPLbl.setHeight(0);
        tvItemsIP.setHeight(0);
        tvItemsIP.setText(locationsList.get(position).getIPaddress());
        tvItemsLat.setText(locationsList.get(position).getLatitude());
        tvItemsLng.setText(locationsList.get(position).getLongitude());
        float dist = Float.valueOf(locationsList.get(position).getdistance());
        String sLbl = "m";
        if(dist > 1000)
        {
            dist = dist / 1000;
            sLbl = "km";
        }
        String sF4 = "    ";
        String sZ4 = "0000";
        String sDist =  String.format("%.4f", dist);
        String sP[] = sDist.split("\\.");
        if(sP[0].length() < 4)
        {
            sP[0] = sF4.substring(0,(3 - sP[0].length())) + sP[0];
        }
        if(sP[1].length() < 4)
        {
            sP[1] = sZ4.substring(0,(3 - sP[0].length())) + sP[0];
        }
        sDist = sP[0] + "." + sP[1];

        tvItemsDistance.setText(sDist);
        tvItemsDistanceUnits.setText(sLbl);
        tvItemsClockDirectiom.setText(locationsList.get(position).getClockheading());

        return v;

    }

}
