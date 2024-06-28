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
import android.widget.TextView;
import android.widget.Toast;

import com.example.lavnexonlineshop.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import Admins.AdminHomeActivity;
import Model.Users;
import Prevalent.Prevalent;
import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private Button LoginButton;
    private EditText InputPhoneNumber,InputPassword;
    private ProgressDialog loadingBar;
    private String parentDbName ="Users";
    private CheckBox chkBoxRememberMe;
    private TextView AdminLink,NotAdminLink,ForgetPasswordLink;
    private Drawable eyeIcon;
    private boolean isPasswordVisible = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton= findViewById(R.id.login_btn);
        InputPhoneNumber = findViewById(R.id.login_phone_number);
        InputPassword= findViewById(R.id.login_password_input);
        loadingBar= new ProgressDialog(this);
        chkBoxRememberMe= findViewById(R.id.remember_me_chkb);
        Paper.init(this);
        eyeIcon = ContextCompat.getDrawable(this, R.drawable.ic_eye);


        AdminLink= findViewById(R.id.admin_panel_link);
        NotAdminLink= findViewById(R.id.not_admin_panel_link);
        ForgetPasswordLink=findViewById(R.id.forget_password_link);




        ForgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(LoginActivity.this, ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);
            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login Admin");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentDbName="Admins";
            }
        });
        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login Customer");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentDbName="Users";
            }
        });
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

    private void LoginUser() {
        String phone = InputPhoneNumber.getText().toString();
        String password =InputPassword.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please Enter your phoneNumber...", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Kindly fill your password...", Toast.LENGTH_SHORT).show();

        }else {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please be patient while we are checking your incredientials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccesssToAccount(phone,password);
        }



    }

    private void AllowAccesssToAccount(String phone, String password) {

        if (chkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }

        final DatabaseReference RootRef;

        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (datasnapshot.child(parentDbName).child(phone).exists()) {
                    Users usersData = datasnapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone)) {

                        if (usersData.getPassword().equals(password)) {

                            if (parentDbName.equals("Admins")) {
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Welcome in Admin... You can Add products successfully...", Toast.LENGTH_SHORT).show();


                                Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                startActivity(intent);
                            } else if (parentDbName.equals("Users")) {
                                Intent intent= new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);

                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Welcome you are logged in successfully", Toast.LENGTH_SHORT).show();

                            }
                        }

                    }
                }else {
                    Toast.makeText(LoginActivity.this, "Account with this " + phone + "number do not exist", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "You need to create new Account", Toast.LENGTH_SHORT).show();
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