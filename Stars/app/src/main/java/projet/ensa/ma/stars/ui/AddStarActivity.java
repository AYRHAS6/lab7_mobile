package projet.ensa.ma.stars.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import projet.ensa.ma.stars.R;
import projet.ensa.ma.stars.beans.Star;
import projet.ensa.ma.stars.service.StarService;

public class AddStarActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_star);

        EditText etName   = findViewById(R.id.etName);
        EditText etImg    = findViewById(R.id.etImg);
        RatingBar ratingBar = findViewById(R.id.addRatingBar);
        Button btnSave    = findViewById(R.id.btnSave);
        Button btnCancel  = findViewById(R.id.btnCancel);

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String img  = etImg.getText().toString().trim();
            float rating = ratingBar.getRating();

            if (TextUtils.isEmpty(name)) {
                etName.setError("Le nom est obligatoire");
                return;
            }
            if (TextUtils.isEmpty(img)) {
                img = "https://upload.wikimedia.org/wikipedia/commons/thumb/8/89/Portrait_Placeholder.png/200px-Portrait_Placeholder.png";
            }

            StarService.getInstance().create(new Star(name, img, rating));
            Toast.makeText(this, name + " ajouté !", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        });

        btnCancel.setOnClickListener(v -> finish());
    }
}
