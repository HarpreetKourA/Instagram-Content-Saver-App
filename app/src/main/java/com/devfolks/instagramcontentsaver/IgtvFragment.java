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
import android.widget.MediaController;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.devfolks.instagramcontentsaver.databinding.FragmentIgtvBinding;
import com.devfolks.instagramcontentsaver.databinding.FragmentReelBinding;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.StringUtils;

public class IgtvFragment extends Fragment {
    private FragmentIgtvBinding binding;
    String Url="NULL";
    private MediaController mediaController;
    String igtvUrl="1";
    private Uri uri2;


    public IgtvFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding=FragmentIgtvBinding.inflate(getLayoutInflater());
        View v= binding.getRoot();
        mediaController=new MediaController(getContext());
        mediaController.setAnchorView(binding.particularIgtv);
        binding.getIgtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Url=binding.getIgtvLink.getText().toString().trim();
                if(binding.getIgtvLink.equals("NULL")){
                    Toast.makeText(getContext(),"First enter URL", Toast.LENGTH_SHORT).show();
                }
                else{
                    String result2= StringUtils.substringBefore(Url,"/?");
                    Url=result2+"/?__a=1";
                    processData();

                }
            }
        });
        binding.downloadIgtv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!igtvUrl.equals("1")){
                    DownloadManager.Request request=new DownloadManager.Request(uri2);
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI| DownloadManager.Request.NETWORK_MOBILE);
                    request.setTitle("Download");
                    request.setDescription(".......");
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM,""+System.currentTimeMillis()+".mp4");
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
        StringRequest request= new StringRequest(Url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                MainURL mainURL = gson.fromJson(response, MainURL.class);
                igtvUrl = mainURL.getGraphql().getShortcode_media().getVideo_url();
                uri2 = Uri.parse(igtvUrl);
                binding.particularIgtv.setMediaController(mediaController);
                binding.particularIgtv.setVideoURI(uri2);
                binding.particularIgtv.requestFocus();
                binding.particularIgtv.start();
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