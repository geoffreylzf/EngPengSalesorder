package my.com.engpeng.engpengsalesorder.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.database.customerCompanyAddress.CustomerCompanyAddressEntry;

public class AddressSelectionAdapter extends RecyclerView.Adapter<AddressSelectionAdapter.CustomerAddressViewHolder>{

    private Context context;
    private List<CustomerCompanyAddressEntry> customerCompanyAddressEntryList;
    private AddressSelectionAdapterListener asaListener;

    public interface AddressSelectionAdapterListener {
        void onAddressSelected(Long id);
    }

    public AddressSelectionAdapter(Context context, AddressSelectionAdapterListener asaListener) {
        this.context = context;
        this.asaListener = asaListener;
    }

    @NonNull
    @Override
    public CustomerAddressViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_two_line, viewGroup, false);
        return new CustomerAddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerAddressViewHolder customerAddressViewHolder, int i) {
        final CustomerCompanyAddressEntry cca = customerCompanyAddressEntryList.get(i);
        customerAddressViewHolder.tvCode.setText(cca.getPersonCustomerAddressCode());
        customerAddressViewHolder.tvName.setText(cca.getPersonCustomerAddressName());

        customerAddressViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asaListener.onAddressSelected(cca.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (customerCompanyAddressEntryList == null) {
            return 0;
        }
        return customerCompanyAddressEntryList.size();
    }

    public void setCustomerCompanyEntryList(List<CustomerCompanyAddressEntry> customerCompanyAddressEntryList) {
        this.customerCompanyAddressEntryList = customerCompanyAddressEntryList;
        notifyDataSetChanged();
    }

    class CustomerAddressViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvCode;

        public CustomerAddressViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.li_tv_primary);
            tvCode = view.findViewById(R.id.li_tv_secondary);
        }
    }
}
