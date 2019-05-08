package sw.gmit.ie.crist.cameradetection.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import sw.gmit.ie.crist.cameradetection.R;

public class NameDialog extends AppCompatDialogFragment {
    // Variables
    private EditText personName; // text inputted in the dialog
    private NameDialogListener listener; // dialog listener

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ()); // build the dialog

        LayoutInflater inflater = getActivity ().getLayoutInflater ();
        View view = inflater.inflate (R.layout.layout_dialog, null); // view the dialog

        builder.setView (view)
                .setTitle ("Enter the full person's name from the notification")
                .setNegativeButton ("Cancel", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton ("Ok", new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = null;

                        // If the input is empty
                        // let the user know a person's name is required
                        // otherwise get the input/name
                        if (TextUtils.isEmpty (personName.getText ())) {
                            showMessage ("Person's full name required");
                        } else {
                            // gets the input from the dialog box
                            // and sets it as the name of the person
                            // whose videos are to be downloaded
                            name = personName.getText ().toString ();
                            // sends the name of that person
                            // to the applyTexts(name) method
                            // in the "Home" activity
                            listener.applyTexts (name);
                        }
                    }
                });

        personName = view.findViewById (R.id.person_name);  // get the XML id of the input text

        return builder.create ();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach (context);

        try {
            // attach the listener to the dialog
            // to make sure applyTexts(name) method is called
            listener = (NameDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException (context.toString () + "must implement NameDialogListener");
        }
    }

    public interface NameDialogListener {
        void applyTexts(String personName); // call the method and send the dialog input (person's name) to it
    }

    private void showMessage(String message) {
        Toast.makeText (getActivity ().getApplicationContext (), message, Toast.LENGTH_SHORT).show ();
    }


}
