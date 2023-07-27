package com.monheim.barcode_inout_v2.Van;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.monheim.barcode_inout_v2.BarcodeInOut.BarcodeInOutFunctions;
import com.monheim.barcode_inout_v2.NewBarcode.NewBarcodeFunctions;
import com.monheim.barcode_inout_v2.R;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import MssqlCon.Logs;
import MssqlCon.PublicVars;

public class VanFragment extends Fragment {
    VanFunctions vanFunc = new VanFunctions();
    PublicVars pubVars = new PublicVars();
    NewBarcodeFunctions newBarFunc = new NewBarcodeFunctions();
    BarcodeInOutFunctions barInOut = new BarcodeInOutFunctions();
    SimpleAdapter simAd;
    String user, refNbr, searchDate, solomonID, uom;
    int qty;
    ListView lvVanOut;
    TextView tvTotCs,tvTotCaseShot,tvTotPcs,tvTotPcsShot;
    Spinner spinUom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_van, container, false);

        EditText etSearchDate = view.findViewById(R.id.etSearchVanDate);
        EditText etVanOutQty = view.findViewById(R.id.etVanOutQty);
        EditText etVanOutBarcode = view.findViewById(R.id.etVanOutBarcode);
        Spinner spinRefNbr = view.findViewById(R.id.spinRefNbr);
        spinUom = view.findViewById(R.id.spInvtUom);
        lvVanOut = view.findViewById(R.id.lvVanOut);

        tvTotCs = view.findViewById(R.id.tvTotCase);
        tvTotCaseShot = view.findViewById(R.id.tvTotCaseShot);
        tvTotPcs = view.findViewById(R.id.tvTotPcs);
        tvTotPcsShot = view.findViewById(R.id.tvTotPcsShot);

        user = pubVars.GetUser();

        //Uncomment Cabrera
