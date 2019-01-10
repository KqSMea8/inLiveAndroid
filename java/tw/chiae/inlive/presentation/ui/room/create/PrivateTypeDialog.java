package tw.chiae.inlive.presentation.ui.room.create;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import tw.chiae.inlive.R;

/**
 * Created by Administrator on 2016/12/13 0013.
 */

public class PrivateTypeDialog extends DialogFragment implements View.OnClickListener {

    private PrivateTypeCommit privateTypeCommit;
    PrivateSetStringDialog privateSetStringDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Window window = getDialog().getWindow();
        View view = inflater.inflate(R.layout.private_type_dialog_popu,  ((ViewGroup) window.findViewById(android.R.id.content)), false);//需要用android.R.id.content这个view
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
        window.setLayout(-1, -1);//这2行,和上面的一样,注意顺序就行;
        initView(view);
        return view;
    }

    private void initView(View view) {
        privateTypeCommit= (PrivateTypeCommit) getActivity();
        if (privateTypeCommit!=null){
            view.findViewById(R.id.private_tyep_cancel).setOnClickListener(this);
            view.findViewById(R.id.private_tyep_password).setOnClickListener(this);
            view.findViewById(R.id.private_tyep_ticket).setOnClickListener(this);
            view.findViewById(R.id.private_tyep_level).setOnClickListener(this);
            view.findViewById(R.id.private_tyep_recovery).setOnClickListener(this);
            view.findViewById(R.id.private_type_dialog_rootlayout).setOnClickListener(this);
        }
    }


    @Override
    public void onClick(View v) {
        if (privateSetStringDialog==null)
           privateSetStringDialog=new PrivateSetStringDialog();
        switch (v.getId()){
            case R.id.private_tyep_cancel:
                dismiss();
                break;
            case R.id.private_tyep_password:
                privateSetStringDialog.show(getFragmentManager(),PrivateSetStringDialog.PRIVTE_ROOM_PWD);
                dismiss();
                break;
            case R.id.private_tyep_ticket:
                privateSetStringDialog.show(getFragmentManager(),PrivateSetStringDialog.PRIVTE_ROOM_TICKET);
                dismiss();
                break;
            case R.id.private_tyep_level:
                privateSetStringDialog.show(getFragmentManager(),PrivateSetStringDialog.PRIVTE_ROOM_LEVEL);
                dismiss();
                break;
            case R.id.private_tyep_recovery:
                ((PrivateTypeCommit) getActivity()).recoveryCommit();
                dismiss();
                break;
            case R.id.private_type_dialog_rootlayout:
                dismiss();
                break;
        }
    }
}
