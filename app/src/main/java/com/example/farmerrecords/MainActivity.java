package com.example.farmerrecords;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // View references
    private TextInputLayout layoutFarmerName, layoutCrop, layoutQuantity;
    private EditText editTextFarmerName, editTextCrop, editTextQuantity;
    private Button buttonSubmit;
    private TextView textViewRecordCount;
    private RecyclerView recyclerViewRecords;

    // Data
    private List<FarmerRecord> recordList = new ArrayList<>();
    private RecordAdapter recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind views
        layoutFarmerName = findViewById(R.id.layoutFarmerName);
        layoutCrop = findViewById(R.id.layoutCrop);
        layoutQuantity = findViewById(R.id.layoutQuantity);
        editTextFarmerName = findViewById(R.id.editTextFarmerName);
        editTextCrop = findViewById(R.id.editTextCrop);
        editTextQuantity = findViewById(R.id.editTextQuantity);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        textViewRecordCount = findViewById(R.id.textViewRecordCount);
        recyclerViewRecords = findViewById(R.id.recyclerViewRecords);

        // Setup RecyclerView
        recordAdapter = new RecordAdapter(recordList);
        recyclerViewRecords.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRecords.setAdapter(recordAdapter);
        recyclerViewRecords.setHasFixedSize(true);

        // Restore draft values on rotation
        if (savedInstanceState != null) {
            String draftName = savedInstanceState.getString("draft_farmer_name", "");
            String draftCrop = savedInstanceState.getString("draft_crop", "");
            String draftQuantity = savedInstanceState.getString("draft_quantity", "");
            editTextFarmerName.setText(draftName);
            editTextCrop.setText(draftCrop);
            editTextQuantity.setText(draftQuantity);
        }

        buttonSubmit.setOnClickListener(v -> onSubmit());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save draft values across rotation
        outState.putString("draft_farmer_name", editTextFarmerName.getText().toString());
        outState.putString("draft_crop", editTextCrop.getText().toString());
        outState.putString("draft_quantity", editTextQuantity.getText().toString());
    }

    private void onSubmit() {
        String farmerName = editTextFarmerName.getText().toString().trim();
        String crop = editTextCrop.getText().toString().trim();
        String quantityStr = editTextQuantity.getText().toString().trim();

        // Clear previous errors
        layoutFarmerName.setError(null);
        layoutCrop.setError(null);
        layoutQuantity.setError(null);

        boolean valid = true;

        // Validation 1: Farmer name / reference required
        if (TextUtils.isEmpty(farmerName)) {
            layoutFarmerName.setError("Please enter farmer name or reference");
            valid = false;
        }

        // Validation 2: Crop required
        if (TextUtils.isEmpty(crop)) {
            layoutCrop.setError("Please enter crop name");
            valid = false;
        }

        // Validation 3: Quantity required, numeric, non-negative, finite
        if (TextUtils.isEmpty(quantityStr)) {
            layoutQuantity.setError("Please enter quantity in kg");
            valid = false;
        } else {
            try {
                double quantity = Double.parseDouble(quantityStr);
                if (quantity < 0) {
                    layoutQuantity.setError("Quantity cannot be negative");
                    valid = false;
                } else if (!Double.isFinite(quantity)) {
                    layoutQuantity.setError("Quantity must be a finite number");
                    valid = false;
                }
                // Zero is allowed - a zero weekly harvest is a valid business event
                // (different from earlier exercise which required positive quantity)
            } catch (NumberFormatException e) {
                layoutQuantity.setError("Quantity must be a valid number");
                valid = false;
            }
        }

        if (!valid) {
            return;
        }

        // Create record and add to list
        double quantity = Double.parseDouble(quantityStr);
        FarmerRecord record = new FarmerRecord(farmerName, crop, quantity);
        recordList.add(0, record); // Add to top
        recordAdapter.notifyItemInserted(0);
        recyclerViewRecords.scrollToPosition(0);

        // Update record count
        textViewRecordCount.setText("Records: " + recordList.size());

        // Clear form
        editTextFarmerName.setText("");
        editTextCrop.setText("");
        editTextQuantity.setText("");
    }

    // Data model
    public static class FarmerRecord {
        public final String farmerName;
        public final String crop;
        public final double quantity;

        public FarmerRecord(String farmerName, String crop, double quantity) {
            this.farmerName = farmerName;
            this.crop = crop;
            this.quantity = quantity;
        }
    }

    // RecyclerView Adapter
    private static class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {
        private final List<FarmerRecord> records;

        public RecordAdapter(List<FarmerRecord> records) {
            this.records = records;
        }

        @Override
        public RecordViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View view = android.view.LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new RecordViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecordViewHolder holder, int position) {
            FarmerRecord record = records.get(position);
            holder.text1.setText(record.farmerName + " — " + record.crop);
            holder.text2.setText(String.format("%.2f kg", record.quantity));
        }

        @Override
        public int getItemCount() {
            return records.size();
        }

        static class RecordViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2;

            public RecordViewHolder(View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
                text1.setTextSize(16);
                text2.setTextSize(14);
            }
        }
    }
}