package tw.chiae.inlive.presentation.ui.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;
import tw.chiae.inlive.BeautyLiveApplication;
import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.HotAreaBean;
import tw.chiae.inlive.data.bean.ThemBean;
import tw.chiae.inlive.data.bean.me.UserInfo;
import tw.chiae.inlive.domain.LocalDataManager;
import tw.chiae.inlive.presentation.ui.base.BaseFragment;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerAdapter;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerHolder;
import tw.chiae.inlive.util.Const;
import tw.chiae.inlive.util.L;
import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.RequestMethod;
import com.yolanda.nohttp.rest.OnResponseListener;
import com.yolanda.nohttp.rest.Request;
import com.yolanda.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

public class BlankFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private String sex;
    private final static String ARG_UID = "sex";
    private List<HotAreaBean> list;
    private Gson gson;
    private BackCtiy mBackCtiy;
    private PlaceRecycleAdapter placeRecycleAdapter;
    public void setSex(String sex) {
        this.sex = sex;
    }
    private int chackitem=-1;
    private IndexHotListActivity indexHotListActivity;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_blank;
    }

    @Override
    protected void initViews(View view) {
        gson=new Gson();
        getHostListJson(LocalDataManager.getInstance().getLoginInfo().getToken(),sex);
        list=new ArrayList<>();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.index_hot_area);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        adapter=new ThemAdapter(list);
//        mRecyclerView.setAdapter(adapter);
        placeRecycleAdapter =new PlaceRecycleAdapter(getActivity(),list);
        mRecyclerView.setAdapter(placeRecycleAdapter);
        placeRecycleAdapter.setOnItemClickLitener(new OnItemClickLitener() {
            @Override
            public void onItemClick(PlaceRecycleAdapter.Holder view, int position) {
                mBackCtiy.back(list.get(position).getCity(), sex);
            }
        });
//        if (indexHotListActivity!=null){
//            indexHotListActivity.setHotviewpager(new IndexHotListActivity.Hotviewpager() {
//                @Override
//                public void finsh() {
////                    这里是回调接口，切换fragment的时候原来的fragment的Recycleview的选中被取消
//                }
//            });
//        }
    }

    public void finshiRecyView(){
        placeRecycleAdapter.clearImg();
    }
