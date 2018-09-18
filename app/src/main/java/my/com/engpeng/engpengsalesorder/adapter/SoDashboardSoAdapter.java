package my.com.engpeng.engpengsalesorder.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.chip.Chip;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.database.salesorder.SoDisplay;

import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_CONFIRM;
import static my.com.engpeng.engpengsalesorder.Global.SO_STATUS_DRAFT;

public class SoDashboardSoAdapter extends RecyclerView.Adapter<SoDashboardSoAdapter.SoViewHolder> {

    private Context context;
    private List<SoDisplay> soDisplayList;
    private SoDashboardSoAdapterListener sdsaListener;

    public interface SoDashboardSoAdapterListener {
        void onSoSelected(long id);
    }

    public SoDashboardSoAdapter(Context context, SoDashboardSoAdapterListener sdsaListener) {
        this.context = context;
        this.sdsaListener = sdsaListener;
    }

    @NonNull
    @Override
    public SoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_so_dashboard_so, viewGroup, false);
        return new SoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoViewHolder soViewHolder, int i) {
        final SoDisplay item = soDisplayList.get(i);
        String title = item.getRunningNo();
        if (item.getRunningNo() == null) {
            title = item.getStatus();
        } else if (item.getRunningNo().equals("")) {
            title = item.getStatus();
        }
        soViewHolder.tvTitle.setText(title);

        soViewHolder.tvCustomer.setText(item.getCustomerCompanyName());
        soViewHolder.tvAddress.setText(item.getCustomerAddressName());
        soViewHolder.tvLpo.setText(item.getLpo());
        soViewHolder.tvRemark.setText(item.getRemark());
        soViewHolder.tvCreateDate.setText(item.getCreateDatetime());
        soViewHolder.cpDeliveryDate.setText(item.getDeliveryDate());

        //TODO set ivUpload

        if(item.getStatus().equals(SO_STATUS_DRAFT)){
            soViewHolder.ivIcon.setImageResource(R.drawable.ic_baseline_drafts_24px);
        }else if(item.getStatus().equals(SO_STATUS_CONFIRM)){
            soViewHolder.ivIcon.setImageResource(R.drawable.ic_baseline_done_all_24px);
        }else{
            soViewHolder.ivIcon.setImageResource(R.drawable.ic_baseline_error_24px);
        }

        soViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sdsaListener.onSoSelected(item.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (soDisplayList == null) {
            return 0;
        }
        return soDisplayList.size();
    }

    public void setList(List<SoDisplay> soDisplayList) {
        this.soDisplayList = soDisplayList;
        notifyDataSetChanged();
    }

    class SoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCustomer, tvAddress, tvLpo, tvRemark, tvCreateDate;
        Chip cpDeliveryDate;
        ImageView ivIcon, ivUpload;

        public SoViewHolder(View view) {
            super(view);
            ivIcon = view.findViewById(R.id.li_iv_icon);
            tvTitle = view.findViewById(R.id.li_tv_running_no);
            tvCustomer = view.findViewById(R.id.li_tv_customer);
            tvAddress = view.findViewById(R.id.li_tv_address);
            tvLpo = view.findViewById(R.id.li_tv_lpo);
            tvRemark = view.findViewById(R.id.li_tv_remark);
            cpDeliveryDate = view.findViewById(R.id.li_cp_delivery_date);
            ivUpload = view.findViewById(R.id.li_iv_upload);
            tvCreateDate = view.findViewById(R.id.li_tv_create_date);
        }
    }
}
