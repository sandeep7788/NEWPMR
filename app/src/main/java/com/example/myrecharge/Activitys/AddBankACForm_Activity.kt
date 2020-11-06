package com.example.myrecharge.Activitys

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.DataBindingUtil
import cn.pedant.SweetAlert.SweetAlertDialog
import com.bumptech.glide.Glide
import com.example.myrecharge.Activitys.Manus.MT_UserAddedBankAC
import com.example.myrecharge.Custom_.CustomDialog
import com.example.myrecharge.Custom_.CustomDialogAllBankList
import com.example.myrecharge.Helper.ApiInterface
import com.example.myrecharge.Helper.Constances
import com.example.myrecharge.Helper.Local_data
import com.example.myrecharge.Helper.RetrofitManager
import com.example.myrecharge.Models.BankListModel
import com.example.myrecharge.R
import com.example.myrecharge.databinding.AddBankAcActivityBinding
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddBankACForm_Activity : AppCompatActivity() {
    lateinit var mainBinding : AddBankAcActivityBinding
    var editTextStatus=true
    var pref= Local_data(this@AddBankACForm_Activity)
    var pDialog: SweetAlertDialog?=null
    var bank_id=""
    var ID=""
    var updateStatus=false
    var banklist:ArrayList<BankListModel> = java.util.ArrayList()
    var  TAG=">>AddBankACForm_Activity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        mainBinding =
            DataBindingUtil.setContentView(this,R.layout.add_bank_ac_activity)
        pref.setMyappContext(this@AddBankACForm_Activity)
        pDialog=SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)
        pDialog!!.progressHelper.barColor = Color.parseColor("#132752")
        pDialog!!.titleText = "Loading ..."
        pDialog!!.setCancelable(false)

        mainBinding.edtPhoneNum.setInputType(InputType.TYPE_CLASS_NUMBER)
        mainBinding.edtIfsc.setInputType(InputType.TYPE_CLASS_NUMBER)
        mainBinding.edtConfirmAcNum.setInputType(InputType.TYPE_CLASS_NUMBER)
        mainBinding.edtAcNum.setInputType(InputType.TYPE_CLASS_NUMBER)

        set_data()
        Onclick()



    }
    fun Onclick() {
        mainBinding.edtIcon.setOnClickListener {
            if(editTextStatus) {
                enableAll()
                editTextStatus=false
                mainBinding.edtIcon.setColorFilter(Color.parseColor("#0b892e"))
                mainBinding.edtPhoneNum.setInputType(InputType.TYPE_CLASS_NUMBER)
                mainBinding.edtIfsc.setInputType(InputType.TYPE_CLASS_NUMBER)
                mainBinding.edtConfirmAcNum.setInputType(InputType.TYPE_CLASS_NUMBER)
                mainBinding.edtAcNum.setInputType(InputType.TYPE_CLASS_NUMBER)
            } else {
                disableAll()
                editTextStatus=true
                mainBinding.edtIcon.setColorFilter(Color.parseColor("#ed1b24"))
            }
        }
        mainBinding.toolbar.back.setOnClickListener {
            finish()
        }

        mainBinding.btnSubmit.setOnClickListener {
            if(mainBinding.edtAcNum.text.toString().isEmpty()) {
                Toast.makeText(this@AddBankACForm_Activity,"Please Enter Account Number.", Toast.LENGTH_LONG).show()
            } else if(mainBinding.edtConfirmAcNum.text.toString().isEmpty()) {
                Toast.makeText(this@AddBankACForm_Activity,"Please Enter Conform Account Number.", Toast.LENGTH_LONG).show()
            } else if(!mainBinding.edtAcNum.text.toString().equals(mainBinding.edtConfirmAcNum.text.toString())) {
                Toast.makeText(this@AddBankACForm_Activity,"Entered Account no. and Conform ac no. Not same.", Toast.LENGTH_LONG).show()
            } else if(mainBinding.edtIfsc.text.toString().isEmpty()) {
                Toast.makeText(this@AddBankACForm_Activity,"Please Enter IFSC Code.", Toast.LENGTH_LONG).show()
            } else if(mainBinding.edtAcHolderName.text.toString().isEmpty()) {
                Toast.makeText(this@AddBankACForm_Activity,"Please Enter Account Holder Name.", Toast.LENGTH_LONG).show()
            } else if(mainBinding.edtPhoneNum.text.toString().isEmpty()) {
                Toast.makeText(this@AddBankACForm_Activity,"Please Enter Account Phone no..", Toast.LENGTH_LONG).show()
            } else if(mainBinding.edtNickName.text.toString().isEmpty()) {
                Toast.makeText(this@AddBankACForm_Activity,"Please Enter Nick Name", Toast.LENGTH_LONG).show()
            }  else {
                if (updateStatus) {
                    updateProcess()
                } else {
                    addDetails()
                }

            }
        }
        mainBinding.txtBankname.setOnClickListener {
            getBankList()
        }
    }
    fun getBankList()
    {
        pDialog?.show()

        var apiInterface: ApiInterface = RetrofitManager(this@AddBankACForm_Activity).instance!!.create(
            ApiInterface::class.java)

        apiInterface.getAllbanknamedetails().
        enqueue(object : Callback<JsonObject>
        {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(this@AddBankACForm_Activity,t.message.toString()+" ", Toast.LENGTH_LONG).show()
                pDialog?.dismiss()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                banklist.clear()
                Log.d(TAG, "onResponse: "+response.toString())
                pDialog?.dismiss()
                if(response.isSuccessful) {

                    Log.d(TAG, "onResponse: "+response.body().toString())
                    var mJsonObject = JSONObject(response.body().toString())
//                    Toast.makeText(this@Prepaid_Activity,mJsonObject.getString("Message")+" ",Toast.LENGTH_LONG).show()

                    if(mJsonObject.has("Data") && !mJsonObject.isNull("Data") ) {

                        var mJsonArray = mJsonObject.getJSONArray("Data")

                        if (mJsonObject.getString("Error").toLowerCase()
                                .equals("false") && mJsonArray != null
                        ) {


                            for (i in 0 until mJsonArray.length()) {

                                var JsonObjectData = mJsonArray.getJSONObject(i)
                                var mRechargereportModel: BankListModel = BankListModel()
                                mRechargereportModel.bankerMasterID =
                                    JsonObjectData.getString("BankerMasterID").toInt()
                                mRechargereportModel.bankerMasterName =
                                    JsonObjectData.getString("BankerMasterName")
                                mRechargereportModel.bankImage =
                                    JsonObjectData.getString("BankImage")

                                banklist.add(mRechargereportModel)
                                Log.d("!!" + TAG, "data " + i.toString() + " " + mRechargereportModel.bankerMasterName)

                            }
                            var operatorDialog : CustomDialogAllBankList = CustomDialogAllBankList(
                                    this@AddBankACForm_Activity,
                                    banklist,
                                    mainBinding.txtBankname
                                )
                            operatorDialog.getWindow()!!.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                            operatorDialog.setDialogResult {
                                Log.e("@@data1",it.toString())
                            }
                            operatorDialog.show()

                        } else {

                        }
                    } else {

                    }
                } else {

                }
            }
        })
    }

    fun set_data() {
        Log.d(TAG, "set_data: "+intent.getStringExtra("bank_img"))
        if(intent!=null && intent.getStringExtra("bank_img")!=null&& !intent.getStringExtra("bank_img").equals("")) {
            Log.d(TAG, "set_data: "+intent.getStringExtra("bank_img"))
            mainBinding.txtBankname.setText(intent.getStringExtra("bank_name"))
            bank_id= intent.getStringExtra("bank_id").toString()

            Glide.with(this)
                .load(intent.getStringExtra("bank_img").toString())
                .thumbnail(0.5f)
                .placeholder(R.drawable.prepaid)
                .into(mainBinding.ingBank)
            enableAll()
        } else {
            disableAll()
            fillDatails_when_EDIT()
        }
    }

    fun disableAll() {
        disableEditText(mainBinding.edtAcNum)
        disableEditText(mainBinding.edtConfirmAcNum)
        disableEditText(mainBinding.edtIfsc)
        disableEditText(mainBinding.edtAcHolderName)
        disableEditText(mainBinding.edtPhoneNum)
        disableEditText(mainBinding.edtNickName)
    }
    fun enableAll() {
        enableEditText(mainBinding.edtAcNum)
        enableEditText(mainBinding.edtConfirmAcNum)
        enableEditText(mainBinding.edtIfsc)
        enableEditText(mainBinding.edtAcHolderName)
        enableEditText(mainBinding.edtPhoneNum)
        enableEditText(mainBinding.edtNickName)
    }
    private fun disableEditText(editText: EditText) {
        editText.isFocusable = false
        editText.isEnabled = false
        editText.isCursorVisible = false
        editText.keyListener = null
//        editText.setBackgroundColor(Color.parseColor("#999999"))
    }
    private fun enableEditText(editText: EditText) {
        editText.isFocusable = true
        editText.isEnabled = true
        editText.isCursorVisible = true
        editText.setFocusableInTouchMode(true)
        editText.setKeyListener(AppCompatEditText(getApplicationContext()).getKeyListener())
//        editText.setBackgroundColor(Color.WHITE)
    }
    fun addDetails() {
        pDialog!!.show()
        var apiInterface: ApiInterface = RetrofitManager(this).instance!!.create(ApiInterface::class.java)

        apiInterface.AddMemberBankAccount(pref.ReadStringPreferences(Constances.PREF_Msrno),
            bank_id,
        pref.ReadStringPreferences(Constances.PREF_Mobile),
        mainBinding.edtAcNum.text.toString(),
            mainBinding.edtAcHolderName.text.toString(),
            mainBinding.edtIfsc.text.toString()
        ).enqueue(object : retrofit2.Callback<JsonObject>
        {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(this@AddBankACForm_Activity," "+t.message.toString(), Toast.LENGTH_LONG).show()
                pDialog?.dismiss()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                pDialog?.dismiss()
                if(response.isSuccessful) {
                    Log.d("@@addbank", "onResponse: " + response.body().toString())
                    var mJsonObject = JSONObject(response.body().toString())
                    var operatorDialog: CustomDialog

                    try {
                        if (mJsonObject.getString("Error").toLowerCase().equals("false")) {
                            var mJsonArray = mJsonObject.getJSONArray("Data")
                            mJsonArray.getJSONObject(0).getString("ID")
                            showDialog1(mJsonObject.getString("Message").toString(),"2")

                        } else {
                            showDialog1(mJsonObject!!.getString("Message"),"3")
                        }
                    }catch (e:Exception) {
                        Toast.makeText(this@AddBankACForm_Activity," "+e.message.toString(), Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@AddBankACForm_Activity,"Bad Response.", Toast.LENGTH_LONG).show()
                }
            }
        })

    }

    fun updateProcess() {
        pDialog!!.show()
        var apiInterface: ApiInterface = RetrofitManager(this).instance!!.create(ApiInterface::class.java)

        apiInterface.EditMemberBankAccount(
            pref.ReadStringPreferences(Constances.PREF_Mobile),
            pref.ReadStringPreferences(Constances.PREF_Login_password),
            pref.ReadStringPreferences(Constances.PREF_Msrno),
            bank_id,
            ID,
            pref.ReadStringPreferences(Constances.PREF_Mobile),
            mainBinding.edtAcNum.text.toString(),
            mainBinding.edtAcHolderName.text.toString(),
            mainBinding.edtIfsc.text.toString()
        ).enqueue(object : retrofit2.Callback<JsonObject>
        {
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Toast.makeText(this@AddBankACForm_Activity," "+t.message.toString(), Toast.LENGTH_LONG).show()
                pDialog?.dismiss()
            }

            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                pDialog?.dismiss()
                if(response.isSuccessful) {
                    Log.d("@@addbank", "onResponse: " + response.body().toString())
                    var mJsonObject = JSONObject(response.body().toString())
                    var operatorDialog: CustomDialog

                    try {
                        if (mJsonObject.getString("Error").toLowerCase().equals("false")) {
                            var mJsonArray = mJsonObject.getJSONArray("Data")
                            mJsonArray.getJSONObject(0).getString("ID")
                            showDialog1(mJsonObject.getString("Message").toString(),"2")

                        } else {
                            showDialog1(mJsonObject!!.getString("Message"),"3")
                        }
                    }catch (e:Exception) {
                        Toast.makeText(this@AddBankACForm_Activity," "+e.message.toString(), Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this@AddBankACForm_Activity,"Bad Response.", Toast.LENGTH_LONG).show()
                }
            }
        })

    }

    fun showDialog1(title:String,type:String){
        SweetAlertDialog(this, type.toInt())
            .setTitleText(title)
//            .setContentText(title)
            .setConfirmText("OK")
            .setConfirmClickListener { sDialog ->
                sDialog.dismiss()
                var mIntent= Intent(this,MT_UserAddedBankAC::class.java)
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(mIntent)
            }
            .show()
//        cln()
    }

    fun cln() {
        mainBinding.edtAcNum.setText("")
        mainBinding.edtConfirmAcNum.setText("")
        mainBinding.edtIfsc.setText("")
        mainBinding.edtAcHolderName.setText("")
        mainBinding.edtPhoneNum.setText("")
        mainBinding.edtAcNum.setText("")
        mainBinding.edtNickName.setText("")
    }
    fun fillDatails_when_EDIT() {
        if (intent!=null) {
            ID= intent.getStringExtra("id").toString()
            bank_id= intent.getStringExtra("bankID").toString()
            mainBinding.txtBankname.setText(intent.getStringExtra("bankName"))
            mainBinding.edtAcNum.setText(intent.getStringExtra("accountnumber"))
            mainBinding.edtConfirmAcNum.setText(intent.getStringExtra("accountnumber"))
            mainBinding.edtIfsc.setText(intent.getStringExtra("ifsccode"))
            mainBinding.edtAcHolderName.setText(intent.getStringExtra("customerName"))
            mainBinding.edtPhoneNum.setText(intent.getStringExtra("customernumber"))
//            mainBinding.edtNickName.setText(intent.getStringExtra("bankImage"))

            Glide.with(this)
                .load(intent.getStringExtra("bankImage"))
                .thumbnail(0.5f)
                .into(mainBinding.ingBank)
            updateStatus=true
        }
        else {

        }
    }
}