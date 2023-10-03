package com.monheim.barcode_inout_v2.ViewInventory;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.monheim.barcode_inout_v2.Home.HomeFunctions;
import com.monheim.barcode_inout_v2.R;

import java.util.List;
import java.util.Map;

import MssqlCon.PublicVars;

public class ViewInventoryFragment extends Fragment {

    SimpleAdapter simAd;
    ViewInventoryFunctions viewInvtFunc = new ViewInventoryFunctions();

    PublicVars pubVars = new PublicVars();

    String user ="";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_inventory, container, false);

        EditText etSearchRefNbr = rootView.findViewById(R.id.etViewInvtSearchRefNbr);
        ListView lvBarcodeTrans = rootView.findViewById(R.id.lvViewInvtBarcodeTrans);
        Button btnSearch = rootView.findViewById(R.id.btnViewInvtSearch);
        TextView tvTotCs = rootView.findViewById(R.id.tvViewInvtTotCS);
        TextView tvTotPcs = rootView.findViewById(R.id.tvViewInvtTotPcs);

        user = pubVars.GetUser();


        etSearchRefNbr.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                btnSearch.callOnClick();
                return true;
            }
            return false;
        });

        btnSearch.setOnClickListener(v -> {
            String refNbr = etSearchRefNbr.getText().toString();
            if(viewInvtFunc.GetTotCs(tvTotCs, tvTotPcs, refNbr, user) == true) {
                ListInvtTran(lvBarcodeTrans, refNbr);
            } else {
                Toast.makeText(getActivity(), "Reference number not found.", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private void ListInvtTran(ListView lvBarcodeTrans, String refNbr) {
        List<Map<String, String>> dataList;
        dataList = viewInvtFunc.GetInvtList(refNbr, user);

        String[] from = {"barcode","solomonID","description","uom","qty"};
        int[] to = {R.id.barcode,R.id.tranType,R.id.description,R.id.uom,R.id.qty};
        simAd = new SimpleAdapter(getActivity(),dataList,R.layout.temp_barcode_tran_list_template,from,to);
        lvBarcodeTrans.setAdapter(simAd);
    }
}