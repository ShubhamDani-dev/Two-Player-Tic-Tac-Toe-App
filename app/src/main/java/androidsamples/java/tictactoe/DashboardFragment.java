package androidsamples.java.tictactoe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

public class DashboardFragment extends Fragment {

  private static final String TAG = "DashboardFragment";
  private NavController mNavController;

  /**
   * Mandatory empty constructor for the fragment manager to instantiate the
   * fragment (e.g. upon screen orientation changes).
   */
  public DashboardFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    setHasOptionsMenu(true); // Needed to display the action menu for this fragment
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
                           ViewGroup container,
                           Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_dashboard, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mNavController = Navigation.findNavController(view);

    // TODO if a user is not logged in, go to LoginFragment

    // Show a dialog when the user clicks the "new game" button
    view.findViewById(R.id.fab_new_game).setOnClickListener(v -> {
      AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
      builder.setTitle(R.string.new_game)
              .setMessage(R.string.new_game_dialog_message);

      // Listener for single-player mode
      DialogInterface.OnClickListener singlePlayerListener = (dialog, which) -> {
        String gameType = getString(R.string.one_player);
        Log.d(TAG, "New Game: " + gameType);
        NavDirections action = DashboardFragmentDirections.actionGame(gameType, null);
        mNavController.navigate(action);
      };

      // Listener for two-player mode
      DialogInterface.OnClickListener twoPlayerListener = (dialog, which) -> {
        AlertDialog.Builder idDialog = new AlertDialog.Builder(requireActivity());
        idDialog.setTitle(R.string.enter_game_id);

        View inputView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_game_id, null);
        idDialog.setView(inputView);

        idDialog.setPositiveButton(R.string.start_game, (idDialogInterface, idWhich) -> {
          String gameId = ((EditText) inputView.findViewById(R.id.edit_game_id)).getText().toString();
          if (!gameId.isEmpty()) {
            Log.d(TAG, "Starting Two Player Game with ID: " + gameId);

            String gameType = getString(R.string.two_player);
            NavDirections action = DashboardFragmentDirections.actionGame(gameType, gameId);
            mNavController.navigate(action);
          } else {
            Toast.makeText(requireContext(), R.string.enter_valid_game_id, Toast.LENGTH_SHORT).show();
          }
        });

        idDialog.setNegativeButton(R.string.cancel, (idDialogInterface, idWhich) -> idDialogInterface.dismiss());
        idDialog.create().show();
      };

      builder.setPositiveButton(R.string.two_player, twoPlayerListener)
              .setNegativeButton(R.string.one_player, singlePlayerListener)
              .setNeutralButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

      builder.create().show();
    });
  }

  @Override
  public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    inflater.inflate(R.menu.menu_logout, menu);
    // this action menu is handled in MainActivity
  }
}