package edu.utep.cs.floodalertsystem.GUI.AlertDialogs;

/**
 * <h1> Camera Alert Dialog</h1>
 *
 * This class sets the layout for the Camera Alert Dialog
 *
 *
 * @author  IRES: U.S.-Mexico Interdisciplinary Research Collaboration for
 * Smart Cities investigators and contributing participants.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import edu.utep.cs.floodalertsystem.R;

public class CameraAlertDialog extends DialogFragment {
    private final String TAG="Flood";
    private final String ACTIVITY="CameraAlertDialog: ";

    public static CameraAlertDialog newInstance(int title) {
        CameraAlertDialog frag = new CameraAlertDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int titleText = getArguments().getInt("title");

        TextView title = new TextView(getActivity());
        //Title can be customized in the next line
        title.setText(titleText);
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(20, 20, 20, 20);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(25);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_alert_camera, null))
                .setCustomTitle(title)
                .setPositiveButton(R.string.understood,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        }
                );

        return builder.create();
    }

}
