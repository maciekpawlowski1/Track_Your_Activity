package com.pawlowski.trackyouractivity.base;

import com.pawlowski.trackyouractivity.composition.ActivityCompositionRoot;
import com.pawlowski.trackyouractivity.composition.PresentationCompositionRoot;

import androidx.fragment.app.Fragment;

public class BaseFragment extends Fragment {
    private ActivityCompositionRoot compositionRoot;

    public PresentationCompositionRoot getCompositionRoot()
    {
        return new PresentationCompositionRoot(((BaseActivity)requireActivity()).getActivityCompositionRoot());
    }
}
