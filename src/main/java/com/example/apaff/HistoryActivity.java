package com.example.apaff;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FirebaseFirestore db;
    WeatherAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        loadData();
    }

    private void loadData() {

        db.collection("weather")
                .get()
                .addOnSuccessListener(snapshot -> {

                    List<WeatherRecord> list =
                            snapshot.toObjects(WeatherRecord.class);

                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).id = snapshot.getDocuments().get(i).getId();
                    }

                    adapter = new WeatherAdapter(list, new WeatherAdapter.OnItemClickListener() {

                        @Override
                        public void onClick(WeatherRecord r) {

                            EditText input = new EditText(HistoryActivity.this);

                            new AlertDialog.Builder(HistoryActivity.this)
                                    .setTitle("Editar observação")
                                    .setView(input)
                                    .setPositiveButton("Salvar", (d, w) -> {

                                        db.collection("weather")
                                                .document(r.id)
                                                .update("note", input.getText().toString());

                                        loadData();
                                    })
                                    .setNegativeButton("Cancelar", null)
                                    .show();
                        }

                        @Override
                        public void onLongClick(WeatherRecord r) {

                            db.collection("weather")
                                    .document(r.id)
                                    .delete();

                            Toast.makeText(HistoryActivity.this,
                                    "Deletado!", Toast.LENGTH_SHORT).show();

                            loadData();
                        }
                    });

                    recyclerView.setAdapter(adapter);
                });
    }
}