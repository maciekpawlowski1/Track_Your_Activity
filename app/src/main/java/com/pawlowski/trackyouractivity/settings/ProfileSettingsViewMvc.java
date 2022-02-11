package com.pawlowski.trackyouractivity.settings;

import android.app.DatePickerDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pawlowski.trackyouractivity.R;
import com.pawlowski.trackyouractivity.base.BaseObservableViewMvc;

import java.util.Calendar;
import java.util.Objects;

import androidx.annotation.Nullable;

public class ProfileSettingsViewMvc extends BaseObservableViewMvc<ProfileSettingsViewMvc.ProfileSettingsButtonsClickListener> {

    private final SeekBar mSeekBar;
    private final EditText mSeekBarEditText;
    private final TextInputEditText mBirthdayInput;
    private final TextInputEditText mNameInput;
    private final TextInputEditText mWeightInput;
    private final TextInputLayout mBirthdayInputLayout;
    private final TextInputLayout mNameInputLayout;
    private final TextInputLayout mWeightInputLayout;
    private final ImageButton mCalendarButton;
    private final Button mSaveButton;

    public ProfileSettingsViewMvc(LayoutInflater inflater, ViewGroup container) {
        rootView = inflater.inflate(R.layout.fragment_profile_settings, container, false);
        mSeekBar = findViewById(R.id.seek_bar_profile_settings);
        mSeekBarEditText = findViewById(R.id.goal_seek_bar_text_profile_settings);
        mBirthdayInput = findViewById(R.id.birthday_input_edit_text_profile_settings);
        mCalendarButton = findViewById(R.id.calendar_button_profile_settings);
        mNameInput = findViewById(R.id.name_input_edit_text_profile_settings);
        mWeightInput = findViewById(R.id.weight_input_edit_text_profile_settings);
        mBirthdayInputLayout = findViewById(R.id.birthday_input_layout_profile_settings);
        mNameInputLayout = findViewById(R.id.name_input_layout_profile_settings);
        mWeightInputLayout = findViewById(R.id.weight_input_layout_profile_settings);
        mSaveButton = findViewById(R.id.save_button_profile_settings);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyListeners(null);
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mSeekBarEditText.setText(""+i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mCalendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCalendar();
            }
        });

        mNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mNameInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mBirthdayInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mBirthdayInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mWeightInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mWeightInputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mSeekBarEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSeekBarEditText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public String getDateOfBirth()
    {
        return Objects.requireNonNull(mBirthdayInput.getText()).toString();
    }

    public String getName()
    {
        return Objects.requireNonNull(mNameInput.getText()).toString();
    }

    public int getWeight()
    {
        String weightString = Objects.requireNonNull(mWeightInput.getText()).toString();
        if(weightString.length() > 0)
            return Integer.parseInt(weightString);
        else
            return -1;
    }

    public void setNameError(String errorText)
    {
        mNameInputLayout.setError(errorText);
    }

    public void setBirthdayError(String errorText)
    {
        mBirthdayInputLayout.setError(errorText);
    }

    public void setWeightError(String errorText)
    {
        mWeightInputLayout.setError(errorText);
    }

    public void setGoalError(String errorText)
    {
        mSeekBarEditText.setError(errorText);
    }

    public void openCalendar()
    {
        int year, month, day;
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(rootView.getContext(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String newDate = (dayOfMonth>9?dayOfMonth:"0"+dayOfMonth) + "-" + (monthOfYear+1>9?monthOfYear+1:"0"+(monthOfYear+1)) + "-" + year;
                        mBirthdayInput.setText(newDate);
                    }
                }, year, month, day);

        datePickerDialog.show();
    }

    @Override
    protected void notifyListeners(@Nullable Integer which) {
        for(ProfileSettingsButtonsClickListener l:listeners)
        {
            l.onSaveClick();
        }
    }

    public int getGoal()
    {
        String goalString = mSeekBarEditText.getText().toString();
        if(goalString.length() == 0)
            return -1;
        else
            return Integer.parseInt(goalString);
    }

    interface ProfileSettingsButtonsClickListener
    {
        void onSaveClick();
    }
}
