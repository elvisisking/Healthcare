package com.redhat.healthcare;

import android.R.string;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * Callback for a {@link PatientChart} result.
 */
public abstract class PatientChartCallback implements OnClickListener {

    @Override
    public void onClick( final DialogInterface dialog,
                         final int which ) {
        // TODO quit app
    }

    /**
     * @param error the error that was caught running the task (never <code>null</code>)
     */
    public void onFailure( final Exception error ) {
        HealthcareApp.logError( getClass(), "onFailure", null, error );
        showAlertDialog( HealthcareApp.getContext().getString( R.string.app_error_dialog_title ), error.getLocalizedMessage() );
    }

    /**
     * This method is called when an error occurs but no {@link Exception} was caught.
     *
     * @param errorMsg the error message generated running the task (never empty)
     */
    public void onFailure( final String errorMsg ) {
        HealthcareApp.logError( getClass(), "onFailure", errorMsg, null );
        showAlertDialog( HealthcareApp.getContext().getString( R.string.app_error_dialog_title ), errorMsg );
    }

    /**
     * The task completed successfully.
     *
     * @param results the results (never <code>null</code>)
     */
    public abstract void onSuccess( final PatientChart results );

    private void showAlertDialog( final String title,
                                  final String message ) {
        final Context context = HealthcareApp.getContext();
        final Builder builder = new Builder( context );
        builder.setTitle( title );
        builder.setMessage( message );

        final String positiveText = context.getString( string.ok );
        builder.setPositiveButton( positiveText, this );

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

}
