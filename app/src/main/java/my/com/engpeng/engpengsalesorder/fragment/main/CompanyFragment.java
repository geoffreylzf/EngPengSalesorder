package my.com.engpeng.engpengsalesorder.fragment.main;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.activity.NavigationHost;
import my.com.engpeng.engpengsalesorder.adapter.CompanyAdapter;
import my.com.engpeng.engpengsalesorder.database.AppDatabase;
import my.com.engpeng.engpengsalesorder.database.branch.BranchEntry;
import my.com.engpeng.engpengsalesorder.utilities.SharedPreferencesUtils;

public class CompanyFragment extends Fragment {

    public static final String tag = "MAIN_COMPANY_FRAGMENT";

    private RecyclerView rv;

    private AppDatabase mDb;
    private CompanyAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_m_company, container, false);

        rv = rootView.findViewById(R.id.m_company_rv);

        getActivity().setTitle("Select Company");
        mDb = AppDatabase.getInstance(getActivity().getApplicationContext());
        setupRecycleView();

        return rootView;
    }

    private void setupRecycleView() {
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new CompanyAdapter(getActivity(), new CompanyAdapter.CompanyAdapterListener() {
            @Override
            public void afterCompanySelected(long companyId) {
                SharedPreferencesUtils.saveCompanyId(getContext(), companyId);
                ((NavigationHost) getParentFragment()).navigateDefault();
            }
        });
        rv.setAdapter(adapter);

        final LiveData<List<BranchEntry>> ld = mDb.branchDao().loadLiveAllCompany();
        ld.observe(this, new Observer<List<BranchEntry>>() {
            @Override
            public void onChanged(@Nullable List<BranchEntry> branchEntryList) {
                LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getActivity(), R.anim.layout_animation_from_right);
                rv.setLayoutAnimation(controller);
                adapter.setList(branchEntryList);
            }
        });
    }
}
