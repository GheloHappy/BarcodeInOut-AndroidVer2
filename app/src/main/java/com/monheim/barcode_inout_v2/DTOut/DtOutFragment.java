package com.monheim.barcode_inout_v2.DTOut;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.monheim.barcode_inout_v2.R;

import java.util.List;
import java.util.Map;

import MssqlCon.PublicVars;

public class DtOutFragment extends Fragment {
    SimpleAdapter simAd;
    DtOutFunctions dtOutFunc = new DtOutFunctions();
    String dtDate ="";
    String dt = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dt_out, container, false);

        Spinner spinDtDate = rootView.findViewById(R.id.spinDtDate);
        Spinner spinDt = rootView.findViewById(R.id.spinDT);
        ListView lvDTOut = rootView.findViewById(R.id.lvDTOut);
        EditText etDtOutBarcode = rootView.findViewById(R.id.etDtOutBarcode);
        EditText etDtOutQty = rootView.findViewById(R.id.etDtOutQty);

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, dtOutFunc.GetDTDate());
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinDtDate.setAdapter(adapter);

        spinDtDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dtDate = parent.getItemAtPosition(position).toString();
                ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, dtOutFunc.GetDt(dtDate));
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinDt.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinDt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dt = parent.getItemAtPosition(position).toString();
                ListBarcodeTran(lvDTOut);
                etDtOutBarcode.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        etDtOutBarcode.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {

                String barcode = etDtOutBarcode.getText().toString();
                int qty = Integer.parseInt(etDtOutQty.getText().toString());
                String solomonID = dtOutFunc.GetSolomonID(barcode);
                if (solomonID != "NA") {
                    dtOutFunc.UpdateDtItem(dtDate,dt,solomonID,qty);
                    ListBarcodeTran(lvDTOut);
                } else {
                    Toast.makeText(getActivity(),"Item not found.", Toast.LENGTH_SHORT).show();
                }
                etDtOutBarcode.setText("");
                etDtOutBarcode.post(() -> etDtOutBarcode.requestFocus()); //focus request
                return true;
            }
            return false;
        });

        return  rootView;
    }

    private void ListBarcodeTran(ListView lvDTOut) {
        List<Map<String, String>> dataList;
        dataList = dtOutFunc.GetDTList(dt);

        String[] from = {"solomonID","uom","qty","qtyOut"};
        int[] to = {R.id.description,R.id.barcode,R.id.sapCode,R.id.qty};
        simAd = new SimpleAdapter(getActivity(),dataList,R.layout.temp_barcode_tran_list_template,from,to);
        lvDTOut.setAdapter(simAd);
    }
}