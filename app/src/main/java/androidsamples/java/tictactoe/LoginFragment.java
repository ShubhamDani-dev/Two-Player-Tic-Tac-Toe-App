package androidsamples.java.tictactoe;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

public class LoginFragment extends Fragment {
    private static final String TAG = "LoginFragment";
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            try {
                Log.d(TAG, "User already logged in, navigating to Dashboard");
                NavDirections action = LoginFragmentDirections.actionLoginSuccessful();
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(action);
            } catch (Exception e) {
                Log.e(TAG, "Navigation failed", e);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        EditText emailField = view.findViewById(R.id.edit_email);
        EditText passwordField = view.findViewById(R.id.edit_password);

        view.findViewById(R.id.btn_log_in).setOnClickListener(v -> {
            String email = emailField.getText().toString();
            String password = passwordField.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Sign-in successful");
                                NavDirections action = LoginFragmentDirections.actionLoginSuccessful();
                                Navigation.findNavController(v).navigate(action);
                            } else {
                                Log.w(TAG, "Sign-in failed", task.getException());
                                Toast.makeText(requireContext(), "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}