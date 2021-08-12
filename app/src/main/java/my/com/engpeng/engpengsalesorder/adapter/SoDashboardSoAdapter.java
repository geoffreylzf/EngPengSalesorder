package my.com.engpeng.engpengsalesorder.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.chip.Chip;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
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
        void onSoActionBtnClicked(long salesorderId);

        void onSoDeleteBtnClicked(long salesorderId, int deletePosition);
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
    public void onBindViewHolder(@NonNull final SoViewHolder soViewHolder, int i) {
        final SoDisplay item = soDisplayList.get(i);
        String title = item.getRunningNo();
        if (item.getRunningNo() == null) {
            title = item.getStatus();
        } else if (item.getRunningNo().equals("")) {
            title = item.getStatus();
        } else {
            title = StringUtils.getDisplayRunningNo(title);
        }

        soViewHolder.tvTitle.setText(title);
        soViewHolder.tvCustomer.setText(item.getCustomerCompanyName());
        soViewHolder.tvAddress.setText(item.getCustomerAddressName());
        soViewHolder.tvStore.setText(item.getStoreName());
        soViewHolder.cpDeliveryDate.setText(item.getDeliveryDate());
        soViewHolder.cpCount.setText(String.valueOf(item.getCount()));

        if (item.getIsUpload() == 1) {
            soViewHolder.ivUpload.setVisibility(View.VISIBLE);
        } else {
            soViewHolder.ivUpload.setVisibility(View.GONE);
        }

        if (item.getStatus().equals(SO_STATUS_DRAFT)) {
            soViewHolder.cv.setBackground(context.getDrawable(R.color.colorLime50));
            soViewHolder.ivIcon.setImageResource(R.drawable.ic_baseline_drafts_24px);
            soViewHolder.btnAction.setText(context.getString(R.string.edit));
            soViewHolder.btnDelete.setVisibility(View.VISIBLE);
        } else if (item.getStatus().equals(SO_STATUS_CONFIRM)) {
            soViewHolder.cv.setBackground(context.getDrawable(R.color.colorBackground));
            soViewHolder.ivIcon.setImageResource(R.drawable.ic_baseline_done_all_24px);
            soViewHolder.btnAction.setText(context.getString(R.string.view));
            soViewHolder.btnDelete.setVisibility(View.GONE);
        } else {
            soViewHolder.cv.setBackground(context.getDrawable(R.color.colorRed800));
            soViewHolder.ivIcon.setImageResource(R.drawable.ic_baseline_error_24px);
            soViewHolder.btnDelete.setVisibility(View.GONE);
        }

        soViewHolder.btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sdsaListener.onSoActionBtnClicked(item.getId());
            }
        });

        soViewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sdsaListener.onSoDeleteBtnClicked(item.getId(), soViewHolder.getAdapterPosition());
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

    public void setListAfterDelete(List<SoDisplay> soDisplayList, int position) {
        this.soDisplayList = soDisplayList;
        notifyItemRemoved(position);
        notifyItemRangeChanged(0, soDisplayList.size());
    }

    class SoViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCustomer, tvAddress, tvStore;
        Chip cpDeliveryDate, cpCount;
        ImageView ivIcon, ivUpload;
        Button btnAction, btnDelete;
        CardView cv;

        public SoViewHolder(View view) {
            super(view);
            cv = view.findViewById(R.id.li_cv);
            ivIcon = view.findViewById(R.id.li_iv_icon);
            tvTitle = view.findViewById(R.id.li_tv_running_no);
            tvCustomer = view.findViewById(R.id.li_tv_customer);
            tvAddress = view.findViewById(R.id.li_tv_address);
            tvStore = view.findViewById(R.id.li_tv_store);
            cpDeliveryDate = view.findViewById(R.id.li_cp_delivery_date);
            cpCount = view.findViewById(R.id.li_cp_count);
            ivUpload = view.findViewById(R.id.li_iv_upload);
            btnAction = view.findViewById(R.id.li_btn_action);
            btnDelete = view.findViewById(R.id.li_btn_delete);
        }
    }
}
