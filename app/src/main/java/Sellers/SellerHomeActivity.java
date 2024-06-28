package Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lavnexonlineshop.Buyers.MainActivity;
import com.example.lavnexonlineshop.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Admins.AdminWhatAppActivity;
import Model.Products;
import ViewHolder.ItemViewHolder;

public class SellerHomeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_home);

        recyclerView = findViewById(R.id.seller_home_recyclerView);
        recyclerView.hasFixedSize();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");
        BottomNavigationView navView = findViewById(R.id.nav_view);


        navView.setSelectedItemId(R.id.navigation_home);
        navView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.navigation_home) {

                    Intent intent = new Intent(SellerHomeActivity.this, SellerHomeActivity.class);
                    startActivity(intent);
                } else if (id == R.id.navigation_logout) {


                    AlertDialog.Builder builder= new AlertDialog.Builder(SellerHomeActivity.this);
                    builder.setMessage("Dear seller are you sure want to Logout from Seller's section???");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final FirebaseAuth mAuth;
                                    mAuth = FirebaseAuth.getInstance();
                                    mAuth.signOut();

                                    Intent intent = new Intent(SellerHomeActivity.this, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);


                                    Toast.makeText(SellerHomeActivity.this, "We sre happy to have you... Welcome back again.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create().show();




                } else if (id == R.id.navigation_add) {
                    Intent intent = new Intent(SellerHomeActivity.this, SellerProductCategoryActivity.class);
                    startActivity(intent);
                } else if (id ==R.id.Admins_WhatApp) {

                    AlertDialog.Builder builder= new AlertDialog.Builder(SellerHomeActivity.this);
                    builder.setMessage("Dear seller are you sure want to join Admins What App Group???");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent= new Intent(SellerHomeActivity.this, AdminWhatAppActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }).setIcon(getDrawable(R.drawable.ic_admin)).setCancelable(false)
                            .create().show();

                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options= new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(unverifiedProductsRef.orderByChild("sid").equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid()), Products.class)
                .build();


        FirebaseRecyclerAdapter<Products, ItemViewHolder> adapter= new FirebaseRecyclerAdapter<Products, ItemViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemViewHolder holder, int position, @NonNull Products model) {
                holder.txtproductionName.setText(model.getPname());
                holder.txtproductionDescription.setText(model.getDescription());
                holder.txtproductPrice.setText("Price = " +model.getPrice() + "KSH");
                holder.txtProductsStatus.setText("State = " + model.getProductState());


                final  Products itemClick = model;

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final  String productID = model.getPid();

                        CharSequence options[]= new CharSequence[]{
                                "Yes",
                                "No"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(SellerHomeActivity.this);
                        builder.setTitle("Do you want to Delete this product.Are you sure?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i ==0){
                                    deleteProducts(productID);
                                }if (i==1){

                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.seller_item_view,parent,false);
                ItemViewHolder holder= new ItemViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void deleteProducts(String productID) {
        unverifiedProductsRef.child(productID)
                .removeValue()

                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(SellerHomeActivity.this, "Item have been deleted successfully...", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
