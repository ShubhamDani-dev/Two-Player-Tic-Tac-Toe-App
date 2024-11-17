package androidsamples.java.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // Ensure the NavController is accessed after the layout is inflated
    getWindow().getDecorView().post(() -> {
      NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

      FirebaseAuth mAuth = FirebaseAuth.getInstance();
      if (mAuth.getCurrentUser() != null) {
        Log.d(TAG, "User already logged in, navigating to Dashboard");
        navController.navigate(R.id.dashboardFragment);
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == R.id.menu_logout) {
      Log.d(TAG, "Logout clicked");
      FirebaseAuth.getInstance().signOut();

      // Navigate back to login screen
      Intent intent = new Intent(this, MainActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}