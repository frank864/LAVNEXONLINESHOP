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

public class SellerLoginActivity extends AppCompatActivity {

    private Button loginSellerButton;
    private EditText emailInput,passwordInput;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private Drawable eyeIcon;
    private boolean isPasswordVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_login);

        eyeIcon = ContextCompat.getDrawable(this, R.drawable.ic_eye);

        emailInput= findViewById(R.id.seller_login_email);
        passwordInput=findViewById(R.id.seller_login_password);
        loginSellerButton=findViewById(R.id.seller_login_btn);
        loadingBar=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();


        loginSellerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginSeller();
            }
        });

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
    }


    private void loginSeller() {
        final String email = emailInput.getText().toString();
        final String password = passwordInput.getText().toString();


        if (!password.equals("") && !email.equals("")){
            loadingBar.setTitle("Logging into Account");
            loadingBar.setMessage("Please wait while we are checking your incrediantials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(SellerLoginActivity.this,SellerHomeActivity.class);
                        startActivity(intent);
                    }
                }
            });

        }else {
            Toast.makeText(this, "Pleae complete the logging form", Toast.LENGTH_SHORT).show();
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