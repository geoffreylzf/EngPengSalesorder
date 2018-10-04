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
import my.com.engpeng.engpengsalesorder.database.log.LogEntry;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private Context context;
    private List<LogEntry> logEntryList;

    public LogAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.list_item_two_line, viewGroup, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder logViewHolder, int i) {
        final LogEntry log = logEntryList.get(i);
        logViewHolder.tvRemark.setText(log.getRemark());
        logViewHolder.tvDatetime.setText(log.getDatetime());
    }

    @Override
    public int getItemCount() {
        if (logEntryList == null) {
            return 0;
        }
        return logEntryList.size();
    }

    public void setList(List<LogEntry> logEntryList) {
        this.logEntryList = logEntryList;
        notifyDataSetChanged();
    }

    class LogViewHolder extends RecyclerView.ViewHolder {

        TextView tvRemark, tvDatetime;

        public LogViewHolder(@NonNull View view) {
            super(view);
            tvRemark = view.findViewById(R.id.li_tv_primary);
            tvDatetime = view.findViewById(R.id.li_tv_secondary);
        }
    }
}
