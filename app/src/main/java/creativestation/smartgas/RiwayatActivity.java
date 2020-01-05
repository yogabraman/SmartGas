package creativestation.smartgas;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import creativestation.smartgas.Adapter.RiwayatAdapter;
import creativestation.smartgas.Preferences.PrefManager;
import creativestation.smartgas.models.Riwayat;

public class RiwayatActivity extends AppCompatActivity {
    RiwayatAdapter riwayatAdapter;
    String gaschild;
    public PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat);
        setTitle("Riwayat Ganti LPG");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        prefManager = new PrefManager(getApplicationContext());
        gaschild = prefManager.getAlat();

                Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("SmartGas")
                .child(gaschild)
                .child("GantiLPG");
        FirebaseRecyclerOptions<Riwayat> options = new FirebaseRecyclerOptions.Builder<Riwayat>()
                .setQuery(query, Riwayat.class)
                .setLifecycleOwner(this)
                .build();

        riwayatAdapter = new RiwayatAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recycle_riwayat);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(riwayatAdapter);
    }
    @Override
    public void onStart() {
        super.onStart();
        riwayatAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        riwayatAdapter.stopListening();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
