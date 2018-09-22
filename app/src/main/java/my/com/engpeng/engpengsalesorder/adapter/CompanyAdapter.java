package my.com.engpeng.engpengsalesorder.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.database.branch.BranchEntry;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder> {

    private Context context;
    private List<BranchEntry> branchEntryList;
    private CompanyAdapterListener caListener;

    public interface CompanyAdapterListener{
        void afterCompanySelected(long companyId);
    }

    public CompanyAdapter(Context context, CompanyAdapterListener caListener) {
        this.context = context;
        this.caListener = caListener;
    }

    @NonNull
    @Override
    public CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_company, viewGroup, false);
        return new CompanyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyViewHolder companyViewHolder, int i) {
        final BranchEntry b = branchEntryList.get(i);

        companyViewHolder.tvCode.setText(b.getBranchShortName());
        companyViewHolder.tvName.setText(b.getBranchName());

        companyViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                caListener.afterCompanySelected(b.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (branchEntryList == null) {
            return 0;
        }
        return branchEntryList.size();
    }

    public void setList(List<BranchEntry> branchEntryList){
        this.branchEntryList = branchEntryList;
        notifyDataSetChanged();
    }

    class CompanyViewHolder extends RecyclerView.ViewHolder {

        TextView tvCode, tvName;

        public CompanyViewHolder(@NonNull View view) {
            super(view);

            tvCode = view.findViewById(R.id.li_tv_company_code);
            tvName = view.findViewById(R.id.li_tv_company_name);
        }
    }
}
