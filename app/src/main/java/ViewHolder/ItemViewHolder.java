package ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lavnexonlineshop.R;

import Interfaces.ItemClickListerner;

public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView txtproductionName,txtproductionDescription,txtproductPrice,txtProductsStatus;
    public ImageView ProductSellerImage;
    public ItemClickListerner listerner;

    @Override
    public void onClick(View view) {
        listerner.onClick(view, getAdapterPosition(),false);

    }

    public ItemViewHolder(@NonNull View itemView) {
        super(itemView);

        ProductSellerImage=itemView.findViewById(R.id.product_seller_image);
        txtproductionName=itemView.findViewById(R.id.product_seller_name);
        txtproductionDescription=itemView.findViewById(R.id.product_seller_description);
        txtproductPrice=itemView.findViewById(R.id.product_seller_price);
        txtProductsStatus=itemView.findViewById(R.id.seller_product_status);
    }

    public void setItemClickListerner(ItemClickListerner listerner){
        this.listerner=listerner;
    }
}



