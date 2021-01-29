package com.example.mareu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mareu.databinding.ActivityMeetingAddBinding;
import com.example.mareu.generator.GenerateMeetingList;
import com.example.mareu.model.Meeting;
import com.google.android.material.chip.ChipDrawable;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class MeetingAddActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private ActivityMeetingAddBinding mBinding;
    private DatePickerDialog mDatePickerDialog;
    private TimePickerDialog mTimePickerDialog;
    private TimePicker mTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMeetingAddBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        setContentView(view);
        getSupportActionBar().setTitle(getString(R.string.actionbar_title_meeting_add));

        setSpinner();
        setDatePickerDialog();
        setTimePickerDialog();
        setChip();
        mBinding.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker datePicker = mDatePickerDialog.getDatePicker();
                Meeting meeting = new Meeting(
                        String.format("%02d/%02d/%04d", datePicker.getDayOfMonth(), datePicker.getMonth()+1, datePicker.getYear()),
                        String.format("%02dh%02d", mTimePicker != null ? mTimePicker.getCurrentHour() : getResources().getInteger(R.integer.default_hour), mTimePicker != null ? mTimePicker.getCurrentMinute() : getResources().getInteger(R.integer.default_minute)),
                        String.format("%d", mBinding.meetingRoom.getSelectedItemPosition()+1),
                        mBinding.meetingSubject.getText().toString(),
                        GenerateMeetingList.getRandomColor(),
                        Arrays.asList(mBinding.meetingParticipants.getText().toString())
                );
                Toast.makeText(MeetingAddActivity.this.getApplicationContext(), getString(R.string.add_meeting_toast_create), Toast.LENGTH_SHORT).show();

                Intent data = new Intent();
                data.putExtra("meeting", meeting);
                setResult(MeetingListActivity.RETURN_CODE_MEETING_CREATED, data);
                finish();
            }
        });
        mBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeetingAddActivity.this.finish();
                Toast.makeText(MeetingAddActivity.this.getApplicationContext(), MeetingAddActivity.this.getString(R.string.add_meeting_toast_cancel), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setChip() {
        mBinding.meetingParticipants.addTextChangedListener(new TextWatcher() {
            private int SpannedLength = 0, chipLength = 4;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == SpannedLength - chipLength) {
                    SpannedLength = charSequence.length();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().charAt(editable.length() - 1) == ' ' || editable.toString().charAt(editable.length() - 1) == '\n') {
                    ChipDrawable chip = ChipDrawable.createFromResource(MeetingAddActivity.this, R.xml.email_chip);
                    chip.setText(editable.subSequence(SpannedLength, editable.length() - 1));
                    chip.setBounds(0, 0, chip.getIntrinsicWidth(), chip.getIntrinsicHeight());
                    ImageSpan span = new ImageSpan(chip);
                    editable.setSpan(span, SpannedLength, editable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannedLength = editable.length();
                }

            }
        });
    }

    private void setDatePickerDialog() {
        Calendar c = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(this, MeetingAddActivity.this,
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );
        mDatePickerDialog.setCancelable(false);
        setDate(mDatePickerDialog.getDatePicker());
        mBinding.meetingDate.setOnClickListener(v -> {
            mDatePickerDialog.show();
        });
    }

    private void setTimePickerDialog() {
        mTimePickerDialog = new TimePickerDialog(this, MeetingAddActivity.this, getResources().getInteger(R.integer.default_hour), getResources().getInteger(R.integer.default_minute), true);
        mTimePickerDialog.setCancelable(false);
        setTime(getResources().getInteger(R.integer.default_hour), getResources().getInteger(R.integer.default_minute));
        mBinding.meetingTime.setOnClickListener(v -> {
            mTimePickerDialog.show();
        });
    }

    private void setDate(DatePicker date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(date.getYear(), date.getMonth(), date.getDayOfMonth());
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE d MMMM yyyy", Locale.FRANCE);
        String output = formatter.format(calendar.getTime());
        mBinding.meetingDate.setText(output);
    }

    private void setTime(int hour, int minute) {
        mBinding.meetingTime.setText(String.format("%02d h %02d", hour, minute));
    }

    private void setSpinner() {
        Spinner spinner = mBinding.meetingRoom;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.meeting_room_names_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        setDate(view);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (mTimePicker == null)
            mTimePicker = view;

        setTime(view.getCurrentHour(), view.getCurrentMinute());
    }
}