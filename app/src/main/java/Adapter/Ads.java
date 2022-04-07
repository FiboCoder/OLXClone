package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.olx.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Model.Ad;

public class Ads extends RecyclerView.Adapter<Ads.MyViewHolder> {

    private Context context;
    private List<Ad> myAddsList;

    public Ads(Context context, List<Ad> myAddsList) {
        this.context = context;
        this.myAddsList = myAddsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_add, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Ad ad = myAddsList.get(position);

        holder.tvTitle.setText(ad.getTitle());
        holder.tvPrice.setText(ad.getValue());

        String url = ad.getImages().get(0);
        Picasso.get().load(url).into(holder.ivAdd);
    }

    @Override
    public int getItemCount() {
        return myAddsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private AppCompatImageView ivAdd;
        private AppCompatTextView tvTitle, tvPrice;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivAdd = itemView.findViewById(R.id.ivAdd);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);

        }
    }
}