//    public void setIndexHotListActivity(IndexHotListActivity indexHotListActivity) {
//        this.indexHotListActivity = indexHotListActivity;
//    }

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                placeRecycleAdapter.setDataList(list);
            }
        }
    };

    int PAGER_JSON = 1;
    //0男 1女
    public void getHostListJson(String token,String sex) {
        Request<JSONObject> request = NoHttp.createJsonObjectRequest(Const.MAIN_HOST_URL + "/OpenAPI/V1/Anchor/getHotList", RequestMethod.POST);
        request.add("token",token);
        request.add("sex",sex);
        BeautyLiveApplication.getRequestQueue().add(PAGER_JSON, request, ViewPagerOnResponse);
    }

    private OnResponseListener<JSONObject> ViewPagerOnResponse = new OnResponseListener<JSONObject>() {
        @Override
        public void onStart(int i) {
        }

        @Override
        public void onSucceed(int i, Response<JSONObject> response) {
            if (i == PAGER_JSON) {// 判断what是否是刚才指定的请求
                //                {"code":0,"msg":"ok","data":"320200"}这个是什么
                // 请求成功
                JSONObject result = response.get();// 响应结果
                try {
                    JSONArray json=result.getJSONArray("data");
                    for (int s=0;s<json.length();s++){
                        HotAreaBean bean=new HotAreaBean();
                        bean.setAnchorcnt(json.getJSONObject(s).getString("anchorcnt"));
                        bean.setCity(json.getJSONObject(s).getString("province"));
                        list.add(bean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(1);
                // 响应头
                Headers headers = response.getHeaders();
                headers.getResponseCode();// 响应码
                response.getNetworkMillis();// 请求花费的时间
            }
        }

        @Override
        public void onFailed(int i, String s, Object o, Exception e, int i1, long l) {
        }

        @Override
        public void onFinish(int i) {
        }
    };

    @Override
    public void setCoinData(int currencyItemSum) {

    }

    @Override
    public void onCancelDialogcheck(Bundle mArgs) {

    }

    public class PlaceRecycleAdapter extends RecyclerView.Adapter<PlaceRecycleAdapter.Holder>{
        private int layoutbgpreead,layoutbgup;
        private Context context;
        private List<HotAreaBean> list;
        private LayoutInflater mInflater;
        private HashMap<Integer,Holder> itemintlist;
        public PlaceRecycleAdapter(Context context, List<HotAreaBean> placelist) {
            this.context = context;
            this.list = placelist;
            mInflater = LayoutInflater.from(context);
            itemintlist=new HashMap<Integer,Holder>();
        }

        public Holder viewHolder;

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
//        找到我们的item布局
            View view = mInflater.inflate(R.layout.index_popu_recy_item,parent,false);
//        实例化一个Myviewholder对象 ,并传入我们的item布局
            viewHolder  = new Holder(view);
            return viewHolder;
        }

        public void clearImg(){
            if (itemintlist.get(chackitem)!=null) {
                itemintlist.get(chackitem).mImg.setVisibility(View.INVISIBLE);
                chackitem = -1;
            }
        }
        @Override
        public void onBindViewHolder(final Holder holder, final int position) {
            itemintlist.put(position,holder);
            holder.mTitile.setText(list.get(position).getCity());
            holder.mNumber.setText(list.get(position).getAnchorcnt() + getString(R.string.unit_watching));
            if (chackitem==position) {
                holder.mImg.setVisibility(View.VISIBLE);
            }else if (chackitem>=0){
                holder.mImg.setVisibility(View.INVISIBLE);
            }
            // 如果设置了回调，则设置点击事件
            if (mOnItemClickLitener != null)
            {
//          这里item设置我们系统的点击事件
                holder.itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
//                    然后这里设置一个int 用来接受当前点击的是 myViewHolder的哪个layout
                        int pos = holder.getLayoutPosition();
//                    如果老的chack！=当前点的pos，那么就设置老的holder图片为空
                        if (chackitem!=pos&&chackitem>=0){
                            itemintlist.get(chackitem).mImg.setVisibility(View.INVISIBLE);
                        }
                        holder.mImg.setVisibility(View.VISIBLE);
                        chackitem=pos;
//                    然后这里调用接口里的点击抽象方法，并传入 View类型的参数 和当前点击的是哪个
                        if (mOnItemClickLitener!=null) {
                            mOnItemClickLitener.onItemClick(holder, pos);
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            if (list==null){
                return 0;
            }
            return list.size();
        }

        public void setDataList(List<HotAreaBean> placelist) {
            this.list = placelist;
            notifyDataSetChanged();
        }

        //    ========================1=========================点击监听 需要我们自己来写，但是依然需要用到系统的点击回调

        private OnItemClickLitener mOnItemClickLitener;
        //   提供一个公开的方法用来进行回调，参数就是我们的回调接口，让需要调用的Activity来实现这个接口里的俩个抽象方法
        public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
        {
            this.mOnItemClickLitener = mOnItemClickLitener;
        }


        //    内部类holder
        public class Holder extends RecyclerView.ViewHolder {

            private TextView mTitile, mNumber;
            private ImageView mImg;
            public Holder(View itemView) {
                super(itemView);
                mTitile = (TextView) itemView.findViewById(R.id.index_recy_title);
                mNumber = (TextView) itemView.findViewById(R.id.index_recy_number);
                mImg = (ImageView) itemView.findViewById(R.id.index_recy_img);
            }
        }
    }

    //    监听依旧需要一个回调接口
    public interface OnItemClickLitener {
        void onItemClick(PlaceRecycleAdapter.Holder view, int position);
    }

    interface BackCtiy{
        void back(String ctiy,String sex);
    }

    public void setmBackCtiy(BackCtiy mBackCtiy) {
        this.mBackCtiy = mBackCtiy;
    }
}
