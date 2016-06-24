package com.redhat.healthcare;

import android.app.Application;
import android.content.Context;
import android.util.Log;

/**
 * The Healthcare Mobile App.
 */
public class HealthcareApp extends Application {

    private static final String LOG_MSG = ( "%s: %s: %s" );
    private static final String LOG_TAG = "healthcare";

    private static Context _context;

    /**
     * @return the app context (never <code>null</code>)
     */
    public static Context getContext() {
        return _context;
    }

    /**
     * @param clazz         the class logging the debug message (cannot be <code>null</code>)
     * @param methodContext the name of the method where the error is being logged (cannot be empty)
     * @param msg           the debug message (cannot be empty)
     */
    public static void logDebug( final Class< ? > clazz,
                                 final String methodContext,
                                 final String msg ) {
        Log.d( LOG_TAG, String.format( LOG_MSG, clazz.getSimpleName(), methodContext, msg ) );
    }

    /**
     * @param clazz         the class logging the error message (cannot be <code>null</code>)
     * @param methodContext the name of the method where the error is being logged (cannot be empty)
     * @param msg           the error message (can be empty)
     * @param e             the error (can be <code>null</code>)
     */
    public static void logError( final Class< ? > clazz,
                                 final String methodContext,
                                 final String msg,
                                 final Throwable e ) {
        final String errorMsg = ( ( msg == null ) ? "" : msg );
        Log.e( LOG_TAG, String.format( LOG_MSG, clazz.getSimpleName(), methodContext, errorMsg ), e );
    }

    /**
     * @param hostIpAddress the IP address of the host that is being checked (cannot be empty)
     * @return <code>true</code> if host is reachable
     */
    public static boolean ping( final String hostIpAddress ) {
        boolean reachable = false;

        try {
            String cmd;

            if ( System.getProperty( "os.name" ).startsWith( "Windows" ) ) {
                cmd = ( "ping -n 1 " + hostIpAddress );
            } else {
                cmd = ( "ping -c 1 " + hostIpAddress );
            }

            final Process myProcess = Runtime.getRuntime().exec( cmd );
            myProcess.waitFor();

            if ( myProcess.exitValue() == 0 ) {
                reachable = true;
            }
        } catch ( final Exception e ) {
            reachable = false;
        }

        return reachable;
    }

    /**
     * Must be called once at startup from the main activity.
     *
     * @param mainActivity the shared context used by the app (cannot be <code>null</code>)
     */
    public static void setContext( final Context mainActivity ) {
        if ( _context == null ) {
            _context = mainActivity;
        } else {
            logError( HealthcareApp.class, "setContext", "setting context more than once", null );
        }
    }

}
