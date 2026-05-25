package projet.ensa.ma.stars.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import projet.ensa.ma.stars.R;
import projet.ensa.ma.stars.beans.Star;
import projet.ensa.ma.stars.service.StarService;

public class StarAdapter extends RecyclerView.Adapter<StarAdapter.StarViewHolder>
        implements Filterable {

    public interface OnStarDeletedListener {
        void onStarDeleted(Star star, int position);
    }

    private final List<Star> stars;           // master list
    private List<Star> starsFilter;           // displayed list
    private final Context context;
    private final NewFilter mFilter;
    private OnStarDeletedListener deleteListener;

    public StarAdapter(Context context, List<Star> stars) {
        this.context = context;
        this.stars = stars;
        this.starsFilter = new ArrayList<>(stars);
        this.mFilter = new NewFilter(this);
    }

    public void setOnStarDeletedListener(OnStarDeletedListener listener) {
        this.deleteListener = listener;
    }

    /** Called by ItemTouchHelper on swipe */
    public void deleteItem(int position) {
        Star star = starsFilter.get(position);
        StarService.getInstance().delete(star);
        starsFilter.remove(position);
        stars.remove(star);
        notifyItemRemoved(position);
        if (deleteListener != null) deleteListener.onStarDeleted(star, position);
    }

    /** Restore after undo */
    public void restoreItem(Star star, int position) {
        StarService.getInstance().create(star);
        starsFilter.add(position, star);
        stars.add(star);
        notifyItemInserted(position);
    }

    @NonNull
    @Override
    public StarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.star_item, parent, false);
        StarViewHolder holder = new StarViewHolder(v);

        holder.itemView.setOnClickListener(view -> {
            int position = holder.getBindingAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;
            showRatingDialog(starsFilter.get(position), position);
        });

        return holder;
    }

    private void showRatingDialog(Star star, int position) {
        View popup = LayoutInflater.from(context).inflate(R.layout.star_edit_item, null);
        ImageView img     = popup.findViewById(R.id.img);
        RatingBar bar     = popup.findViewById(R.id.ratingBar);
        TextView  nameTV  = popup.findViewById(R.id.editName);

        Glide.with(context)
                .load(star.getImg())
                .apply(new RequestOptions().circleCrop())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(img);

        bar.setRating(star.getRating());
        if (nameTV != null) nameTV.setText(star.getName());

        new AlertDialog.Builder(context)
                .setTitle("✏️ Modifier la note")
                .setView(popup)
                .setPositiveButton("Valider", (dialog, which) -> {
                    float newRating = bar.getRating();
                    star.setRating(newRating);
                    StarService.getInstance().update(star);
                    notifyItemChanged(position);
                })
                .setNegativeButton("Annuler", null)
                .show();
    }

    @Override
    public void onBindViewHolder(@NonNull StarViewHolder holder, int position) {
        Star star = starsFilter.get(position);

        Glide.with(context)
                .load(star.getImg())
                //.apply(new RequestOptions().circleCrop().override(120, 120)
                        //.placeholder(R.drawable.star)
                        //.error(R.drawable.star))
                //.transition(DrawableTransitionOptions.withCrossFade())
                .circleCrop()
                .into(holder.img);

        holder.name.setText(star.getName().toUpperCase());
        holder.ratingBar.setRating(star.getRating());
        holder.ratingValue.setText(String.format("%.1f / 5", star.getRating()));
    }

    @Override public int getItemCount() { return starsFilter.size(); }
    @Override public Filter getFilter() { return mFilter; }

    // ── ViewHolder ────────────────────────────────────────────────────────────

    public static class StarViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView  name, ratingValue;
        RatingBar ratingBar;

        public StarViewHolder(@NonNull View itemView) {
            super(itemView);
            img         = itemView.findViewById(R.id.imgStar);
            name        = itemView.findViewById(R.id.tvName);
            ratingBar   = itemView.findViewById(R.id.rating);
            ratingValue = itemView.findViewById(R.id.tvRatingValue);
        }
    }

    // ── Filter ────────────────────────────────────────────────────────────────

    public class NewFilter extends Filter {
        private final RecyclerView.Adapter<StarViewHolder> mAdapter;

        public NewFilter(RecyclerView.Adapter<StarViewHolder> adapter) {
            this.mAdapter = adapter;
        }

        @Override
        protected FilterResults performFiltering(CharSequence cs) {
            List<Star> filtered = new ArrayList<>();
            if (cs == null || cs.length() == 0) {
                filtered.addAll(stars);
            } else {
                String pattern = cs.toString().toLowerCase().trim();
                for (Star s : stars) {
                    if (s.getName().toLowerCase().contains(pattern)) filtered.add(s);
                }
            }
            FilterResults results = new FilterResults();
            results.values = filtered;
            results.count  = filtered.size();
            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence cs, FilterResults results) {
            starsFilter = (List<Star>) results.values;
            mAdapter.notifyDataSetChanged();
        }
    }
}