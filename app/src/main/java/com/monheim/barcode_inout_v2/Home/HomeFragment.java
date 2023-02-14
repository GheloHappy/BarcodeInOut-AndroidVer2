package com.monheim.barcode_inout_v2.Home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;
import com.monheim.barcode_inout_v2.BarcodeInOut.BarcodeInOutFunctions;
import com.monheim.barcode_inout_v2.R;

import java.util.List;
import java.util.Map;

import MssqlCon.PublicVars;

public class HomeFragment extends Fragment {
    SimpleAdapter simAd;
    HomeFunctions homeFunc = new HomeFunctions();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        EditText etSearchRefNbr = rootView.findViewById(R.id.etSearchRefNbr);
        ListView lvBarcodeTrans = rootView.findViewById(R.id.lvHomeBarcodeTrans);
        Button btnSearch = rootView.findViewById(R.id.btnSearch);
        TextView tvTotCs = rootView.findViewById(R.id.tvHomeTotCS);

        etSearchRefNbr.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                btnSearch.callOnClick();
                return true;
            }
            return false;
        });
        btnSearch.setOnClickListener(v -> {
            String refNbr = etSearchRefNbr.getText().toString();
            if(homeFunc.GetTotCs(tvTotCs, refNbr) == true) {
                ListBarcodeTran(lvBarcodeTrans, refNbr);
            } else {
                Toast.makeText(getActivity(), "Reference number not found.", Toast.LENGTH_SHORT).show();
            }
        });
        return  rootView;
    }

    private void ListBarcodeTran(ListView lvBarcodeTrans, String refNbr) {
        List<Map<String, String>> dataList;
        dataList = homeFunc.GetBarList(refNbr);

        String[] from = {"barcode","solomonID","description","uom","qty"};
        int[] to = {R.id.barcode,R.id.tranType,R.id.description,R.id.uom,R.id.qty};
        simAd = new SimpleAdapter(getActivity(),dataList,R.layout.temp_barcode_tran_list_template,from,to);
        lvBarcodeTrans.setAdapter(simAd);
    }
}