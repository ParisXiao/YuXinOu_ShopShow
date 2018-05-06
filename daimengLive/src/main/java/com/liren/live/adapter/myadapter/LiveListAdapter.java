package com.liren.live.adapter.myadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liren.live.R;
import com.liren.live.entity.LiveListEntity;
import com.liren.live.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 申诉记录适配器
 * Created by Administrator on 2017/12/12 0012.
 */

public class LiveListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {



    private LayoutInflater mInflater;
    private Context context;
    private List<LiveListEntity> list = new ArrayList<>();
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    //上拉加载更多
    public static final int PULLUP_LOAD_MORE = 0;
    //正在加载中
    public static final int LOADING_MORE = 1;
    //没有加载更多 隐藏
    public static final int NO_LOAD_MORE = 2;

    //上拉加载更多状态-默认为0
    private int mLoadMoreStatus = 0;


    public LiveListAdapter(Context context, List<LiveListEntity> list) {
        this.context = context;
        this.list = list;
        this.mInflater = LayoutInflater.from(context);


    }

    @Override
    public int getItemViewType(int position) {

        if (position + 1 == getItemCount()) {
            //最后一个item设置为footerView
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View itemView = mInflater.inflate(R.layout.item_livelist, parent, false);

            return new MyViewHolder(itemView);
        } else if (viewType == TYPE_FOOTER) {
            View itemView = mInflater.inflate(R.layout.recycleview_footview, parent, false);

            return new FooterViewHolder(itemView);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MyViewHolder) {
            ImageUtils.load(context,list.get(position).getIcon(),  ((MyViewHolder) holder).liveIcon);
            ((MyViewHolder) holder).liveName.setText(list.get(position).getName());
            ((MyViewHolder) holder).remark.setText(list.get(position).getRemark());
            ((MyViewHolder) holder).liveSpNum.setText(list.get(position).getNums()+"件");
            if (list.get(position).getWatch()!=null) {
                if (Integer.valueOf(list.get(position).getWatch())>10000) {
                    ((MyViewHolder) holder).liveWatch.setText((Integer.valueOf(list.get(position).getWatch())/10000)+"万人");
                }else {
                    ((MyViewHolder) holder).liveWatch.setText(list.get(position).getWatch()+"人");
                }
            }
            if (list.get(position).getImgs()!=null) {
                if (list.get(position).getImgs().size()>0){
                    if (list.get(position).getImgs().size()==1){
                        ImageUtils.load(context,list.get(position).getImgs().get(0),  ((MyViewHolder) holder).liveSp1);
                        ((MyViewHolder) holder).liveSp2.setVisibility(View.GONE);
                        ((MyViewHolder) holder).liveSp3.setVisibility(View.GONE);
                    }else  if (list.get(position).getImgs().size()==2){
                        ImageUtils.load(context,list.get(position).getImgs().get(0),  ((MyViewHolder) holder).liveSp1);
                        ImageUtils.load(context,list.get(position).getImgs().get(1),  ((MyViewHolder) holder).liveSp2);
                        ((MyViewHolder) holder).liveSp3.setVisibility(View.GONE);
                    }else  if (list.get(position).getImgs().size()>=3){
                        ImageUtils.load(context,list.get(position).getImgs().get(0),  ((MyViewHolder) holder).liveSp1);
                        ImageUtils.load(context,list.get(position).getImgs().get(1),  ((MyViewHolder) holder).liveSp2);
                        ImageUtils.load(context,list.get(position).getImgs().get(2),  ((MyViewHolder) holder).liveSp3);
                    }
                }
            }

            ((MyViewHolder) holder).itemView.setTag(position);

        } else if (holder instanceof FooterViewHolder) {


            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;


            switch (mLoadMoreStatus) {
                case PULLUP_LOAD_MORE:
                    footerViewHolder.mLoadLayout.setVisibility(View.VISIBLE);
                    footerViewHolder.mTvLoadText.setText("上拉加载更多...");
                    break;
                case LOADING_MORE:
                    footerViewHolder.mLoadLayout.setVisibility(View.VISIBLE);
                    footerViewHolder.mTvLoadText.setText("正在加载更多...");
                    break;
                case NO_LOAD_MORE:
                    //隐藏加载更多
                    footerViewHolder.mLoadLayout.setVisibility(View.GONE);
                    break;

            }

        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pbLoad)
        ProgressBar mPbLoad;
        @BindView(R.id.tvLoadText)
        TextView mTvLoadText;
        @BindView(R.id.loadLayout)
        LinearLayout mLoadLayout;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void AddHeaderItem(List<LiveListEntity> items) {
        list.addAll(0, items);
        notifyDataSetChanged();
    }

    public void AddFooterItem(List<LiveListEntity> items) {
        list.addAll(items);
        notifyDataSetChanged();
    }

    /**
     * 更新加载更多状态
     *
     * @param status
     */
    public void changeMoreStatus(int status) {
        mLoadMoreStatus = status;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.live_icon)
        ImageView liveIcon;
        @BindView(R.id.live_name)
        TextView liveName;
        @BindView(R.id.live_watch)
        TextView liveWatch;
        @BindView(R.id.remark)
        TextView remark;
        @BindView(R.id.live_sp1)
        ImageView liveSp1;
        @BindView(R.id.live_sp2)
        ImageView liveSp2;
        @BindView(R.id.live_sp3)
        ImageView liveSp3;
        @BindView(R.id.live_sp_num)
        TextView liveSpNum;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(LiveListAdapter.this);
        }

    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * item里面有多个控件可以点击
     */
    public enum ViewName {
        ITEM,
        Button
    }

    public interface OnRecyclerViewItemClickListener {
        void onClick(View view, ViewName viewName, int position);
    }

    @Override
    public void onClick(View v) {
        //注意这里使用getTag方法获取数据
        int position = (int) v.getTag();
        if (mOnItemClickListener != null) {
            switch (v.getId()) {
                default:
                    mOnItemClickListener.onClick(v, ViewName.ITEM, position);
                    break;
            }
        }
    }
}
