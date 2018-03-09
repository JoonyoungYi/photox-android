package kr.photox.android.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.photox.android.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginIntroFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static LoginIntroFragment newInstance(int sectionNumber) {
        LoginIntroFragment fragment = new LoginIntroFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public LoginIntroFragment() {
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;
        int section_number = getArguments().getInt(ARG_SECTION_NUMBER);

        if (section_number == 0) {
            rootView = inflater.inflate(R.layout.login_intro_fragment_0, container, false);

        } else if (section_number == 1) {
            rootView = inflater.inflate(R.layout.login_intro_fragment_1, container, false);

        } else {
            rootView = inflater.inflate(R.layout.login_intro_fragment_2, container, false);

        }

        return rootView;
    }
}

