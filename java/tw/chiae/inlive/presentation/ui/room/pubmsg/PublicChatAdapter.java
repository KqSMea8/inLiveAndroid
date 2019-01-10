package tw.chiae.inlive.presentation.ui.room.pubmsg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import tw.chiae.inlive.R;
import tw.chiae.inlive.data.bean.websocket.LightHeartMsg;
import tw.chiae.inlive.data.bean.websocket.RoomPublicMsg;
import tw.chiae.inlive.data.bean.websocket.SendGiftMsg;
import tw.chiae.inlive.data.bean.websocket.SystemMsg;
import tw.chiae.inlive.data.bean.websocket.SystemWelcome;
import tw.chiae.inlive.data.bean.websocket.UserPublicMsg;
import tw.chiae.inlive.presentation.ui.base.recycler.SimpleRecyclerAdapter;

import java.util.List;

/**
 * @author Muyangmin
 * @since 1.0.0
 */
public class PublicChatAdapter extends SimpleRecyclerAdapter<RoomPublicMsg, PublicChatHolder> {


    private final Context mContext;
    private final RecyclerView mRecyclerView;
    //    item的点击监听
    private OnItemClickListener mListener;

    private appendDataCallback mCallback;
    private boolean isStop = true;

    public PublicChatAdapter(List<RoomPublicMsg> msgList, Context context, RecyclerView recyclerView) {
        super(msgList);
        this.mContext = context.getApplicationContext();
        this.mRecyclerView = recyclerView;
        isStop = false;
    }

    private RoomPublicMsg msgdata;
    List<RoomPublicMsg> TheLastGiftMsg ;
    private final int MAX_DATA_ITEM = 35;

    public void appendData(@NonNull final RoomPublicMsg data){
        //sRunnable runnable = new sRunnable(this,getDataList(),data);
         //((Activity)mContext).runOnUiThread(runnable);
        TheLastGiftMsg=getDataList();
        if(TheLastGiftMsg.size()>= MAX_DATA_ITEM) {
            TheLastGiftMsg.remove(0);
            notifyItemRemoved(0);
            View view = mRecyclerView.getChildAt(0);

            mRecyclerView.getChildViewHolder(view).itemView.setOnClickListener(null);
   /*         RecyclerView.ViewHolder holderview = mRecyclerView.getChildViewHolder(view);
            if(holderview instanceof  PublicChatHolder){
                ((PublicChatHolder)holderview).recycleView();
            }*/
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getDataList().add(data);
        //notifyDataSetChanged();
        notifyItemInserted(getItemCount());
        mCallback.CheckScrollVertically();
    }


    public void StopAllRequest(){
        /*isStop = true;*/
        getDataList().clear();

    }

   /* private static class sRunnable implements Runnable {

        private final PublicChatAdapter adapter;
        private final RoomPublicMsg msgdata;
        List<RoomPublicMsg> TheLastGiftMsg ;
        private final int MAX_DATA_ITEM = 35;


        public sRunnable(PublicChatAdapter publicChatAdapter, List<RoomPublicMsg> datalist, RoomPublicMsg data) {
            this.TheLastGiftMsg = datalist;
            this.adapter = publicChatAdapter;
            this.msgdata = data;
        }

        @Override
        public void run() {
            *//*if(adapter.isStop)
                return;*//*
            if(TheLastGiftMsg.size()>= MAX_DATA_ITEM) {
                TheLastGiftMsg.remove(0);
                adapter.notifyItemRemoved(0);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            adapter.getDataList().add(msgdata);
            //notifyDataSetChanged();
            adapter.notifyItemInserted(adapter.getItemCount());
            adapter.mCallback.CheckScrollVertically();
        }
    }*/

    @Override
    protected int getItemLayoutId(int viewType) {
        return R.layout.item_room_public_chat;
    }

    @NonNull
    @Override
    protected PublicChatHolder createHolder(View view) {
        return new PublicChatHolder(view,mContext);
    }

    @Override
    public void onBindViewHolder(final PublicChatHolder holder, final int position) {
        super.onBindViewHolder(holder, position);
        if(mListener == null) {
            holder.itemView.setOnClickListener(null);
            return;
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mListener.onItemClick(position, getDataList().get(position),holder);
                mListener.onItemClick(position, getDataList().get(position),holder);
            }
        });
    }

    //   item的点击事件
    public interface OnItemClickListener {
        void onItemClick(int position, RoomPublicMsg data, PublicChatHolder holder);
    }

    public interface appendDataCallback {

        void CheckScrollVertically();
    }


    public void setOnItemClickListener(OnItemClickListener li) {
        mListener = li;
    }
    public void setCallback(appendDataCallback callback) {
        mCallback = callback;
    }
}
