package com.redhat.healthcare;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents the current chart of a patient.
 */
final class PatientChart {

    interface JsonName {

        String DIASTOLIC = "dia";
        String FIRST_NAME = "firstName";
        String HIGH_BP_HISTORY = "highBpHistory";
        String INTERACTION = "interaction";
        String LAST_NAME = "lastName";
        String MEDICATION = "medication";
        String RESULTS = "results";
        String SIDE_EFFECTS = "sideeffects";
        String SYSTOLIC = "sys";
        String WARNING = "warning";

    }

    private final int diastolic;
    private final String firstName;
    private final boolean highBpHistory;
    private final String interactions;
    private final String lastName;
    private final String medication;
    private final String resultsMessage;
    private final String sideEffects;
    private final int systolic;
    private final String warningMessage;

    public PatientChart( final String firstName,
                         final String lastName,
                         final boolean highBpHistory,
                         final int currentSys,
                         final int currentDys,
                         final String medication,
                         final String interactions,
                         final String sideEffects,
                         final String resultsMessage,
                         final String warningMessage ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.highBpHistory = highBpHistory;
        this.systolic = currentSys;
        this.diastolic = currentDys;
        this.medication = medication;
        this.interactions = interactions;
        this.sideEffects = sideEffects;
        this.resultsMessage = resultsMessage;
        this.warningMessage = warningMessage;
    }

    public PatientChart( final String json ) throws JSONException {
        /*
        final String input = "{\n" +
            "d: {\n" +
            "results: [\n" +
            "{\n" +
            "__metadata: {\n" +
            "uri: \"http://127.0.0.1:8080/odata/Alerts/Alerts.messages(firstName='RICHARD',lastName='HOWATT')\",\n" +
            "type: \"Alerts.messages\"\n" +
            "},\n" +
            "firstName: \"RICHARD\",\n" +
            "lastName: \"HOWATT\",\n" +
            "medication: \"Clorpres oral\",\n" +
            "highBpHistory: \"false\",\n" +
            "sys: \"107\",\n" +
            "dia: \"60\",\n" +
            "results: \"Low BP\",\n" +
            "warning: \"Check medication dosage and interactions\",\n" +
            "interaction: \"Amoxapine decreases effects of clonidine. Avoid or use alternate drug.\",\n" +
            "sideeffects: \"dizziness, nausea, drowsiness, rash\"\n" +
            "}\n" +
            "]\n" +
            "}\n" +
            "}";
         */
        final JSONObject chart = new JSONObject( json );

        // required
        this.firstName = chart.getString( JsonName.FIRST_NAME );
        this.lastName = chart.getString( JsonName.LAST_NAME );

        // optional
        this.highBpHistory = ( chart.has( JsonName.HIGH_BP_HISTORY ) ? chart.getBoolean( JsonName.HIGH_BP_HISTORY ) : false );
        this.diastolic = ( chart.has( JsonName.DIASTOLIC ) ? chart.getInt( JsonName.DIASTOLIC ) : 0 );
        this.interactions = ( chart.has( JsonName.INTERACTION ) ? chart.getString( JsonName.INTERACTION ) : "" );
        this.medication = ( chart.has( JsonName.MEDICATION ) ? chart.getString( JsonName.MEDICATION ) : "" );
        this.resultsMessage = ( chart.has( JsonName.RESULTS ) ? chart.getString( JsonName.RESULTS ) : "" );
        this.sideEffects = ( chart.has( JsonName.SIDE_EFFECTS ) ? chart.getString( JsonName.SIDE_EFFECTS ) : "" );
        this.systolic = ( chart.has( JsonName.SYSTOLIC ) ? chart.getInt( JsonName.SYSTOLIC ) : 0 );
        this.warningMessage = ( chart.has( JsonName.WARNING ) ? chart.getString( JsonName.WARNING ) : "" );
    }

    public int getDiastolic() {
        return this.diastolic;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public boolean isHighBpHistory() {
        return this.highBpHistory;
    }

    public String getInteractions() {
        return this.interactions;
    }

    public String getLastName() {
        return this.lastName;
    }

    public String getMedication() {
        return this.medication;
    }

    public String getResultsMessage() {
        return this.resultsMessage;
    }

    public String getSideEffects() {
        return this.sideEffects;
    }

    public int getSystolic() {
        return this.systolic;
    }

    public String getWarningMessage() {
        return this.warningMessage;
    }

}
