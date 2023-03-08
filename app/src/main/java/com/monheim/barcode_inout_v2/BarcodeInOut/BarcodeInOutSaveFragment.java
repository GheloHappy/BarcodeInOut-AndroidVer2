package com.monheim.barcode_inout_v2.BarcodeInOut;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.monheim.barcode_inout_v2.Home.HomeFragment;
import com.monheim.barcode_inout_v2.MainActivity;
import com.monheim.barcode_inout_v2.R;

import java.util.List;
import java.util.Map;

import MssqlCon.Logs;
import MssqlCon.PublicVars;

public class BarcodeInOutSaveFragment extends Fragment {
    SimpleAdapter simAd;
    BarcodeInOutFunctions barFunc = new BarcodeInOutFunctions();
    Logs log = new Logs();
    PublicVars pubVars = new PublicVars();
    String user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_barcode_in_out_save, container, false);

        EditText etRefNbr = rootView.findViewById(R.id.etRefNbr);
        EditText etRemarks = rootView.findViewById(R.id.etRemarks);
        Button btnSave = rootView.findViewById(R.id.btnSave);
        TextView tvTotCs = rootView.findViewById(R.id.tvTotCase);
        TextView tvTotPcs= rootView.findViewById(R.id.tvTotPcs);
        ListView lvTempBarcodeTran = rootView.findViewById(R.id.lvBarcodeTrans);

        user = pubVars.GetUser();

        ListTempBarcodeTran(lvTempBarcodeTran);
        barFunc.GetToTQtyCs(tvTotCs, user);
        barFunc.GetToTQtyPcs(tvTotPcs, user);

        lvTempBarcodeTran.setOnItemLongClickListener((parent, view, position, id) -> {
            TextView tvID = view.findViewById(R.id.id);
            TextView tvBarcode = view.findViewById(R.id.barcode);
            int item = Integer.parseInt(tvID.getText().toString());

            new AlertDialog.Builder(getActivity())
                   .setIcon(android.R.drawable.ic_delete)
                   .setTitle("Are you sure ?")
                   .setMessage("Do you want to delete this item")
                   .setPositiveButton("Yes",(dialog, which) -> {
                       barFunc.DeleteTempBarcodeItem(item, user);
                       barFunc.GetToTQtyCs(tvTotCs,user);
                       barFunc.GetToTQtyPcs(tvTotPcs,user);
                       ListTempBarcodeTran(lvTempBarcodeTran);
                       log.InsertUserLog("Delete Barcode upon Saving Ref",tvBarcode.getText().toString()); //logUser
                   })
                   .setNegativeButton("No", null)
                   .show();
            return true;
        });

        btnSave.setOnClickListener(v -> {
            String refNbr = etRefNbr.getText().toString();
            String remarks = etRemarks.getText().toString();

            if( refNbr.matches("")) {
                Toast.makeText(getActivity(), "Please input Reference number first.", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Please confirm reference number - "+ refNbr);
                builder.setCancelable(true);

                builder.setPositiveButton("Yes", (dialog, which) -> {
                    if (!barFunc.InsertRefNbr(refNbr,remarks)) {
                        Toast.makeText(getActivity(),"Insert Fail please contact IT!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(),"Reference saved successfully.", Toast.LENGTH_SHORT).show();
                        barFunc.ClearTempTrans(user);
                        etRefNbr.setText("");

                        log.InsertUserLog("BarcodeInOut",refNbr); //logUser
                        EnableMainNavItem(); //enable Navigation barcode IN and Out

                        HomeFragment homeFrag = new HomeFragment();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container,homeFrag).commit();
                    }
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                    dialog.cancel();
                });

                AlertDialog alertDia = builder.create();
                alertDia.show();
            }
        });

        return rootView;
    }

    private void EnableMainNavItem(){
        PublicVars.GetNav().getMenu().findItem(R.id.barcodeIn).setEnabled(true);
        PublicVars.GetNav().getMenu().findItem(R.id.barcodeOut).setEnabled(true);
    }

    private void ListTempBarcodeTran(ListView lvTempBarcodeTran) {
        List<Map<String, String>> dataList;
        BarcodeInOutFunctions barFUnc = new BarcodeInOutFunctions();
        dataList = barFUnc.GetTempBarList(user);

//        String[] from = {"id","barcode","solomonID","description","uom","qty"};
//        int[] to = {R.id.id,R.id.barcode,R.id.tranType,R.id.description,R.id.uom,R.id.qty};
        String[] from = {"barcode","solomonID","description","uom","qty"};
        int[] to = {R.id.barcode,R.id.tranType,R.id.description,R.id.uom,R.id.qty};
        simAd = new SimpleAdapter(getActivity(),dataList,R.layout.temp_barcode_tran_list_template,from,to);
        lvTempBarcodeTran.setAdapter(simAd);
    }
}