package my.com.engpeng.engpengsalesorder.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.chip.Chip;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import my.com.engpeng.engpengsalesorder.R;
import my.com.engpeng.engpengsalesorder.database.salesorder.SoDisplay;
import my.com.engpeng.engpengsalesorder.utilities.StringUtils;

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
        } else {
            title = StringUtils.getDisplayRunningNo(title);
        }

        soViewHolder.tvTitle.setText(title + " (" + item.getCreateDatetime() + ")");

        soViewHolder.tvCustomer.setText(item.getCustomerCompanyName());
        soViewHolder.tvAddress.setText(item.getCustomerAddressName());
        soViewHolder.cpDeliveryDate.setText(item.getDeliveryDate());
        soViewHolder.cpCount.setText(String.valueOf(item.getCount()));

        if (item.getIsUpload() == 1) {
            soViewHolder.ivUpload.setVisibility(View.VISIBLE);
        } else {
            //TODO soViewHolder.ivUpload.setVisibility(View.GONE);
        }

        if (item.getStatus().equals(SO_STATUS_DRAFT)) {
            soViewHolder.ivIcon.setImageResource(R.drawable.ic_baseline_drafts_24px);
            soViewHolder.btnAction.setText(context.getString(R.string.edit));
            soViewHolder.btnDelete.setVisibility(View.VISIBLE);
        } else if (item.getStatus().equals(SO_STATUS_CONFIRM)) {
            soViewHolder.ivIcon.setImageResource(R.drawable.ic_baseline_done_all_24px);
            soViewHolder.btnAction.setText(context.getString(R.string.view));
            soViewHolder.btnDelete.setVisibility(View.GONE);
        } else {
            soViewHolder.ivIcon.setImageResource(R.drawable.ic_baseline_error_24px);
            soViewHolder.btnDelete.setVisibility(View.GONE);
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
        TextView tvTitle, tvCustomer, tvAddress;
        Chip cpDeliveryDate, cpCount;
        ImageView ivIcon, ivUpload;
        Button btnAction, btnDelete;

        public SoViewHolder(View view) {
            super(view);
            ivIcon = view.findViewById(R.id.li_iv_icon);
            tvTitle = view.findViewById(R.id.li_tv_running_no);
            tvCustomer = view.findViewById(R.id.li_tv_customer);
            tvAddress = view.findViewById(R.id.li_tv_address);
            cpDeliveryDate = view.findViewById(R.id.li_cp_delivery_date);
            cpCount = view.findViewById(R.id.li_cp_count);
            ivUpload = view.findViewById(R.id.li_iv_upload);
            btnAction = view.findViewById(R.id.li_btn_action);
            btnDelete = view.findViewById(R.id.li_btn_delete);
        }
    }
}
