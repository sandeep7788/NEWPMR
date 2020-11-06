package com.example.myrecharge.Custom_;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myrecharge.Adapter.AllBankAdapter;
import com.example.myrecharge.Helper.Constances;
import com.example.myrecharge.Helper.Local_data;
import com.example.myrecharge.Models.BankListModel;
import com.example.myrecharge.R;
import java.util.ArrayList;

public class CustomDialogAllBankList extends Dialog implements AdapterView.OnItemClickListener {

    public Activity c;
    public ImageView dialog_cancel_btn;
    public ArrayList<BankListModel> TitleName = new ArrayList<>();
    public ArrayList<BankListModel> array_sort=new ArrayList<>();
    int textlength=0;
    public Context context;
    TextView textView;
    ListView alertdialog_Listview;
    EditText alertdialog_edittext;
    String mOperatorcode;
    Boolean click_status=false;
    OnMyDialogResult mDialogResult;
    String BankID=" ";

    public CustomDialogAllBankList(Context context, final ArrayList<BankListModel> TitleName, TextView textview){
        super(context,R.style.FullWidth_Dialog);
        this.context=context;
        this.TitleName=TitleName;
        this.textView=textview;
    }

    public CustomDialogAllBankList(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(WindowManager.LayoutParams.FILL_PARENT);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_layout);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().setAttributes(layoutParams);
        alertdialog_Listview =  findViewById(R.id.alertdialog_Listview);
        alertdialog_edittext = findViewById(R.id.alertdialog_edittext);
        dialog_cancel_btn = findViewById(R.id.back);
        dialog_cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        setview();
    }

    public void setview() {
        AllBankAdapter arrayAdapter=new AllBankAdapter((Activity) context, TitleName);
        alertdialog_Listview.setAdapter(arrayAdapter);
        alertdialog_Listview.setOnItemClickListener(this);
        alertdialog_edittext.addTextChangedListener(new TextWatcher()
        {
            public void afterTextChanged(Editable s){
            }
            public void beforeTextChanged(CharSequence s,int start, int count, int after){
            }
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                alertdialog_edittext.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                textlength = alertdialog_edittext.getText().length();

                array_sort.clear();

                for (int i = 0; i < TitleName.size(); i++)
                {
                    if (textlength <= TitleName.get(i).getBankerMasterName().length())
                    {
                        if(TitleName.get(i).getBankerMasterName().toLowerCase().contains(alertdialog_edittext.getText().toString().toLowerCase().trim()))

                        {

                            array_sort.add(TitleName.get(i)); } }
                }
                alertdialog_Listview.setAdapter(new AllBankAdapter((Activity) context,array_sort));

                click_status=true;
            }

        });
    }
    private class OKListener implements android.view.View.OnClickListener {
        @Override
        public void onClick(View v) {
            if( mDialogResult != null ){
                mDialogResult.finish(String.valueOf(BankID));
            }
            dismiss();
        }
    }

    public void setDialogResult(OnMyDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnMyDialogResult{
        void finish(String result);
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(click_status) {
            Local_data pref = new Local_data(context);
            pref.writeStringPreference(Constances.PREF_operator_code,array_sort.get(i).getBankerMasterName().toString());
            textView.setText(" "+array_sort.get(i).getBankerMasterName().toString());
            textView.setTextColor(Color.BLACK);
            BankID= String.valueOf(array_sort.get(i).getBankerMasterID());
            mDialogResult.finish(String.valueOf(BankID));
        } else {
            Local_data pref = new Local_data(context);
            pref.writeStringPreference(Constances.PREF_operator_code,TitleName.get(i).getBankerMasterName().toString());
            textView.setText(" "+TitleName.get(i).getBankerMasterName().toString());
            textView.setTextColor(Color.BLACK);
            Log.e(">>itemclick"," "+TitleName.get(i).getBankerMasterName().toString());
            BankID= String.valueOf(TitleName.get(i).getBankerMasterID());
            mDialogResult.finish(String.valueOf(BankID));
        }
        dismiss();
    }
}