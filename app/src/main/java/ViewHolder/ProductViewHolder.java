package ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lavnexonlineshop.R;

import Interfaces.ItemClickListerner;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtProductName, txtProductDescription,txtProductPrice;
    public ImageView imageView;
    public ItemClickListerner listerner;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView= itemView.findViewById(R.id.product_image);
        txtProductName= itemView.findViewById(R.id.product_name);
        txtProductDescription=itemView.findViewById(R.id.product_description);
        txtProductPrice=itemView.findViewById(R.id.product_price);
    }

    public void setItemClickListerner(ItemClickListerner listerner){
        this.listerner=listerner;
    }

    @Override
    public void onClick(View view) {

        listerner.onClick(view, getAdapterPosition(),false);
    }
}
