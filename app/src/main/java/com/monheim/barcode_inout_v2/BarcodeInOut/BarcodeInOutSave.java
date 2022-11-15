package com.monheim.barcode_inout_v2.BarcodeInOut;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.monheim.barcode_inout_v2.R;

import java.util.List;
import java.util.Map;

public class BarcodeInOutSave extends Fragment {
    SimpleAdapter simAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_barcode_in_out_save, container, false);

        ListView lvTempBarcodeTran = rootView.findViewById(R.id.lvBarcodeTrans);
        Button btn = rootView.findViewById(R.id.btn);

        btn.setOnClickListener(v -> {
            List<Map<String, String>> dataList = null;
            BarcodeInOutFunctions barFUnc = new BarcodeInOutFunctions();
            dataList = barFUnc.GetTempBarList();

            String[] from = {"id","tranType","barcode","description"};
            int[] to = {R.id.id,R.id.tranType,R.id.barcode,R.id.description};
            simAd = new SimpleAdapter(getActivity(),dataList,R.layout.temp_barcode_tran_list_template,from,to);
            lvTempBarcodeTran.setAdapter(simAd);

            System.out.println("test");
        });

        return rootView;
    }
}