package com.example.hospitals.utils;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.material.datepicker.CalendarConstraints;

import java.util.Calendar;
import java.util.HashSet;

/**
 * DateValidatorUnavailable implements CalendarConstraints.DateValidator.
 * It disables (makes non-selectable) any date that exists in the provided set
 * of disabled dates. Dates are normalized to midnight before comparison.
 */
public class DateValidatorUnavailable implements CalendarConstraints.DateValidator {

    // Set of disabled dates (timestamps normalized to midnight)
    private final HashSet<Long> disabledDates;

    /**
     * Constructor.
     *
     * @param disabledDates A set of timestamps (in milliseconds) representing dates to disable.
     */
    public DateValidatorUnavailable(HashSet<Long> disabledDates) {
        this.disabledDates = disabledDates;
    }

    /**
     * Checks if the provided date is valid (i.e. not in the disabled set).
     *
     * @param date The date in milliseconds.
     * @return True if the normalized date is not in the disabledDates set; false otherwise.
     */
    @Override
    public boolean isValid(long date) {
        // Normalize the given date to midnight
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long normalizedDate = cal.getTimeInMillis();

        // Return false if the normalized date is in the disabled set
        return !disabledDates.contains(normalizedDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Writes the disabledDates set to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Serialize the HashSet
        dest.writeSerializable(disabledDates);
    }

    // Parcelable.Creator to create instances of DateValidatorUnavailable from a Parcel.
    public static final Parcelable.Creator<DateValidatorUnavailable> CREATOR = new Parcelable.Creator<DateValidatorUnavailable>() {
        @Override
        public DateValidatorUnavailable createFromParcel(Parcel in) {
            // Read the serialized HashSet from the Parcel
            HashSet<Long> dates = (HashSet<Long>) in.readSerializable();
            return new DateValidatorUnavailable(dates);
        }

        @Override
        public DateValidatorUnavailable[] newArray(int size) {
            return new DateValidatorUnavailable[size];
        }
    };
}
