package Sellers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lavnexonlineshop.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SellerRegistration extends AppCompatActivity {

    private Button sellerLoginBegin,registerButton;
    private EditText nameInput,phoneInput,emailInput,passwordInput,addressInput;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private Drawable eyeIcon;
    private boolean isPasswordVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_registration);

        eyeIcon = ContextCompat.getDrawable(this, R.drawable.ic_eye);


        sellerLoginBegin=findViewById(R.id.seller_already_have_account_btn);
        registerButton=findViewById(R.id.seller_register_btn);
        nameInput=findViewById(R.id.seller_name);
        phoneInput=findViewById(R.id.seller_phone);
        emailInput=findViewById(R.id.seller_email);
        passwordInput=findViewById(R.id.seller_password);
        addressInput=findViewById(R.id.seller_address);
        mAuth= FirebaseAuth.getInstance();
        loadingBar=new ProgressDialog(this);
        passwordInput.setOnTouchListener(new View.OnTouchListener() {


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Check if touch is on the drawableright (eye icon)
                if (event.getAction() == MotionEvent.ACTION_UP &&
                        event.getRawX() >= (passwordInput.getRight() -
                                passwordInput.getCompoundDrawables()[2].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
                return false;
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerSeller();
            }
        });

        sellerLoginBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SellerRegistration.this, SellerLoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerSeller() {
        final String name= nameInput.getText().toString();
        final String phone= phoneInput.getText().toString();
        final String email= emailInput.getText().toString();
        final String password= passwordInput.getText().toString();
        final String address= addressInput.getText().toString();

        if (!name.equals("") && !phone.equals("") && !password.equals("") && !email.equals("") && !address.equals("")){
            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait while we are checking your incrediantials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        final DatabaseReference rootRef;
                        rootRef= FirebaseDatabase.getInstance().getReference();

                        String sid=mAuth.getCurrentUser().getUid();

                        HashMap<String,Object> sellerMap= new HashMap<>();
                        sellerMap.put("sid",sid);
                        sellerMap.put("phone",phone);
                        sellerMap.put("email",email);
                        sellerMap.put("address",address);
                        sellerMap.put("name",name);

                        rootRef.child("Sellers").child("sid").updateChildren(sellerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                loadingBar.dismiss();

                                Toast.makeText(SellerRegistration.this, "You are registered successfully", Toast.LENGTH_SHORT).show();

                                Intent intent= new Intent(SellerRegistration.this, SellerLoginActivity.class);

                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);


                            }
                        });



                    }
                }
            });
        }else {
            Toast.makeText(this, "Please complete registration form", Toast.LENGTH_SHORT).show();
        }

    }


    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // If password is visible, hide it
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            isPasswordVisible = false;
        } else {
            // If password is hidden, show it
            passwordInput.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            isPasswordVisible = true;
        }

        // Move the cursor to the end of the text
        passwordInput.setSelection(passwordInput.getText().length());

        // Update the eye icon based on password visibility
        updateEyeIcon();
    }
    private void updateEyeIcon() {
        // Set the eye icon to the same drawable for both open and closed states
        eyeIcon = ContextCompat.getDrawable(this, R.drawable.ic_eye);

        // Add the updated eye icon to the right of the EditText
        passwordInput.setCompoundDrawablesWithIntrinsicBounds(null, null, eyeIcon, null);
    }
}