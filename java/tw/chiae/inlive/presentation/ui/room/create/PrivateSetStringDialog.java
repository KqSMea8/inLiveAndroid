package tw.chiae.inlive.presentation.ui.room.create;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import tw.chiae.inlive.R;
import tw.chiae.inlive.presentation.ui.widget.CustomToast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/12/13 0013.
 */

public class PrivateSetStringDialog extends DialogFragment{
    public static final String PRIVTE_ROOM_PWD="1";
    public static final String PRIVTE_ROOM_TICKET="2";
    public static final String PRIVTE_ROOM_LEVEL="3";
    private EditText editString;
    private TextView title,text;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        View view = inflater.inflate(R.layout.private_setstring_dialog_popu,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        title= (TextView) view.findViewById(R.id.private_setstring_title);
        text= (TextView) view.findViewById(R.id.private_setstring_text);
        editString = (EditText) view.findViewById(R.id.private_setstring_edit);
        editString.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId== EditorInfo.IME_ACTION_DONE ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)) {
                    commitPrivateString();
                    return true;
                }
                return false;
            }
        });
        if (getTag()==null){
            CustomToast.makeCustomText(getActivity(), getString(R.string.msg_unknown_error), Toast.LENGTH_SHORT).show();
            dismiss();
            return;
        }else if (getTag().equals(PRIVTE_ROOM_PWD)){
            title.setText(getString(R.string.room_private_pwd_title));
            text.setText(getString(R.string.room_private_pwd_text));
            editString.setHint(getString(R.string.room_private_pwd_edit));
        }else if (getTag().equals(PRIVTE_ROOM_TICKET)){
            title.setText(getString(R.string.room_private_ticket_title));
            text.setText(getString(R.string.room_private_ticket_text));
            editString.setHint(getString(R.string.room_private_ticket_edit));
        }else if (getTag().equals(PRIVTE_ROOM_LEVEL)){
            title.setText(getString(R.string.room_private_level_title));
            text.setText(getString(R.string.room_private_level_text));
            editString.setHint(getString(R.string.room_private_level_edit));
        }
        view.findViewById(R.id.private_setstring_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        view.findViewById(R.id.private_setstring_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitPrivateString();
            }
        });
    }

    public boolean isNumberNo(String mobiles){
        Pattern p = Pattern.compile("^[0-9]*$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public boolean isNumberZmNo(String mobiles){
        Pattern p = Pattern.compile("^\\w+$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public void commitPrivateString(){
        if (TextUtils.isEmpty(editString.getText().toString())){
            CustomToast.makeCustomText(getActivity(), getString(R.string.room_private_edit_null), Toast.LENGTH_SHORT).show();
        }else if (getTag().equals(PRIVTE_ROOM_PWD)&&!isNumberZmNo(editString.getText().toString())){
            CustomToast.makeCustomText(getActivity(), getString(R.string.room_private_edit_error), Toast.LENGTH_SHORT).show();
        }else if (!isNumberNo(editString.getText().toString())&&(getTag().equals(PRIVTE_ROOM_TICKET)||getTag().equals(PRIVTE_ROOM_LEVEL))){
            CustomToast.makeCustomText(getActivity(), getString(R.string.room_private_edit_error), Toast.LENGTH_SHORT).show();
        }else {
            PrivateTypeCommit privateTypeCommit = (PrivateTypeCommit) getActivity();
            privateTypeCommit.privateStringSet(editString.getText().toString(), Integer.valueOf(getTag()));
        }
        dismiss();
    }
}
