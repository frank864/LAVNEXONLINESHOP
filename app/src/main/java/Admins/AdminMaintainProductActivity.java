package Admins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lavnexonlineshop.Buyers.HomeActivity;
import com.example.lavnexonlineshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class AdminMaintainProductActivity extends AppCompatActivity {

    private Button ApplyChangesBtn,deleteBtn;
    private EditText name,price,description;
    private ImageView imageView;
    private String productID = "";
    private DatabaseReference productsRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_maintain_product);
        productID = getIntent().getStringExtra("pid");

        productsRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);




        deleteBtn= findViewById(R.id.delete_products_button);
        ApplyChangesBtn= findViewById(R.id.apply_changes_button);
        name= findViewById(R.id.product_name_maintain);
        price=findViewById(R.id.product_price_maintain);
        description=findViewById(R.id.product_description_maintain);
        imageView= findViewById(R.id.product_image_maintain);

        displaySpecificProductsInfo();


        ApplyChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyChanges();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder= new AlertDialog.Builder(AdminMaintainProductActivity.this);
                builder.setMessage("Dear Admin are you sure want to delete this product from Users end???");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteThisProduct();
                            }
                        })
                        .create().show();
            }
        });


    }

    private void deleteThisProduct() {

        productsRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AdminMaintainProductActivity.this, "The Product is delete successfully...", Toast.LENGTH_SHORT).show();

                Intent intent= new Intent(AdminMaintainProductActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void applyChanges() {
        String pName = name.getText().toString();
        String pPrice = price.getText().toString();
        String pDescription = description.getText().toString();

        if (pName.equals("")){
            Toast.makeText(this, "Please write the name of the product", Toast.LENGTH_SHORT).show();
        } else if (pPrice.equals("")) {
            Toast.makeText(this, "Please write price the product", Toast.LENGTH_SHORT).show();
        } else if (pDescription.equals("")) {

            Toast.makeText(this, "Please write the description of the product", Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String, Object> productMap = new HashMap<>();
            productMap.put("pid",productID);
            productMap.put("description",pDescription);
            productMap.put("price",pPrice);
            productMap.put("pname",pName);

            productsRef.updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(AdminMaintainProductActivity.this, "Changes applied successfully", Toast.LENGTH_SHORT).show();

                        Intent intent= new Intent(AdminMaintainProductActivity.this, AdminMaintainProductActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }

    }

    private void displaySpecificProductsInfo() {
        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (datasnapshot.exists()){
                    String pName= datasnapshot.child("pname").getValue().toString();
                    String pPrice= datasnapshot.child("price").getValue().toString();
                    String pDescription= datasnapshot.child("description").getValue().toString();
                    String pImage= datasnapshot.child("image").getValue().toString();



                    name.setText(pName);
                    price.setText(pPrice);
                    description.setText(pDescription);
                    Picasso.get().load(pImage).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
