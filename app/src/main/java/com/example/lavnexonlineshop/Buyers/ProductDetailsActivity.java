package com.example.lavnexonlineshop.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lavnexonlineshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Model.Products;
import Prevalent.Prevalent;

public class ProductDetailsActivity extends AppCompatActivity {

    private Button addToCart;
    private TextView tvValue;
    private int value = 1; // Set the initial value to 1


    private TextView  productDescription,productName,productPrice;

    private ImageView productImage;
    int totalQuantity =1;



    private String productID= "",state= "Normal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        tvValue = findViewById(R.id.tvValue);
        updateValue();

        //numberButton=findViewById(R.id.tvValue);

        productImage=findViewById(R.id.product_image_details);
        productDescription=findViewById(R.id.product_description_details);
        productName=findViewById(R.id.product_name_details);
        productPrice=findViewById(R.id.product_price_details);
        productID= getIntent().getStringExtra("pid");
        addToCart=findViewById(R.id.pd_add_to_cart_button);
        getProductDeatails(productID);



        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addingToCartList();
                if (state.equals("Order Placed")  || state.equals("Order Shipped")){
                    Toast.makeText(ProductDetailsActivity.this, "You can purchase more product ones your order is confirmed and shipped", Toast.LENGTH_SHORT).show();
                }else {
                    addingToCartList();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOrderState();
    }

    private void addingToCartList() {

        String saveCurrentDate,saveCurrentTime;
        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM ,dd,yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentDate.format(calForDate.getTime());

        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

        final HashMap<String, Object> cartMap = new HashMap<>();
        cartMap.put("pid", productID);
        cartMap.put("pname", productName.getText().toString());
        cartMap.put("date", saveCurrentDate);
        cartMap.put("price", productPrice.getText().toString());
        cartMap.put("time", saveCurrentTime);
        cartMap.put("quantity", value);
        cartMap.put("discount", "");

        cartListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID).updateChildren(cartMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            cartListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID).updateChildren(cartMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(ProductDetailsActivity.this, "added to cartlist successfully", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(ProductDetailsActivity.this, HomeActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void getProductDeatails(String productID) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productsRef.child(productID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (datasnapshot.exists()){
                    Products products =datasnapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productPrice.setText("Price ="  + products.getPrice() +"KSH");
                    productDescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkOrderState() {
        DatabaseReference orderRef;

        orderRef = FirebaseDatabase.getInstance().getReference().child("Order").child(Prevalent.currentOnlineUser.getPhone());
        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (datasnapshot.exists()) {
                    String shippingState = datasnapshot.child("state").getValue().toString();

                    if (shippingState.equals("shipped")) {

                        state = "Order Shipped";
                    } else if (shippingState.equals("not shipped")) {
                        state = "Order placed";
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void increaseValue(View view) {
        value++;
        updateValue();
       // updateTotalPrice();
    }

    public void decreaseValue(View view) {
        if (value > 1) {
            value--;
            updateValue();
          //  updateTotalPrice();
        }
    }

    private void updateValue() {
        tvValue.setText(String.valueOf(value));
    }

}