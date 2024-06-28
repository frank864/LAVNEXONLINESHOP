package com.example.lavnexonlineshop.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lavnexonlineshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Prevalent.Prevalent;

public class ConfirmFinalOrderActivity extends AppCompatActivity {
    private EditText nameEditText,phoneEditText,addressEditText,cityEditText;
    private Button confirmOrderBtn;
    private String totalAmount ="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_final_order);
        confirmOrderBtn= findViewById(R.id.confirm_final_order_btn);
        nameEditText=findViewById(R.id.shippment_name);
        phoneEditText=findViewById(R.id.shippment_phone_number);
        addressEditText=findViewById(R.id.shippment_address);
        cityEditText=findViewById(R.id.shippment_city);

        totalAmount= getIntent().getStringExtra("Total Price");

        Toast.makeText(this, "Total Amount = Ksh " + totalAmount, Toast.LENGTH_SHORT).show();

        confirmOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check();
            }
        });
    }

    private void Check(){
        if (TextUtils.isEmpty(nameEditText.getText().toString())){
            Toast.makeText(this, "Please provide your full name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phoneEditText.getText().toString())) {
            Toast.makeText(this, "Please fill your phone number", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(addressEditText.getText().toString())) {
            Toast.makeText(this, "Please provide your home address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(cityEditText.getText().toString())) {
            Toast.makeText(this, "Please your city name is required", Toast.LENGTH_SHORT).show();
        }else {
            confirmOrder();
        }
    }

    private void confirmOrder() {
        final String saveCurrentTime,saveCurrentDate;


        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM ,dd,yyyy");
        saveCurrentDate= currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime= currentDate.format(calForDate.getTime());

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders")
                .child(Prevalent.currentOnlineUser.getPhone());

        HashMap<String, Object> orderMap = new HashMap<>();

        orderMap.put("toalAmount",totalAmount);
        orderMap.put("name",nameEditText.getText().toString());
        orderMap.put("phone",phoneEditText.getText().toString());
        orderMap.put("date",saveCurrentDate);
        orderMap.put("time",saveCurrentTime);
        orderMap.put("city",cityEditText.getText().toString());
        orderMap.put("address",addressEditText.getText().toString());
        orderMap.put("state"," not Shipped");

        orderRef.updateChildren(orderMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    FirebaseDatabase.getInstance().getReference().child("Cart List")
                            .child("User View")
                            .child(Prevalent.currentOnlineUser.getPhone())
                            .removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(ConfirmFinalOrderActivity.this, "Your final order has been placed successfully", Toast.LENGTH_SHORT).show();
                                        Intent intent= new Intent(ConfirmFinalOrderActivity.this, HomeActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });


    }
}