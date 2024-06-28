package Admins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lavnexonlineshop.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import Model.Products;
import ViewHolder.ProductViewHolder;

public class CheckNewProductActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference unverifiedProductsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_new_product);
        recyclerView=findViewById(R.id.admin_products_check_list);
        recyclerView.hasFixedSize();
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        unverifiedProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Products> options= new FirebaseRecyclerOptions.Builder<Products>()
                .setQuery(unverifiedProductsRef.orderByChild("productState"), Products.class).build();

        FirebaseRecyclerAdapter<Products, ProductViewHolder> adapter= new FirebaseRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Products model) {
                holder.txtProductName.setText(model.getPname());
                holder.txtProductDescription.setText(model.getDescription());
                holder.txtProductPrice.setText("Price = " +model.getPrice() + "KSH");

                Picasso.get().load(model.getImage()).into(holder.imageView);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final  String productID = model.getPid();

                        CharSequence options[]= new CharSequence[]{
                                "Yes",
                                "No"
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(CheckNewProductActivity.this);
                        builder.setTitle("Do you want to approved this product.Are you sure?");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (i ==0){
                                    changeProductsState(productID);
                                }if (i==1){

                                    Toast.makeText(CheckNewProductActivity.this, "Product restored successfully", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        builder.show();
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.products_item_layout,parent,false);
                ProductViewHolder holder= new ProductViewHolder(view);
                return  holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    private void changeProductsState(String productID) {
        unverifiedProductsRef.child(productID)
                .child("ProductState")
                .setValue("Approved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(CheckNewProductActivity.this, "Item have been approved successfully and ready for sale for clients", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
