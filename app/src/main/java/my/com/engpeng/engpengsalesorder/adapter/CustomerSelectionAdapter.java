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
import my.com.engpeng.engpengsalesorder.database.customerCompany.CustomerCompanyEntry;

public class CustomerSelectionAdapter extends RecyclerView.Adapter<CustomerSelectionAdapter.CustomerCompanyViewHolder> {

    private Context context;
    private List<CustomerCompanyEntry> customerCompanyEntryList;
    private CustomerSelectionAdapterListener csaListener;

    public interface CustomerSelectionAdapterListener {
        void onCustomerSelected(Long id);
    }

    public CustomerSelectionAdapter(Context context, CustomerSelectionAdapterListener csaListener) {
        this.context = context;
        this.csaListener = csaListener;
    }

    @NonNull
    @Override
    public CustomerCompanyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_two_line, viewGroup, false);
        return new CustomerCompanyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerCompanyViewHolder customerCompanyViewHolder, int i) {
        final CustomerCompanyEntry cc = customerCompanyEntryList.get(i);
        customerCompanyViewHolder.tvCode.setText(cc.getPersonCustomerCompanyCode());
        customerCompanyViewHolder.tvName.setText(cc.getPersonCustomerCompanyName());

        customerCompanyViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                csaListener.onCustomerSelected(cc.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (customerCompanyEntryList == null) {
            return 0;
        }
        return customerCompanyEntryList.size();
    }

    public void setCustomerCompanyEntryList(List<CustomerCompanyEntry> customerCompanyEntryList) {
        this.customerCompanyEntryList = customerCompanyEntryList;
        notifyDataSetChanged();
    }

    class CustomerCompanyViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvCode;

        public CustomerCompanyViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.li_tv_primary);
            tvCode = view.findViewById(R.id.li_tv_secondary);
        }
    }
}
