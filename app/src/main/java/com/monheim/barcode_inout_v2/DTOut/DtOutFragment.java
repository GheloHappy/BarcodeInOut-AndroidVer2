package com.monheim.barcode_inout_v2.DTOut;

import android.app.AlertDialog;
import android.content.Context;
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
import android.widget.Toast;

import com.monheim.barcode_inout_v2.NewBarcode.NewBarcodeFunctions;
import com.monheim.barcode_inout_v2.R;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import MssqlCon.PublicVars;

public class DtOutFragment extends Fragment {
    SimpleAdapter simAd;
    TextView tvTotCs, tvTotCaseShot, tvTotPcs, tvTotPcsShot;
    DtOutFunctions dtOutFunc = new DtOutFunctions();
    NewBarcodeFunctions newBarFunc = new NewBarcodeFunctions();
    String searchDate ="";
    String dt = "";

    PublicVars pubVars = new PublicVars();
    String user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dt_out, container, false);

        EditText searchDtDate = rootView.findViewById(R.id.etSearchDtDate);
        Spinner spinDt = rootView.findViewById(R.id.spinDT);
        ListView lvDTOut = rootView.findViewById(R.id.lvDTOut);
        EditText etDtOutBarcode = rootView.findViewById(R.id.etDtOutBarcode);
        EditText etDtOutQty = rootView.findViewById(R.id.etDtOutQty);
        tvTotCs = rootView.findViewById(R.id.tvTotCase);
        tvTotCaseShot = rootView.findViewById(R.id.tvTotCaseShot);
        tvTotPcs = rootView.findViewById(R.id.tvTotPcs);
        tvTotPcsShot = rootView.findViewById(R.id.tvTotPcsShot);

        etDtOutQty.setEnabled(false);
        user = pubVars.GetUser();
        searchDtDate.addTextChangedListener(new TextWatcher() {
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
                            searchDtDate.setText(String.format("%s-%s", digits.substring(0, 4), digits.substring(4)));
                            searchDtDate.setSelection(searchDtDate.getText().length());
                        }
                    }
                    if (digits.length() >= 7) {
                        if (digits.length() == 7 || digits.charAt(7) != '-') {
                            searchDtDate.setText(String.format("%s-%s-%s", digits.substring(0, 4), digits.substring(4, 6), digits.substring(6)));
                            searchDtDate.setSelection(searchDtDate.getText().length());
                        }
                    }
                    isFormatting = false;
                }

                searchDate = searchDtDate.getText().toString();

                ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.spinner_item, dtOutFunc.GetDt(searchDate));
                adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinDt.setAdapter(adapter);
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
        spinDt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                dt = parent.getItemAtPosition(position).toString();
                ListBarcodeTran(lvDTOut);
                etDtOutBarcode.requestFocus();
                DisplayTotCs();
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

                if (!Objects.equals(solomonID, "NA")) {
                    if(dtOutFunc.GetLastQty(searchDate,dt,solomonID)) {
                        if (dtOutFunc.UpdateDtItem(qty)){
                            DisplayTotCs();
                            ListBarcodeTran(lvDTOut);
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
                } else {
                    new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("WARNING!")
                            .setMessage("Item not found! Item barcode is added in New Barcode tab")
                            .setPositiveButton("OK",(dialog, which) -> {
                            })
                            .show();
                    newBarFunc.CheckUnknownBarcode(barcode, user);
                }

                etDtOutQty.setText("1");
                etDtOutBarcode.setText("");
                etDtOutBarcode.post(() -> etDtOutBarcode.requestFocus()); //focus request

                return true;
            }
            etDtOutBarcode.post(() -> etDtOutBarcode.requestFocus()); //focus request
            return false;
        });
        etDtOutQty.setOnKeyListener((v, keyCode, event) -> {
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_NAVIGATE_NEXT)) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0); // close keyboard
            }
            return false;
        });
        return  rootView;
    }
    private void ListBarcodeTran(ListView lvDTOut) {
        List<Map<String, String>> dataList;
        dataList = dtOutFunc.GetDTList(dt, searchDate);
        DisplayTotCs();

        String[] from = {"timeStamp","solomonID","description","barcode","qty","uom","qtyOut"};
        int[] to = {R.id.id,R.id.description,R.id.itemDescription,R.id.barcode,R.id.maxQty,R.id.uom,R.id.qty};
        simAd = new SimpleAdapter(getActivity(),dataList,R.layout.dt_barcode_tran_list_template,from,to);
        lvDTOut.setAdapter(simAd);
    }

    private void DisplayTotCs(){
        dtOutFunc.GetTotCs(dt, searchDate, tvTotCs,tvTotCaseShot);
        dtOutFunc.GetTotPcs(dt, searchDate, tvTotPcs,tvTotPcsShot);
    }
}