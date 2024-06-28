package com.example.lavnexonlineshop.Buyers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lavnexonlineshop.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import Prevalent.Prevalent;
import Sellers.SellerAddNewProductActivity;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullNameEditText,userPhoneEditText,addressEditText,passwordEditText;
    private TextView profileChangeTextBtn,closeTextBtn,saveTextButton;


    private String myUrl= "";
    private StorageReference storageProfilePictureRef;
    private String checker="";

    private static final int GalleyPick=1;
    private Uri ImageUri;

    private RelativeLayout parent;
    private StorageTask uploadTask;

    private Button securityQuestionBtn;


    private int requestCode;
    private int resultCode;
    @Nullable
    private Intent data;
    private Drawable eyeIcon;
    private boolean isPasswordVisible = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);




        securityQuestionBtn=findViewById(R.id.security_question_btn);
        parent=findViewById(R.id.parent);
        storageProfilePictureRef= FirebaseStorage.getInstance().getReference().child("Profile pictures");
        passwordEditText=findViewById(R.id.settings_password);
        profileImageView = findViewById(R.id.settings_profile_image);
        fullNameEditText =findViewById(R.id.settings_full_name);
        userPhoneEditText=findViewById(R.id.settings_phone_Number);
        addressEditText= findViewById(R.id.settings_address);

        profileChangeTextBtn=findViewById(R.id.profile_image_change_btn);
        closeTextBtn = findViewById(R.id.close_settings_btn);
        saveTextButton = findViewById(R.id.update_account_settings_btn);
        eyeIcon = ContextCompat.getDrawable(this, R.drawable.ic_eye);


        userInfoDisplay(profileImageView,fullNameEditText,userPhoneEditText,addressEditText,passwordEditText);

        Switch switchDarkMode = findViewById(R.id.switchDarkMode);
        switchDarkMode.setChecked(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES);

        switchDarkMode.setOnCheckedChangeListener((buttonView,isChecked)-> {
            if(isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            recreate();
        });

        passwordEditText.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Check if touch is on the drawableright (eye icon)
                if (event.getAction() == MotionEvent.ACTION_UP &&
                        event.getRawX() >= (passwordEditText.getRight() -
                                passwordEditText.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
                return false;
            }
        });

    }


    private void userInfoDisplay(CircleImageView profileImageView, EditText fullNameEditText, EditText userPhoneEditText, EditText addressEditText, EditText passwordEditText) {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.currentOnlineUser.getPhone());
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (datasnapshot.exists()){
                    if (datasnapshot.child("image").exists()){
                        String image = datasnapshot.child("image").getValue().toString();
                        String name = datasnapshot.child("name").getValue().toString();
                        String phone = datasnapshot.child("phone").getValue().toString();
                        String address = datasnapshot.child("address").getValue().toString();
                        String password= datasnapshot.child("password").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullNameEditText.setText(name);
                        userPhoneEditText.setText(phone);
                        addressEditText.setText(address);
                        passwordEditText.setText(password);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        securityQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SettingActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check","settings");
                startActivity(intent);
            }
        });

        closeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checker.equals("clicked"))
                {
                    userInfoSaved();
                }else {
                    updateOnlyUserInfo();
                }
            }
        });

        profileChangeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checker= "clicked";



                OpenGalley();









            }
        });


    }

    private void OpenGalley() {

        Intent galleyintent = new Intent();
        galleyintent.setAction(Intent.ACTION_GET_CONTENT);
        galleyintent.setType("image/*");
        startActivityForResult(galleyintent,GalleyPick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (
                requestCode == GalleyPick && requestCode == RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            profileImageView.setImageURI(ImageUri);
        }
    }

    private void updateOnlyUserInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap.put("name",fullNameEditText.getText().toString());
        userMap.put("password",passwordEditText.getText().toString());
        userMap.put("address",addressEditText.getText().toString());
        userMap.put("phone",userPhoneEditText.getText().toString());

        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

        startActivity(new Intent(SettingActivity.this, HomeActivity.class));
        Toast.makeText(SettingActivity.this, "profile info Uploaded", Toast.LENGTH_SHORT).show();
        finish();
    }








    private void userInfoSaved() {

        if (TextUtils.isEmpty(fullNameEditText.getText().toString())){
            Toast.makeText(this, "Name is mandatory.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(addressEditText.getText().toString())) {
            Toast.makeText(this, "Address is mandatory.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(userPhoneEditText.getText().toString())) {
            Toast.makeText(this, "Phone number is required.", Toast.LENGTH_SHORT).show();
        } else if (checker.equals("clicked")) {

            uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Update profile");
        progressDialog.setMessage("Please be patient as we update your settings.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(ImageUri != null){
            final StorageReference fileRef= storageProfilePictureRef.child(Prevalent.currentOnlineUser.getPhone() + ".jpg");
            uploadTask = fileRef.getFile(ImageUri);



            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task <Uri>task) {
                    if (task.isSuccessful()){
                        Uri downloadUrl =task.getResult();
                        myUrl= downloadUrl.toString();

                        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String, Object> userMap = new HashMap<>();
                        userMap.put("name",fullNameEditText.getText().toString());
                        userMap.put("password",passwordEditText.getText().toString());
                        userMap.put("address",addressEditText.getText().toString());
                        userMap.put("phone",userPhoneEditText.getText().toString());
                        userMap.put("image",myUrl);




                        ref.child(Prevalent.currentOnlineUser.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();


                        startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                        Toast.makeText(SettingActivity.this, "profile info Uploaded", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(SettingActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(this, "image not selected.", Toast.LENGTH_SHORT).show();
        }
    }
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // If password is visible, hide it
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isPasswordVisible = false;
        } else {
            // If password is hidden, show it
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isPasswordVisible = true;
        }

        // Move the cursor to the end of the text
        passwordEditText.setSelection(passwordEditText.getText().length());

        // Update the eye icon based on password visibility
        updateEyeIcon();
    }
    private void updateEyeIcon() {
        // Set the eye icon to the same drawable for both open and closed states
        eyeIcon = ContextCompat.getDrawable(this, R.drawable.ic_eye);

        // Add the updated eye icon to the right of the EditText
        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null, eyeIcon, null);
    }
}









