package com.redhat.healthcare;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tvFirstName;
    private TextView tvLastName;
    private TextView tvBp;
    private CheckBox chkHighBp;
    private TextView tvMedication;
    private TextView tvInteractions;
    private TextView tvSideEffects;
    private TextView tvResults;
    private TextView tvWarnings;

    private void lookupPatientChart( final String firstName,
                                     final String lastName ) {
        new GetData( firstName, lastName, new PatientChartCallback() {

            @Override
            public void onSuccess( final PatientChart results ) {
                refreshChart( results );
            }
        }, R.string.app_error_dialog_title ).execute();
    }

    private void refreshChart( final PatientChart newChart ) {
        if ( newChart == null ) {
            this.tvFirstName.setText( "" );
            this.tvLastName.setText( "" );
            this.tvBp.setText( "" );
            this.chkHighBp.setChecked( false );
            this.tvMedication.setText( "" );
            this.tvInteractions.setText( R.string.none_found );
            this.tvSideEffects.setText( R.string.none_found );
            this.tvResults.setText( R.string.none_found );
            this.tvWarnings.setText( R.string.none_found );
        } else {
            this.tvFirstName.setText( newChart.getFirstName() );
            this.tvLastName.setText( newChart.getLastName() );
            this.tvBp.setText( newChart.getSystolic() + " / " + newChart.getDiastolic() );
            this.chkHighBp.setChecked( newChart.isHighBpHistory() );
            this.tvMedication.setText( newChart.getMedication() );
            this.tvInteractions.setText( newChart.getInteractions() );
            this.tvSideEffects.setText( newChart.getSideEffects() );
            this.tvResults.setText( newChart.getResultsMessage() );
            this.tvWarnings.setText( newChart.getWarningMessage() );
        }
    }

    @Override
    protected void onCreate( final Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        HealthcareApp.setContext( this );

        Toolbar toolbar = ( Toolbar )findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        this.chkHighBp = ( CheckBox )findViewById( R.id.chk_bp_history );
        this.tvBp = ( TextView )findViewById( R.id.tv_bp );
        this.tvFirstName = ( TextView )findViewById( R.id.tv_first_name );
        this.tvInteractions = ( TextView )findViewById( R.id.tv_interactions );
        this.tvLastName = ( TextView )findViewById( R.id.tv_last_name );
        this.tvMedication = ( TextView )findViewById( R.id.tv_medication );
        this.tvResults = ( TextView )findViewById( R.id.tv_results );
        this.tvSideEffects = ( TextView )findViewById( R.id.tv_side_effects );
        this.tvWarnings = ( TextView )findViewById( R.id.tv_warnings );

        FloatingActionButton fab = ( FloatingActionButton )findViewById( R.id.fab );
        fab.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( final View view ) {
                showPatientQueryDialog();
            }
        } );
    }

    @Override
    public boolean onCreateOptionsMenu( final Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( final MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if ( id == R.id.action_settings ) {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }

    private void showPatientQueryDialog() {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( this );

        // configure
        alertDialogBuilder.setTitle( R.string.query_title )
            .setMessage( R.string.query_message )
            .setCancelable( true )
            .setView( R.layout.query )
            .setPositiveButton( R.string.query_search, null )
            .setNegativeButton( R.string.query_cancel, new DialogInterface.OnClickListener() {

                @Override
                public void onClick( final DialogInterface dialog,
                                     final int id ) {
                    dialog.cancel();
                }
            } );

        // create alert dialog
        final AlertDialog dialog = alertDialogBuilder.create();

        // show it
        dialog.show();

        // setup listeners
        final Button btnSearch = dialog.getButton( DialogInterface.BUTTON_POSITIVE );
        btnSearch.setEnabled( false );
        btnSearch.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick( final View v ) {
                final TextView tvFirstName = ( TextView )dialog.findViewById( R.id.query_first_name );
                final TextView tvLastName = ( TextView )dialog.findViewById( R.id.query_last_name );
                lookupPatientChart( tvFirstName.getText().toString(), tvLastName.getText().toString() );
                dialog.cancel();
            }
        } );

        final TextView tvFirstName = ( TextView )dialog.findViewById( R.id.query_first_name );
        final TextView tvLastName = ( TextView )dialog.findViewById( R.id.query_last_name );

        final TextWatcher watcher = new TextWatcher() {

            @Override
            public void beforeTextChanged( final CharSequence s,
                                           final int start,
                                           final int count,
                                           final int after ) {
                // nothing to do
            }

            @Override
            public void onTextChanged( final CharSequence s,
                                       final int start,
                                       final int before,
                                       final int count ) {
                // nothing to do
            }

            @Override
            public void afterTextChanged( final Editable s ) {
                final boolean enable = ( ( tvFirstName.getText().length() != 0 ) && ( tvLastName.getText().length() != 0 ) );

                if ( btnSearch.isEnabled() != enable ) {
                    btnSearch.setEnabled( enable );
                }
            }
        };

        // make sure there is a first and a last name
        tvFirstName.addTextChangedListener( watcher );
        tvLastName.addTextChangedListener( watcher );
    }

}
