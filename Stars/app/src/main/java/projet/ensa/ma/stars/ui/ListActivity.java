package projet.ensa.ma.stars.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import projet.ensa.ma.stars.R;
import projet.ensa.ma.stars.adapter.StarAdapter;
import projet.ensa.ma.stars.beans.Star;
import projet.ensa.ma.stars.service.StarService;

public class ListActivity extends AppCompatActivity {

    private StarAdapter starAdapter;
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        rv = findViewById(R.id.recycle_view);
        rv.setLayoutManager(new LinearLayoutManager(this));
        starAdapter = new StarAdapter(this, StarService.getInstance().findAll());
        rv.setAdapter(starAdapter);

        // Swipe to delete
        starAdapter.setOnStarDeletedListener((star, position) -> {
            Snackbar.make(rv, star.getName() + " supprimé", Snackbar.LENGTH_LONG)
                    .setAction("Annuler", v -> starAdapter.restoreItem(star, position))
                    .show();
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder target) { return false; }
            @Override public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                starAdapter.deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(rv);

        // FAB → Add star
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v ->
                startActivityForResult(new Intent(this, AddStarActivity.class), 1)
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            starAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.app_bar_search);
        SearchView searchView = (SearchView) item.getActionView();
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override public boolean onQueryTextSubmit(String q) { return true; }
                @Override public boolean onQueryTextChange(String newText) {
                    if (starAdapter != null) starAdapter.getFilter().filter(newText);
                    return true;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share) {
            ShareCompat.IntentBuilder
                    .from(this)
                    .setType("text/plain")
                    .setChooserTitle("Partager Stars App")
                    .setText("Découvrez l'application Stars !")
                    .startChooser();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}