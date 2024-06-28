package Admins;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lavnexonlineshop.Buyers.HomeActivity;
import com.example.lavnexonlineshop.Buyers.MainActivity;
import com.example.lavnexonlineshop.R;

public class AdminHomeActivity extends AppCompatActivity {
    private Button LogoutBtn,checkOrderBtn,maintainProductBtn,checkApprovedProductsBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        LogoutBtn= findViewById(R.id.admin_logout_btn);
        checkOrderBtn=findViewById(R.id.check_orders_btn);
        maintainProductBtn=findViewById(R.id.maintain_btn);
        checkApprovedProductsBtn=findViewById(R.id.check_approved_products_btn);


        maintainProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(AdminHomeActivity.this, HomeActivity.class);
                intent.putExtra("Admin","Admin");
                startActivity(intent);
            }
        });

        LogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminHomeActivity.this);
                builder.setMessage("Dear Admin are you sure want to Logout from Admin's section???");
                builder.setNegativeButton("No",new DialogInterface.OnClickListener()

                        {
                            @Override
                            public void onClick (DialogInterface dialog,int which){

                            }
                        }).

                        setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick (DialogInterface dialog,int which){
                                Intent intent = new Intent(AdminHomeActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);


                                Toast.makeText(AdminHomeActivity.this, "We sre happy to have you... Welcome back again.", Toast.LENGTH_SHORT).show();
                            }
                        }) .create().show();
            }
        });

        checkOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(AdminHomeActivity.this, AdminNewOrderActivity.class);
                startActivity(intent);

            }
        });
        checkApprovedProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(AdminHomeActivity.this, CheckNewProductActivity.class);
                startActivity(intent);

            }
        });
    }

}