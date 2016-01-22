package dg.shenm233.mmaps.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.services.busline.BusLineItem;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusStep;
import com.amap.api.services.route.RouteBusWalkItem;

import java.util.ArrayList;
import java.util.List;

import dg.shenm233.mmaps.R;
import dg.shenm233.mmaps.model.MyPath;
import dg.shenm233.mmaps.ui.widget.BusPathView;
import dg.shenm233.mmaps.util.CommonUtils;

public class BusRouteListAdapter extends BaseRecyclerViewAdapter<BusRouteListAdapter.BusRouteView> {
    private Context mContext;
    private List<BusPath> mBusPaths = new ArrayList<>();
    private List<String> mBusSimplePaths = new ArrayList<>();

    private LatLonPoint startLatLonPoint; // 出发点的经纬位置
    private LatLonPoint endLatLonPoint; // 终点的经纬位置

    public BusRouteListAdapter(Context context) {
        super();
        mContext = context;
    }

    public void newRouteList(List<BusPath> busPaths, LatLonPoint startPoint, LatLonPoint endPoint) {
        mBusPaths = busPaths;
        startLatLonPoint = startPoint;
        endLatLonPoint = endPoint;
        createSimpleList();
        notifyDataSetChanged();
    }

    public void clearAllData() {
        startLatLonPoint = null;
        endLatLonPoint = null;
        mBusPaths.clear();
        mBusSimplePaths.clear();
        notifyDataSetChanged();
    }

    private void createSimpleList() {
        mBusSimplePaths.clear();
        StringBuilder sb = new StringBuilder();
        for (BusPath busPath : mBusPaths) {
            int sbLength = sb.length();
            if (sbLength > 0)
                sb.delete(0, sbLength - 1);
            List<BusStep> busSteps = busPath.getSteps();
            for (BusStep busStep : busSteps) {
                BusLineItem busLineItem = busStep.getBusLine();
                if (busLineItem != null) {
                    sb.append(busLineItem.getBusLineName().split("\\([^)]*\\)")[0]) // 匹配括号外第一个元素
                            .append(" > ");
                } else {
                    RouteBusWalkItem busWalkItem = busStep.getWalk();
                    if (busWalkItem != null) {
                        sb.append("W")
                                .append(CommonUtils.getFriendlyDuration(mContext, busWalkItem.getDuration()))
                                .append(" > ");
                    }
                }
            }
            mBusSimplePaths.add(sb.toString());
        }
    }

    public MyPath getBusPath(int position) {
        if (mBusPaths == null)
            return null;
        else
            return new MyPath(mBusPaths.get(position), startLatLonPoint, endLatLonPoint);
    }

    @Override
    public BusRouteView onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup routeBusItem = (ViewGroup) LayoutInflater.from(mContext)
                .inflate(R.layout.route_bus_item, parent, false); // 需要parent原因：使item的match_parent生效
        return new BusRouteView(routeBusItem, adapterListener);
    }

    @Override
    public void onBindViewHolder(BusRouteView holder, int position) {
        holder.timeTextView.setText(CommonUtils.getFriendlyDuration(mContext,
                mBusPaths.get(position).getDuration()));
        holder.pathView.setBusPath(mBusSimplePaths.get(position));
    }

    @Override
    public int getItemCount() {
        if (mBusPaths == null) return 0;
        return mBusPaths.size();
    }

    protected static class BusRouteView extends BaseRecyclerViewAdapter.BaseViewHolder {
        protected BusPathView pathView;
        protected TextView timeTextView;

        public BusRouteView(ViewGroup itemView, OnItemClickListener l) {
            super(itemView, l);
            pathView = (BusPathView) itemView.findViewById(R.id.route_bus_path);
            timeTextView = (TextView) itemView.findViewById(R.id.route_bus_time);
        }
    }
}