package com.effone.notificationmummy.fragments;

import android.annotation.TargetApi;

import java.util.Calendar;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.effone.notificationmummy.R;
import com.effone.notificationmummy.adapter.SectionListDataAdapter;
import com.effone.notificationmummy.database.DataBaseHandler;
import com.effone.notificationmummy.model.ImageInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GifFragment extends Fragment implements View.OnClickListener {
    DataBaseHandler mDataBaseHandler;
    private RecyclerView mRvMedia;
    private String timeStamp;
    private Toolbar mTbHeader;
    private TextView mTvTitle;
    private ImageView mIvBackArrow;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_image, container, false);
        mIvBackArrow=view.findViewById(R.id.img_backArrow);
        mIvBackArrow.setOnClickListener(this);
        mTvTitle=view.findViewById(R.id.title);
        mTvTitle.setText("GIF");
        mRvMedia=view.findViewById(R.id.rv_media);
        mDataBaseHandler= DataBaseHandler.getInstance(getActivity());
        mRvMedia.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        new basedOnTheTimeStamp().execute("");
        return view;
    }

    public void sendTimeStamp(String sender_message_time_stamp) {
        this.timeStamp=sender_message_time_stamp;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_backArrow:
                getActivity().onBackPressed();
                break;
        }
    }

    private class basedOnTheTimeStamp extends AsyncTask<String,Void,ArrayList<ImageInfo>> {
        ArrayList<ImageInfo> imageDetails = new ArrayList<>();
        File path;
        @Override
        protected ArrayList<ImageInfo> doInBackground(String... parms) {
            path = new File(Environment.getExternalStorageDirectory(), "WhatsApp/Media/WhatsApp Animated Gifs");
            String[] fileNames = new String[0];
            if (path.exists()) {
                fileNames = path.list();
            }
            try {
                for (int i = 0; i < fileNames.length; i++) {
                    try {
                        File file = new File(path.getPath() + "/" + fileNames[i]);
                        ImageInfo imageInfo = new ImageInfo();
                        imageInfo.setPath(path.getPath() + "/" + fileNames[i]);
                        imageInfo.setTimestamp(file.lastModified());
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        String[] imageDate = formatter.format(new Date(file.lastModified())).split(" ");
                        String[] selectedImageDate = formatter.format(new Date(Long.parseLong(timeStamp))).split(" ");
                        if (imageDate[0].equals(selectedImageDate[0]))
                            if (!fileNames[i].equals("Sent"))
                                imageDetails.add(imageInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                Log.e("fileNames", e.getMessage());
            }
            ArrayList<ImageInfo> near = new ArrayList<>();
            near = getNearestDate(imageDetails, timeStamp);
            return near;
        }
        @Override
        protected void onPostExecute(ArrayList<ImageInfo> result) {
            SectionListDataAdapter sectionListDataAdapter = new SectionListDataAdapter(getContext(), result, false);
            mRvMedia.setAdapter(sectionListDataAdapter);

        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static ArrayList<ImageInfo> getNearestDate(ArrayList<ImageInfo> imageInfo, String selectedImageDate) {
        ArrayList<ImageInfo> nearset = new ArrayList<>();
        Date selectedImage = new Date(Long.parseLong(selectedImageDate));
        try {
            for (int i = 0; i < imageInfo.size(); i++) {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String[] date=formatter.format(new Date(Long.parseLong(selectedImageDate))).split(" GMT");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date before =
                        sdf.parse(formatter.format(new Date(imageInfo.get(i).getTimestamp())).split(" GMT")[0]);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(before);
                calendar.add(Calendar.MINUTE, 1);
                before=calendar.getTime();
                calendar.add(Calendar.MINUTE ,-2);
                Date  after=calendar.getTime();


                Date toCheck =  sdf.parse(date[0]);
//is toCheck between the two?
                boolean isAvailable = (after.getTime() <= toCheck.getTime()) && (before.getTime() >= toCheck.getTime());
                if (isAvailable) {
                    nearset.add(imageInfo.get(i));
                }
            }

        }catch (Exception e){
            e.getMessage();
        }



        return nearset;
    }

}

