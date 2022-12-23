package com.devfolks.instagramcontentsaver;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.devfolks.instagramcontentsaver.databinding.FragmentPhotoBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringUtils;


public class PhotoFragment extends Fragment {
    String url="NULL";
    private FragmentPhotoBinding binding;
    String photoUrl="1";
    private Uri uri2;


    public PhotoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentPhotoBinding.inflate(getLayoutInflater());
        View v= binding.getRoot();
        binding.getPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url=binding.getPhotoLink.getText().toString().trim();
                if(binding.getPhotoLink.equals("NULL")){
                    Toast.makeText(getContext(),"First enter URL", Toast.LENGTH_SHORT).show();
                }
                else{
                    String result2= StringUtils.substringBefore(url,"/?");
                    url=result2+"/?__a=1";
                    processData();

                }
            }
        });
        binding.downloadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!photoUrl.equals("1")){
                    DownloadManager.Request request=new DownloadManager.Request(uri2);
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI| DownloadManager.Request.NETWORK_MOBILE);
                    request.setTitle("Download");
                    request.setDescription(".......");
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM,""+System.currentTimeMillis()+".jpeg");
                    DownloadManager manager=(DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                    manager.enqueue(request);
                    Toast.makeText(getContext(),"Downloaded",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(),"No video to download", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }
    private void processData(){
        StringRequest request= new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                MainURL mainURL = gson.fromJson(response, MainURL.class);
                photoUrl = mainURL.getGraphql().getShortcode_media().getDisplay_url();
                uri2 = Uri.parse(photoUrl);
                Glide.with(getContext()).load(uri2).into(binding.particularPhoto);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(),"Not able to fetch Url", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue= Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}