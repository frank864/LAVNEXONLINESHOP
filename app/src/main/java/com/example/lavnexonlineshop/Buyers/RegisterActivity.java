package com.example.lavnexonlineshop.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lavnexonlineshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText InputName,InputPhoneNumber,InputPassword;
    private ProgressDialog loadingBar;
    private Drawable eyeIcon;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        CreateAccountButton=findViewById(R.id.register_btn);
        InputName=findViewById(R.id.register_username_input);
        InputPhoneNumber=findViewById(R.id.register_phone_number);
        InputPassword=findViewById(R.id.register_password_input);
        loadingBar= new ProgressDialog(this);


        eyeIcon = ContextCompat.getDrawable(this, R.drawable.ic_eye);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {
        String name = InputName.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password =InputPassword.getText().toString();


        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Please write your Name...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please Enter your phoneNumber...", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Kindly fill your password...", Toast.LENGTH_SHORT).show();
        }else {
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please be patient while we are checking your incredientials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();


            ValidatephoneNumber(name,phone,password);
        }

        InputPassword.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Check if touch is on the drawableright (eye icon)
                if (event.getAction() == MotionEvent.ACTION_UP &&
                        event.getRawX() >= (InputPassword.getRight() -
                                InputPassword.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
                return false;
            }
        });
    }

    private void ValidatephoneNumber(String name, String phone, String password) {
        final DatabaseReference RootRef;

        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if ((!datasnapshot.child("Users").child(phone).exists())){


                    HashMap<String,Object> userdataMap = new HashMap<>();
                    userdataMap.put("phone",phone);
                    userdataMap.put("password",password);
                    userdataMap.put("name",name);


                    RootRef.child("Users").child(phone).updateChildren(userdataMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(RegisterActivity.this, "Congratulations, you account is create successfully", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();


                                        Intent intent= new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    } else {
                                        loadingBar.dismiss();
                                        Toast.makeText(RegisterActivity.this, "Network error please try again later.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }else {
                    Toast.makeText(RegisterActivity.this, "This " + phone + "alrady exists...", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this, "Try again using a different number", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // If password is visible, hide it
            InputPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isPasswordVisible = false;
        } else {
            // If password is hidden, show it
            InputPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isPasswordVisible = true;
        }

        // Move the cursor to the end of the text
        InputPassword.setSelection(InputPassword.getText().length());

        // Update the eye icon based on password visibility
        updateEyeIcon();
    }
    private void updateEyeIcon() {
        // Set the eye icon to the same drawable for both open and closed states
        eyeIcon = ContextCompat.getDrawable(this, R.drawable.ic_eye);

        // Add the updated eye icon to the right of the EditText
        InputPassword.setCompoundDrawablesWithIntrinsicBounds(null, null, eyeIcon, null);
    }
}