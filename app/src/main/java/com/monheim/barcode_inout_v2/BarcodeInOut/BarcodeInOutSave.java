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

        ListTempBarcodeTran(rootView);

        return rootView;
    }

    private void ListTempBarcodeTran(View v) {
        ListView lvTempBarcodeTran = v.findViewById(R.id.lvBarcodeTrans);

        List<Map<String, String>> dataList;
        BarcodeInOutFunctions barFUnc = new BarcodeInOutFunctions();
        dataList = barFUnc.GetTempBarList();

        String[] from = {"id","barcode","sapCode","description","uom","qty"};
        int[] to = {R.id.id,R.id.barcode,R.id.sapCode,R.id.description,R.id.uom,R.id.qty};
        simAd = new SimpleAdapter(getActivity(),dataList,R.layout.temp_barcode_tran_list_template,from,to);
        lvTempBarcodeTran.setAdapter(simAd);
    }
}