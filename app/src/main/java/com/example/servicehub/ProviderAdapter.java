package com.example.servicehub;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.List;

/**
 * RecyclerView adapter for nearby provider cards on the map screen.
 */
public class ProviderAdapter extends RecyclerView.Adapter<ProviderAdapter.ProviderViewHolder> {

    public interface OnProviderInteractionListener {
        void onProviderSelected(ProviderModel provider, int position);

        void onViewProfile(ProviderModel provider);
    }

    private final List<ProviderModel> providerList;
    private final OnProviderInteractionListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public ProviderAdapter(List<ProviderModel> providerList, OnProviderInteractionListener listener) {
        this.providerList = providerList;
        this.listener = listener;
    }

    public void setSelectedPosition(int position) {
        int previous = selectedPosition;
        selectedPosition = position;
        if (previous != RecyclerView.NO_POSITION) {
            notifyItemChanged(previous);
        }
        if (selectedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(selectedPosition);
        }
    }

    @NonNull
    @Override
    public ProviderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_provider, parent, false);
        return new ProviderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProviderViewHolder holder, int position) {
        ProviderModel provider = providerList.get(position);
        holder.tvProviderName.setText(provider.getName());
        holder.tvRating.setText(String.format("⭐ %.1f", provider.getRating()));
        holder.tvServiceCharge.setText(provider.getServiceCharge());
        holder.tvDistance.setText(DistanceUtils.formatDistance(provider.getDistanceKm()));

        boolean isSelected = position == selectedPosition;
        holder.cardProvider.setStrokeWidth(isSelected ? 4 : 0);
        holder.cardProvider.setStrokeColor(holder.itemView.getContext()
                .getColor(R.color.primary));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProviderSelected(provider, holder.getBindingAdapterPosition());
            }
        });

        holder.btnViewProfile.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewProfile(provider);
            }
        });
    }

    @Override
    public int getItemCount() {
        return providerList != null ? providerList.size() : 0;
    }

    static class ProviderViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardProvider;
        TextView tvProviderName, tvRating, tvServiceCharge, tvDistance;
        Button btnViewProfile;

        ProviderViewHolder(@NonNull View itemView) {
            super(itemView);
            cardProvider = itemView.findViewById(R.id.cardProvider);
            tvProviderName = itemView.findViewById(R.id.tvProviderName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvServiceCharge = itemView.findViewById(R.id.tvServiceCharge);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            btnViewProfile = itemView.findViewById(R.id.btnViewProfile);
        }
    }
}
