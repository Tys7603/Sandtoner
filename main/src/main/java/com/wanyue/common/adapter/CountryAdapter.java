package com.wanyue.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wanyue.main.R;
import com.wanyue.common.model.Country;

import java.util.ArrayList;
import java.util.List;

public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> implements Filterable {

    private List<Country> countries;
    private List<Country> countriesFiltered;
    private Context context;
    private OnCountrySelectedListener listener;

    public interface OnCountrySelectedListener {
        void onCountrySelected(Country country);
    }

    public CountryAdapter(Context context, List<Country> countries, OnCountrySelectedListener listener) {
        this.context = context;
        this.countries = countries;
        this.countriesFiltered = countries;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_country, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Country country = countriesFiltered.get(position);
        holder.imgFlag.setImageResource(country.getFlagResId());
        holder.tvName.setText(country.getName());
        holder.tvCode.setText(country.getDialCode());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCountrySelected(country);
            }
        });
    }

    @Override
    public int getItemCount() {
        return countriesFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString().toLowerCase().trim();
                if (charString.isEmpty()) {
                    countriesFiltered = countries;
                } else {
                    List<Country> filteredList = new ArrayList<>();
                    for (Country country : countries) {
                        if (country.getName().toLowerCase().contains(charString) || 
                            country.getDialCode().contains(charString) ||
                            country.getCode().toLowerCase().contains(charString)) {
                            filteredList.add(country);
                        }
                    }
                    countriesFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = countriesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                countriesFiltered = (List<Country>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFlag;
        TextView tvName;
        TextView tvCode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFlag = itemView.findViewById(R.id.img_country_flag);
            tvName = itemView.findViewById(R.id.tv_country_name);
            tvCode = itemView.findViewById(R.id.tv_country_code);
        }
    }
}