package Admins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lavnexonlineshop.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Model.Cart;
import ViewHolder.CartViewHolder;

public class AdminUsersProductsActivity extends AppCompatActivity {
    private RecyclerView productsList;

    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference cartListRef;
    private String userID ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users_products);

        userID = getIntent().getStringExtra("uid");

        productsList = findViewById(R.id.products_list);
        productsList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        productsList.setLayoutManager(layoutManager);



        cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List").child("Admin View").child(userID).child("Products");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Cart> options= new FirebaseRecyclerOptions.Builder<Cart>()
                .setQuery(cartListRef, Cart.class)
                .build();

        FirebaseRecyclerAdapter<Cart, CartViewHolder> adapter = new FirebaseRecyclerAdapter<Cart, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, int position, @NonNull Cart model) {
                holder.txtProductQuantity.setText("Quantity = " +model.getQuantity());
                holder.txtProductName.setText("Product Name = " +   model.getPname());
                holder.txtProductPrice.setText("price = " +  model.getPrice()+ "Ksh");
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout,parent,false);
                CartViewHolder holder= new CartViewHolder(view);
                return holder;
            }
        };
        productsList.setAdapter(adapter);
        adapter.startListening();
    }
}