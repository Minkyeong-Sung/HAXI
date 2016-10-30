package com.ensharp.haxi.InternationalTaxi;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ensharp.haxi.R;

public class InternationalTaxi_fare_fragment extends Fragment {

    WebView web;
    StringBuilder fare_url= new StringBuilder("http://www.intltaxi.co.kr/Home/rates");
    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.internationaltaxi_fare_layout,container,false);
        init();

        return view;
    }
    private void init() {
        web=(WebView)view.findViewById(R.id.web);
        web.getSettings().setJavaScriptEnabled(true);
        web.setWebViewClient(new WebViewClient());
        fare_url.append(InternationalTaxiActivity.URL_locale);
        web.loadUrl(fare_url.toString());
    }
}