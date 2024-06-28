package com.example.lavnexonlineshop.Buyers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lavnexonlineshop.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import Admins.AdminMaintainProductActivity;
import Model.Products;
import Prevalent.Prevalent;
import ViewHolder.ProductViewHolder;
import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private DatabaseReference ProductsRef;
    private RecyclerView recyclerView ;
    RecyclerView.LayoutManager layoutManager;
    private String type ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            type = getIntent().getExtras().get("Admin").toString();
        }

        Paper.init(this);


        layoutManager = new LinearLayoutManager(this);


        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        initViews();

        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawwer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {


            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();


                if (id == R.id.nav_cart) {
                    if (!type.equals("Admin")) {
                        Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                        startActivity(intent);
                    }


                } else if (id == R.id.nav_search) {
                    if (!type.equals("Admin")) {
                        Intent intent = new Intent(HomeActivity.this, SearchProductActivity.class);
                        startActivity(intent);
                    }else {
                        Intent intent = new Intent(HomeActivity.this, SearchProductActivity.class);
                        startActivity(intent);
                    }


                } else if (id == R.id.nav_setting) {
                    if (!type.equals("Admin")) {
                        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }



                } else if (id == R.id.nav_logout) {
                    if (!type.equals("Admin")) {

                        new android.app.AlertDialog.Builder(HomeActivity.this)
                                .setTitle("LOGOUT")
                                .setIcon(R.drawable.logout)
                                .setMessage("Are you sure you want to logout from our App???...")
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Paper.book().destroy();
                                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        Toast.makeText(HomeActivity.this, "See you soon!!!", Toast.LENGTH_SHORT).show();
                                        startActivity(intent);
                                        finish();
                                    }
                                }).create().show();
                    }


                } else if (id == R.id.Users_Admins_WhatApp) {


                    AlertDialog.Builder builder= new AlertDialog.Builder(HomeActivity.this);
                    builder.setMessage("Dear Users are you sure want to join Other Users from our App What App Group???");
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent= new Intent(HomeActivity.this,UserWhatActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                            }).setIcon(getDrawable(R.drawable.ic_admin)).setCancelable(false)
                            .create().show();
                }
                return false;
            }

        });





        View headerView = navigationView.getHeaderView(0);
        TextView UserNameTextView = headerView.findViewById(R.id.user_profile_name);
        CircleImageView profileImageView = headerView.findViewById(R.id.user_profile_image);


        if (!type.equals("Admin") && Prevalent.currentOnlineUser != null) {
            UserNameTextView.setText(Prevalent.currentOnlineUser.getName());
            Picasso.get().load(Prevalent.currentOnlineUser.getImage()).placeholder(R.drawable.profile).into(profileImageView);
        }
    }



    @Override
    protected void onStart() {

        super.onStart();
        FirebaseRecyclerOptions<Products> options = new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(ProductsRef.orderByChild("ProductState").equalTo("Approved"),Products.class)
                .build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter= new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                holder.txtProductName.setText(model.getPname());
                holder.txtProductDescription.setText(model.getDescription());
                holder.txtProductPrice.setText( "Price = " + model.getPrice() + "KSH");

                Picasso.get().load(model.getImage()).into(holder.imageView);



                holder.itemView.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View v) {


                        if (type.equals("Admin")){
                            Intent intent = new Intent(HomeActivity.this, AdminMaintainProductActivity.class);
                            intent.putExtra("pid",model.getPid());
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(HomeActivity.this, ProductDetailsActivity.class);
                            intent.putExtra("pid",model.getPid());
                            startActivity(intent);
                        }
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.products_item_layout,parent,false);
                ProductViewHolder holder= new ProductViewHolder(view);
                return holder;
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void initViews() {

        toolbar= findViewById(R.id.toolbar);
        navigationView= findViewById(R.id.navigationView);
        drawer= findViewById(R.id.drawer);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

}