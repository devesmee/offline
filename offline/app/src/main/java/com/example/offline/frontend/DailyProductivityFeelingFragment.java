package com.example.offline.frontend;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.offline.R;
import com.example.offline.backend.day.Day;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DailyProductivityFeelingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailyProductivityFeelingFragment extends Fragment {

    private TextView productivityFeelingTextView;
    private ImageView productivityFeelingImageView;
    private View currentView;
    Day selectedDay;

    public DailyProductivityFeelingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DailyProductivityFeelingFragment.
     */
    public static DailyProductivityFeelingFragment newInstance(String param1, String param2) {
        DailyProductivityFeelingFragment fragment = new DailyProductivityFeelingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        selectedDay = (Day) getArguments().getParcelable("selectedDay");
        currentView = inflater.inflate(R.layout.fragment_daily_productivity_feeling, container, false);

        // Set text of labels
        productivityFeelingTextView = (TextView) currentView.findViewById(R.id.ProductivityFeelingAverage);
        productivityFeelingImageView = (ImageView) currentView.findViewById(R.id.ProductivityFeelingEmoji);
        setProductivityFeeling();

        // Inflate the layout for this fragment
        return currentView;
    }

    public void setProductivityFeeling() {
        // change image based on the feeling
        switch (selectedDay.getProductivityFeeling()) {
            case NONE:
            case NEUTRAL:
                productivityFeelingImageView.setImageResource(R.drawable.neutral);
                productivityFeelingTextView.setText(R.string.productivity_feeling_neutral);
                break;
            case SAD:
                productivityFeelingImageView.setImageResource(R.drawable.sad);
                productivityFeelingTextView.setText(R.string.productivity_feeling_sad);
                break;
            case HAPPY:
                productivityFeelingImageView.setImageResource(R.drawable.happy);
                productivityFeelingTextView.setText(R.string.productivity_feeling_happy);
                break;
        }
    }

}