//        etVanOutQty.setEnabled(false);
//        spinUom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (parent.getItemAtPosition(position).toString().equals("PCS")) {
//                    etVanOutQty.setEnabled(true);
//                } else {
//                    etVanOutQty.setText("1");
//                    etVanOutQty.setEnabled(false);
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });

        etSearchDate.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;
            private int inputLength;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (isFormatting) {
                    return;
                }
                inputLength = s.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isFormatting) {
                    return;
                }
                int newLength = s.length();
                String digits = s.toString().replaceAll("[^\\d]", "");
                if (newLength != inputLength && digits.length() < 8) {
                    isFormatting = true;
                    if (digits.length() >= 4) {
                        if (digits.length() == 4 || digits.charAt(4) != '-') {
                            etSearchDate.setText(String.format("%s-%s", digits.substring(0, 4), digits.substring(4)));
                            etSearchDate.setSelection(etSearchDate.getText().length());
                        }
                    }
                    if (digits.length() >= 7) {
                        if (digits.length() == 7 || digits.charAt(7) != '-') {
                            etSearchDate.setText(String.format("%s-%s-%s", digits.substring(0, 4), digits.substring(4, 6), digits.substring(6)));
                            etSearchDate.setSelection(etSearchDate.getText().length());
                        }
                    }
                    isFormatting = false;
                }

                searchDate = etSearchDate.getText().toString();

                if (etSearchDate.length() == 10) {
                    ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, vanFunc.GetVan(searchDate));
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    spinRefNbr.setAdapter(adapter);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) {
                    return;
                }
                if (s.length() > 10) {
                    isFormatting = true;
                    s.delete(10, s.length());
                    isFormatting = false;
                }
            }
        });

        spinRefNbr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refNbr = parent.getItemAtPosition(position).toString();
                ListBarcodeTran(lvVanOut);
                etVanOutBarcode.requestFocus();
                DisplayTotCs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etVanOutQty.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NAVIGATE_NEXT)) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0); // close keyboard
            }
            return false;
        });

        etVanOutBarcode.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {

                String barcode = etVanOutBarcode.getText().toString();
                qty = Integer.parseInt(etVanOutQty.getText().toString());
                uom = spinUom.getSelectedItem().toString();
                solomonID = vanFunc.GetSolomonID(barcode, refNbr, uom);

                if (Objects.equals(solomonID, "NA")) {
                    new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("WARNING!")
                            .setMessage("Item not found! Item barcode is added in New Barcode tab")
                            .setPositiveButton("OK",(dialog, which) -> {
                            })
                            .show();
                    newBarFunc.CheckUnknownBarcode(barcode, user);
                } else if (Objects.equals(solomonID, "NAUOM")){
                    new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("WARNING!")
                            .setMessage("Please select correct UOM")
                            .setPositiveButton("OK",(dialog, which) -> {
                            })
                            .show();
                } else if (Objects.equals(solomonID, "NAITEM")){
                    List<Map<String, String>> dataList;
                    dataList = barInOut.GetMultiBarcode(barcode);

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Detected multiple solomon ID");
                    builder.setMessage("Please select appropriate solomon ID");
                    builder.setCancelable(false);
                    builder.setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    });
                    builder.setPositiveButton("Save", (dialog, which) -> {
                        UpdateDt();
                    });

                    ListView listView = new ListView(getContext());

                    String[] from = {"barcode","description","solomonID"};
                    int[] to = {R.id.barcode,R.id.description,R.id.qty};
                    simAd = new SimpleAdapter(getActivity(),dataList,R.layout.temp_barcode_tran_list_template,from,to);
                    listView.setAdapter(simAd);

                    listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
                    builder.setView(listView);

                    final AlertDialog dialog = builder.create();

                    listView.setOnItemClickListener((parent, customView, position, id) -> {
                        for (int i = 0; i < parent.getChildCount(); i++) {
                            TextView tvItemDesc = parent.getChildAt(i).findViewById(R.id.description);
                            TextView tvSolomonID = parent.getChildAt(i).findViewById(R.id.qty);

                            if (i == position) {
                                tvItemDesc.setTextColor(Color.RED);
                                tvSolomonID.setTextColor(Color.RED);

                                solomonID = tvSolomonID.getText().toString();
                            } else {
                                tvItemDesc.setTextColor(Color.BLACK);
                                tvSolomonID.setTextColor(Color.BLACK);
                            }
                        }
                    });

                    dialog.show();
                } else {
                    UpdateDt();
                }

                etVanOutQty.setText("1");
                etVanOutBarcode.setText("");
                etVanOutBarcode.post(() -> etVanOutBarcode.requestFocus()); //focus request

                return true;
            }
            etVanOutBarcode.post(() -> etVanOutBarcode.requestFocus()); //focus request
            return false;
        });

        return view;
    }

    private void UpdateDt(){
        if(vanFunc.GetLastQty(searchDate,refNbr,solomonID,uom)) {
            if (vanFunc.UpdateVanItem(qty, uom)){
                DisplayTotCs();
                ListBarcodeTran(lvVanOut);
                //log.InsertUserLog("DT-OUT","Update :" + solomonID + " : " + spinUom.getSelectedItem().toString() + " : " + qty + " : " + refNbr);
            } else {
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("WARNING!")
                        .setMessage("You have reached the maximum QTY.")
                        .setPositiveButton("OK",(dialog, which) -> {
                        })
                        .show();
            }
        } else {
            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("WARNING!")
                    .setMessage("Item not listed in this DT")
                    .setPositiveButton("OK",(dialog, which) -> {
                    })
                    .show();
        }
    }

    private void ListBarcodeTran(ListView lvVanOut) {
        List<Map<String, String>> dataList;
        dataList = vanFunc.GetVanList(refNbr, searchDate);
        DisplayTotCs();

        String[] from = {"timeStamp","solomonID","description","barcode","qtyOg","uomOg","qtyOut"};
        int[] to = {R.id.id,R.id.description,R.id.itemDescription,R.id.barcode,R.id.maxQty,R.id.uom,R.id.qty};
        simAd = new SimpleAdapter(getActivity(),dataList,R.layout.dt_barcode_tran_list_template,from,to);
        lvVanOut.setAdapter(simAd);
    }

    private void DisplayTotCs(){
        vanFunc.GetTotCs(refNbr, searchDate, tvTotCs,tvTotCaseShot);
        vanFunc.GetTotPcs(refNbr, searchDate, tvTotPcs,tvTotPcsShot);
    }
}