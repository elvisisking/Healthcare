package com.redhat.healthcare;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * An asynchronous task whose result is a {@link PatientChart}.
 */
class GetData extends AsyncTask< Void, Void, PatientChart > {

    private static final String HOST = "10.0.2.2"; // when DV is running locally (use localhost in browser)
    private static final String PORT = "8080";
    private static final String PSWD = "4teiid$admin";
    private static final String USER = "teiidUser";

    protected static final String URL_PATTERN = "http://"
        + HOST
        + ':'
        + PORT
        + "/odata/Alerts/Alerts.messages(firstName='%s',lastName='%s')?$format=json";

    private final PatientChartCallback callback;
    private ProgressDialog dialog;
    protected Exception error;
    private String errorMsg;
    private final String pswd;
    private final String urlAsString;
    private final String user;

    /**
     * @param firstName               the first name of the patient whose chart is being requested (cannot be empty)
     * @param lastName                the last name of the patient whose chart is being requested (cannot be empty)
     * @param callback                the callback that is notified after task is finished  (cannot be <code>null</code>)
     * @param progressDialogMessageId the resource ID of the progress dialog message or -1 if no progress dialog should be shown
     */
    protected GetData( final String firstName,
                       final String lastName,
                       final PatientChartCallback callback,
                       final int progressDialogMessageId ) {
        this.urlAsString = String.format( URL_PATTERN, firstName, lastName );
        this.user = USER;
        this.pswd = PSWD;
        this.callback = callback;

        if ( progressDialogMessageId != -1 ) {
            this.dialog = new ProgressDialog( HealthcareApp.getContext() );
            this.dialog.setTitle( R.string.app_load_data_progress_title );
            this.dialog.setMessage( HealthcareApp.getContext().getString( progressDialogMessageId ) );
        }
    }

    private void dismissProgressDialog() {
        if ( ( this.dialog != null ) && this.dialog.isShowing() ) {
            this.dialog.dismiss();
        }
    }

    @Override
    protected PatientChart doInBackground( final Void... params ) {
        try {
            return executeHttpGet( this.urlAsString, this.user, this.pswd );
        } catch ( final Exception e ) {
            HealthcareApp.logError( GetData.class, "doInBackground", "url = '" + this.urlAsString + '\'', e );
            this.error = e;
            return null;
        }
    }

    private PatientChart executeHttpGet( final String urlAsString,
                                         final String user,
                                         final String pswd ) {
        boolean ok;
        String json;
        HttpURLConnection urlConnection = null;

        try {
            final URL url = new URL( urlAsString );
            final String userCredentials = ( user + ':' + pswd );
            final String encoding =
                new String( Base64.encode( userCredentials.getBytes(), Base64.DEFAULT ) ).replaceAll( "\\s+", "" );

            urlConnection = ( HttpURLConnection )url.openConnection();
            urlConnection.setRequestProperty( "Authorization", "Basic " + encoding );
            urlConnection.setRequestMethod( "GET" );
            urlConnection.setRequestProperty( "ACCEPT-LANGUAGE", "en-US,en;0.5" );

            final int code = urlConnection.getResponseCode();
            ok = ( code == HttpURLConnection.HTTP_OK );
            InputStream is;

            if ( ok ) {
                HealthcareApp.logDebug( GetData.class, "executeHttpGet", "HTTP GET SUCCESS for URL: " + urlAsString );
                is = urlConnection.getInputStream();
            } else {
                is = urlConnection.getErrorStream();
            }

            final BufferedReader reader = new BufferedReader( new InputStreamReader( is ) );
            final StringBuilder builder = new StringBuilder();
            String line;

            while ( ( line = reader.readLine() ) != null ) {
                builder.append( line ).append( "\n" );
            }

            reader.close();
            json = builder.toString();

            if ( ok ) {
                PatientChart chart = null;

                if ( ( json != null ) && !json.isEmpty() ) {
                    final JSONObject jobj = new JSONObject( json );
                    final JSONObject d = jobj.getJSONObject( "d" );
                    final JSONArray jArray = d.getJSONArray( "results" );
                    chart = new PatientChart( jArray.getJSONObject( 0 ).toString() ); // take first one
                }

                return chart;
            }

            // Not HTTP OK
            this.errorMsg = json;
        } catch ( final Exception e ) {
            HealthcareApp.logError( GetData.class, "executeHttpGet", "url = '" + urlAsString + '\'', e );
            this.error = e;
        } finally {
            if ( urlConnection != null ) {
                urlConnection.disconnect();
            }
        }

        return null;
    }

    /**
     * @return the error that occurred during processing (can be <code>null</code>)
     */
    public Exception getError() {
        return this.error;
    }

    /**
     * @return an error message (can be <code>null</code>)
     */
    public String getErrorMessage() {
        return this.errorMsg;
    }

    @Override
    protected void onCancelled( final PatientChart results ) {
        dismissProgressDialog();
    }

    @Override
    protected void onPreExecute() {
        if ( this.dialog != null ) {
            this.dialog.show();
        }
    }

    @Override
    protected void onPostExecute( final PatientChart results ) {
        dismissProgressDialog();

        if ( this.error != null ) {
            this.callback.onFailure( this.error );
        } else if ( this.errorMsg != null ) {
            this.callback.onFailure( this.errorMsg );
        } else {
            this.callback.onSuccess( results );
        }
    }

}
