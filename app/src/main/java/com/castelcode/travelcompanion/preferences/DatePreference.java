package com.castelcode.travelcompanion.preferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

import com.castelcode.travelcompanion.HomePage;
import com.castelcode.travelcompanion.R;
import com.castelcode.travelcompanion.utils.DateStringUtil;

public class DatePreference extends DialogPreference implements
        DatePicker.OnDateChangedListener {
    private String dateString;
    private String changedValueCanBeNull;
    private DatePicker datePicker;
    private Context mContext;

    @SuppressWarnings("unused")
    public DatePreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressWarnings("unused")
    public DatePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    /**
     * Produces a DatePicker set to the date produced by {@link #getDate()}. When
     * overriding be sure to call the super.
     *
     * @return a DatePicker with the date set
     */
    @Override
    protected View onCreateDialogView() {
        this.datePicker = new DatePicker(mContext);
        mContext.getTheme().applyStyle(R.style.customPicker, true);
        Calendar calendar = getDate();
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), this);
        return datePicker;
    }

    /**
     * Produces the date used for the date picker. If the user has not selected a
     * date, produces the default from the XML's android:defaultValue. If the
     * default is not set in the XML or if the XML's default is invalid it uses
     * the value produced by {@link #defaultCalendar()}.
     *
     * @return the Calendar for the date picker
     */
    private Calendar getDate() {
        try {
            Date date = DateStringUtil.formatter().parse(defaultValue());
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        } catch (java.text.ParseException e) {
            return defaultCalendar();
        }
    }

    private Calendar getDateSlashToDot() {
        try {
            Date date = DateStringUtil.formatter().parse(DateStringUtil.slashToDot(defaultValue()));
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        } catch (java.text.ParseException e) {
            return defaultCalendar();
        }
    }

    /**
     * Set the selected date to the specified string.
     *
     * @param dateString
     *          The date, represented as a string, in the format specified by
     *          {@link DateStringUtil#formatter()}.
     */
    private void setDate(String dateString) {
        this.dateString = dateString;
    }



    /**
     * Produces the date formatter used for showing the date in the summary. The default is MMMM dd, yyyy.
     * Override this to change it.
     *
     * @return the SimpleDateFormat used for summary dates
     */
    private static SimpleDateFormat summaryFormatter() {
        return new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    /**
     * Called when the date picker is shown or restored. If it's a restore it gets
     * the persisted value, otherwise it persists the value.
     */
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object def) {
        if (restoreValue) {
            this.dateString = getPersistedString(defaultValue());
            setTheDate(this.dateString);
        } else {
            boolean wasNull = this.dateString == null;
            setDate((String) def);
            if (!wasNull)
                persistDate(this.dateString);
        }
    }

    /**
     * Called when Android pauses the activity.
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        if (isPersistent())
            return super.onSaveInstanceState();
        else
            return new SavedState(super.onSaveInstanceState());
    }

    /**
     * Called when the user changes the date.
     */
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        Calendar selected = new GregorianCalendar(year, month, day);
        this.changedValueCanBeNull = DateStringUtil.formatter().format(selected.getTime());
        HomePage.cruise.setCruiseDateTime(selected);
    }

    /**
     * Called when the dialog is closed. If the close was by pressing "OK" it
     * saves the value.
     */
    @Override
    protected void onDialogClosed(boolean shouldSave) {
        if (shouldSave && this.changedValueCanBeNull != null) {
            setTheDate(this.changedValueCanBeNull);
            this.changedValueCanBeNull = null;
        }
        mContext.getTheme().applyStyle(R.style.prefScreen, true);
    }

    private void setTheDate(String s) {
        setDate(s);
        persistDate(s);
    }

    private void persistDate(String s) {
        persistString(s);
        setSummary(summaryFormatter().format(getDate().getTime()));
    }

    public void cruiseLoaded() {
        setSummary(summaryFormatter().format(getDateSlashToDot().getTime()));
    }

    public void setCruiseDate(String dateString) {
        this.dateString = dateString;
    }

    public void clearDate(){
        this.dateString = null;
        persistString(null);

    }

    /**
     * The default date to use when the XML does not set it or the XML has an
     * error.
     *
     * @return the Calendar set to the default date
     */
    private static Calendar defaultCalendar() {
        return Calendar.getInstance();
    }

    /**
     * The defaultCalendar() as a string using the {@link DateStringUtil#formatter()}.
     *
     * @return a String representation of the default date
     */
    private static String defaultCalendarString() {
        return DateStringUtil.formatter().format(defaultCalendar().getTime());
    }

    private String defaultValue() {
        if (this.dateString == null)
            setDate(defaultCalendarString());
        return this.dateString;

    }

    /**
     * Called whenever the user clicks on a button. Invokes {@link #onDateChanged(DatePicker, int, int, int)}
     * and {@link #onDialogClosed(boolean)}. Be sure to call the super when overriding.
     */
    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);
        datePicker.clearFocus();
        onDateChanged(datePicker, datePicker.getYear(), datePicker.getMonth(),
                datePicker.getDayOfMonth());
        onDialogClosed(which == DialogInterface.BUTTON_POSITIVE); // OK?
    }

    private static class SavedState extends BaseSavedState {
        String dateValue;

        private SavedState(Parcel p) {
            super(p);
            dateValue = p.readString();
        }

        private SavedState(Parcelable p) {
            super(p);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeString(dateValue);
        }

        @SuppressWarnings("unused")
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}