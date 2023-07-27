package com.monheim.barcode_inout_v2.BarcodeInOut;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.monheim.barcode_inout_v2.NewBarcode.NewBarcodeFunctions;
import com.monheim.barcode_inout_v2.R;

import java.util.List;
import java.util.Map;

import MssqlCon.PublicVars;

public class BarcodeOutFragment extends Fragment {
    BarcodeInOutFunctions barInOut = new BarcodeInOutFunctions();
    NewBarcodeFunctions newBarFunc = new NewBarcodeFunctions();
    PublicVars publVars = new PublicVars();
    String user, itemDesc,solomonId;

    SimpleAdapter simAd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_barcode_out, container, false);

        EditText etBarcode = rootView.findViewById(R.id.etBarcode);
        EditText etBarcodeQty = rootView.findViewById(R.id.etBarcodeQty);
        TextView tvBarcode = rootView.findViewById(R.id.tvBarcode);
        TextView tvDesc = rootView.findViewById(R.id.tvDesc);
        Spinner spUom = rootView.findViewById(R.id.spUom);
        Button btnSave = rootView.findViewById(R.id.btnSave);
        TextView tvItemCount = rootView.findViewById(R.id.tvItemCount);

        //Uncomment Cabrera
//        etBarcodeQty.setEnabled(true);
//
//        etBarcode.requestFocus();

        if(barInOut.CheckTempBarTranData(user) == true) {
            btnSave.setEnabled(true);
        }else {
            btnSave.setEnabled(false);
        }

        user = publVars.GetUser();

        //Uncomment Cabrera
//        spUom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (parent.getItemAtPosition(position).toString().equals("PCS")) {
//                    etBarcodeQty.setEnabled(true);
//                } else {
//                    etBarcodeQty.setText("1");
//                    etBarcodeQty.setEnabled(false);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });

        etBarcode.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {

                String barcode = etBarcode.getText().toString();
                String uom = spUom.getSelectedItem().toString();
                Integer qty = Integer.parseInt(etBarcodeQty.getText().toString());

                if (barInOut.GetBarcode(barcode,tvBarcode,tvDesc) == true) {
                    List<Map<String, String>> dataList;
                    dataList = barInOut.GetMultiBarcode(barcode);

                    if(barInOut.CheckMultiBarcode(barcode)){
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Detected multiple solomon ID");
                        builder.setMessage("Please select appropriate solomon ID");
                        builder.setCancelable(false);
                        builder.setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                        });
                        builder.setPositiveButton("Save", (dialog, which) -> {
                            barInOut.InsertIn(barcode,uom,qty * -1,"OUT", user, itemDesc, solomonId);
                            PublicVars.GetNav().getMenu().findItem(R.id.barcodeIn).setEnabled(false); //disable Barcode Out in Menu if tempbarcode has data of IN
                            btnSave.setEnabled(true);
                        });

                        ListView listView = new ListView(getContext());

                        String[] from = {"barcode","description","solomonID"};
                        int[] to = {R.id.barcode,R.id.description,R.id.qty};
                        simAd = new SimpleAdapter(getActivity(),dataList,R.layout.temp_barcode_tran_list_template,from,to);
                        listView.setAdapter(simAd);

                        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                        builder.setView(listView);

                        final AlertDialog dialog = builder.create();

                        listView.setOnItemClickListener((parent, view, position, id) -> {
                            for (int i = 0; i < parent.getChildCount(); i++) {
                                TextView tvItemDesc = parent.getChildAt(i).findViewById(R.id.description);
                                TextView tvSolomonID = parent.getChildAt(i).findViewById(R.id.qty);

                                if (i == position) {
                                    tvItemDesc.setTextColor(Color.RED);
                                    tvSolomonID.setTextColor(Color.RED);

                                    itemDesc = tvItemDesc.getText().toString();
                                    solomonId = tvSolomonID.getText().toString();
                                } else {
                                    tvItemDesc.setTextColor(Color.BLACK);
                                    tvSolomonID.setTextColor(Color.BLACK);
                                }
                            }
                        });

                        dialog.show();
                    } else {
                        String itemDesc = null;
                        String solomonID = null;;

                        for (Map<String, String> dataMap : dataList) {
                            itemDesc = dataMap.get("description");
                            solomonID = dataMap.get("solomonID");
                        }

                        barInOut.InsertIn(barcode,uom,qty * -1,"OUT", user, itemDesc, solomonID);
                    }
                    tvItemCount.setText(String.valueOf(barInOut.GetItemCount(barcode)));
                } else {
                    newBarFunc.CheckUnknownBarcode(barcode,user);
                }

                if(barInOut.CheckTempBarTranData(user) == true) {
                    PublicVars.GetNav().getMenu().findItem(R.id.barcodeIn).setEnabled(false); //disable Barcode Out in Menu if tempbarcode has data of OUT
                    btnSave.setEnabled(true);
                }

                etBarcodeQty.setText("1");
                etBarcode.setText("");
                etBarcode.post(() -> etBarcode.requestFocus()); //focus request
                return true;
            }

            etBarcode.post(() -> etBarcode.requestFocus()); //focus request
            return false;
        });

        etBarcodeQty.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    ((keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) || keyCode == KeyEvent.KEYCODE_ENTER)) {

                etBarcode.post(() -> etBarcode.requestFocus()); //focus request
                return true;
            }
            return false;
        });

        BarcodeInOutSaveFragment barSaveFrag = new BarcodeInOutSaveFragment();
        btnSave.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container,barSaveFrag).commit();
        });

        return rootView;
    }
}