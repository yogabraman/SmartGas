package creativestation.smartgas.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

import creativestation.smartgas.R;
import creativestation.smartgas.models.Riwayat;

public class RiwayatAdapter extends FirebaseRecyclerAdapter<Riwayat, RiwayatAdapter.RiwayatHolder> {

    public RiwayatAdapter(@NonNull FirebaseRecyclerOptions<Riwayat> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull RiwayatAdapter.RiwayatHolder holder, int position, @NonNull Riwayat model) {
        Date wakt = new Date(model.getWaktu());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

        holder.text_date.setText(dateFormat.format(wakt));
    }

    @Override
    public RiwayatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_riwayat, parent, false);
        return new RiwayatHolder(view);
    }

    class RiwayatHolder extends RecyclerView.ViewHolder {
        TextView text_date;
        CardView cardView;

        public RiwayatHolder(View itemView) {
            super(itemView);
            text_date = itemView.findViewById(R.id.txt_date);
            cardView = itemView.findViewById(R.id.card_riwayat);
        }
    }
}
