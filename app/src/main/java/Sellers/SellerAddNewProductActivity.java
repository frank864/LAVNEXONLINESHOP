package Sellers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.lavnexonlineshop.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SellerAddNewProductActivity extends AppCompatActivity {

    private String CategoryName,Description,Price,Pname,saveCurrentDate,saveCurrentTime;
    private Button AddNewProductsButton;
    private EditText InputProductName,InputProductDescription,InputProductPrice;
    private ImageView InputProductImage;
    private static final int GalleyPick=1;

    private Uri ImageUri;

    private String ProductRandomKey,downloadImageuRL;

    private StorageReference ProductImagesRef;

    private DatabaseReference ProductsRef,sellerRef;
    private ProgressDialog loadingBar;
    private String sName,sAddress,sPhone,sEmail,sID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_add_new_product);

        CategoryName=getIntent().getExtras().get("category").toString();
        ProductImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef= FirebaseDatabase.getInstance().getReference().child("Products");
        loadingBar= new ProgressDialog(this);

        sellerRef=FirebaseDatabase.getInstance().getReference().child("Sellers");

        sellerRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                        if (datasnapshot.exists()){
                            sName= datasnapshot.child("name").getValue().toString();
                            sPhone= datasnapshot.child("phone").getValue().toString();
                            sEmail= datasnapshot.child("email").getValue().toString();
                            sAddress= datasnapshot.child("address").getValue().toString();
                            sID= datasnapshot.child("sid").getValue().toString();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        AddNewProductsButton=findViewById(R.id.add_new_product);
        InputProductImage=findViewById(R.id.select_product_image);
        InputProductName=findViewById(R.id.product_name);
        InputProductDescription= findViewById(R.id.product_description);
        InputProductPrice=findViewById(R.id.product_price);

        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openGalley();
            }
        });

        AddNewProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();

            }
        });


    }

    private void ValidateProductData() {
        Description= InputProductDescription.getText().toString();
        Price=InputProductPrice.getText().toString();
        Pname=InputProductName.getText().toString();

        if (ImageUri==null){
            Toast.makeText(this, "Product Image is mandatory...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Description)) {
            Toast.makeText(this, "Please write product description...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Price)) {
            Toast.makeText(this, "Please write the price of the product...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Pname)) {
            Toast.makeText(this, "Please name your product...", Toast.LENGTH_SHORT).show();

        }else {
            StoreProductInformation();
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait while we are checking your incrediantials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
        }
    }

    private void StoreProductInformation() {

        loadingBar.setTitle("Add New Product");
        loadingBar.setMessage(" Dear seller, Please wait while we are adding the new product");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


        Calendar calendar= Calendar.getInstance();

        SimpleDateFormat currentDate= new SimpleDateFormat("MMM,dd,yyyy");
        saveCurrentDate= currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime= new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime= currentTime.format(calendar.getTime());



        ProductRandomKey=saveCurrentDate + saveCurrentTime;

        StorageReference filepath = ProductImagesRef.child(ImageUri.getLastPathSegment() + ProductRandomKey +".jpg");

        final UploadTask uploadTask= filepath.putFile(ImageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();

                Toast.makeText(SellerAddNewProductActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SellerAddNewProductActivity.this, "Image Uploaded successfully...", Toast.LENGTH_SHORT).show();


                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()){
                            throw  task.getException();

                        }
                        downloadImageuRL = filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()){

                            downloadImageuRL =task.getResult().toString();
                            Toast.makeText(SellerAddNewProductActivity.this, "got the Product Image url successfully...", Toast.LENGTH_SHORT).show();

                            saveProductInfoToDatabase();
                        }
                    }
                });
            }
        });


    }

    private void saveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("pid",ProductRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",Description);
        productMap.put("image",downloadImageuRL);
        productMap.put("category", CategoryName);
        productMap.put("price",Price);
        productMap.put("pname",Pname);
        productMap.put("sellerAddress",sAddress);
        productMap.put("sellerName",sName);
        productMap.put("sellerEmail",sEmail);
        productMap.put("sid",sID);
        productMap.put("sellerPhone",sPhone);
        productMap.put("productState","Not Approved");




        ProductsRef.child(ProductRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){

                            Intent intent= new Intent(SellerAddNewProductActivity.this,SellerHomeActivity.class);
                            startActivity(intent);

                            loadingBar.dismiss();
                            Toast.makeText(SellerAddNewProductActivity.this, "Product is added successfully...", Toast.LENGTH_SHORT).show();
                        }else {
                            loadingBar.dismiss();
                            String message =task.getException().toString();

                            Toast.makeText(SellerAddNewProductActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    private void openGalley() {
        Intent galleyintent = new Intent();
        galleyintent.setAction(Intent.ACTION_GET_CONTENT);
        galleyintent.setType("image/*");
        startActivityForResult(galleyintent,GalleyPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleyPick && resultCode ==RESULT_OK && data != null)
        {
            ImageUri = data.getData();
            InputProductImage.setImageURI(ImageUri);
        }
    }


}