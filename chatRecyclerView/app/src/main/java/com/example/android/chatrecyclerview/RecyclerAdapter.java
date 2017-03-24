package com.example.android.chatrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Harold Prabhu on 17-03-2017.
 */

class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    boolean[] isSender;
    String[] messages;
    public RecyclerAdapter(boolean[] isSender, String[] messages) {
          this.isSender=isSender;
          this.messages=messages;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        if(viewType==0) {
            View view = inflater.inflate(R.layout.left_layout, parent, false);
            holder= new ViewHolderLeft(view);
        }
        else{
            View view = inflater.inflate(R.layout.right_layout, parent, false);
            holder= new ViewHolderRight(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(isSender[position]){
            ViewHolderRight viewHolderRight =(ViewHolderRight)holder;
            viewHolderRight.SentMsgTx.setText(messages[position]);

        }
        else{
            ViewHolderLeft viewHolderLeft=(ViewHolderLeft)holder;
            viewHolderLeft.ReceivedMsgTx.setText(messages[position]);

        }

    }

    public static class ViewHolderLeft extends RecyclerView.ViewHolder
    {
        TextView ReceivedMsgTx;
        public ViewHolderLeft(View view)
        {
            super(view);
            ReceivedMsgTx=(TextView)view.findViewById(R.id.received_text_view);

        }

    }

    public static class ViewHolderRight extends RecyclerView.ViewHolder
    {
        TextView SentMsgTx;
        public ViewHolderRight(View view)
        {
            super(view);
            SentMsgTx=(TextView)view.findViewById(R.id.sent_text_view);

        }

    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        if(isSender[position])
          return 1;
        return 0;
    }
    @Override
    public int getItemCount() {
        return messages.length;
    }
}
