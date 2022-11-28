package com.monheim.barcode_inout_v2.NewBarcode;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.monheim.barcode_inout_v2.BarcodeInOut.BarcodeInOutFunctions;
import com.monheim.barcode_inout_v2.R;

import java.util.List;
import java.util.Map;

import MssqlCon.Logs;

public class NewBarcodeFragment extends Fragment {
    SimpleAdapter simAd;
    NewBarcodeFunctions newBarFunc = new NewBarcodeFunctions();
    Logs log = new Logs();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_new_barcode, container, false);

        ListView lvUnknownBarList = rootView.findViewById(R.id.lvUnknownBarList);
        EditText etUBarcode = rootView.findViewById(R.id.etUBarcode);
        EditText etUSapCode = rootView.findViewById(R.id.etUSapCode);
        EditText etUDesc = rootView.findViewById(R.id.etUDescription);
        EditText etUSolomonID = rootView.findViewById(R.id.etUSolomonID);
        EditText etUUOM = rootView.findViewById(R.id.etUUOM);
        EditText etUCsPkg = rootView.findViewById(R.id.etUCsPkg);
        Button btnUSave = rootView.findViewById(R.id.btnUSave);

        UnknownBarcodeList(lvUnknownBarList);


        lvUnknownBarList.setOnItemClickListener((parent, view, position, id) -> {
            TextView tvUBar = view.findViewById(R.id.unknownBarcode);
            String item = tvUBar.getText().toString();

            etUBarcode.setText(item);
        });
        lvUnknownBarList.setOnItemLongClickListener((parent, view, position, id) -> {
            TextView tvUBar = view.findViewById(R.id.unknownBarcode);
            String item = tvUBar.getText().toString();

            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_delete)
                    .setTitle("Are you sure ?")
                    .setMessage("Do you want to delete this item")
                    .setPositiveButton("Yes",(dialog, which) -> {
                        newBarFunc.DeleteUnknownBarcode(item);;
                        UnknownBarcodeList(lvUnknownBarList);
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        });

        btnUSave.setOnClickListener(v -> {
            String barcode = etUBarcode.getText().toString();
            String sapCode = etUSapCode.getText().toString();
            String desc = etUDesc.getText().toString();
            String solomonId = etUSolomonID.getText().toString();
            String uom = etUUOM.getText().toString();
            String csPkg = etUCsPkg.getText().toString();

            if(barcode.matches("") || sapCode.matches("") || desc.matches("") ||
                    solomonId.matches("") || uom.matches("") || csPkg.matches("")) {
                Toast.makeText(getActivity(), "PLEASE FILL ALL FIELDS.", Toast.LENGTH_SHORT).show();
            } else {
                if (!newBarFunc.InsertToProducts(barcode, sapCode, desc, solomonId,uom, Integer.parseInt(csPkg))) {
                    Toast.makeText(getActivity(), "UPDATE BARCODE FAILED.", Toast.LENGTH_SHORT).show();
                } else {
                    newBarFunc.DeleteUnknownBarcode(barcode);
                    log.InsertUserLog("ADD new Barcode",barcode); //logUser
                    Toast.makeText(getActivity(), "NEW BARCODE ADDED!.", Toast.LENGTH_SHORT).show();
                    etUBarcode.getText().clear();
                    etUSapCode.getText().clear();
                    etUDesc.getText().clear();
                    etUSolomonID.getText().clear();
                    etUUOM.getText().clear();
                    etUCsPkg.getText().clear();
                    UnknownBarcodeList(lvUnknownBarList);
                }
            }
        });

        return rootView;
    }

    private void UnknownBarcodeList(ListView lvUnknownBarList) {
        List<Map<String, String>> dataList;
        dataList = newBarFunc.GetUnknownBarList();

        String[] from = {"barcode"};
        int[] to = {R.id.unknownBarcode};
        simAd = new SimpleAdapter(getActivity(),dataList,R.layout.unknown_barcode_template,from,to);
        lvUnknownBarList.setAdapter(simAd);
    }
}