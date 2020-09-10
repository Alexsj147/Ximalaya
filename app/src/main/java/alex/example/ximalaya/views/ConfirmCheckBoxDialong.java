package alex.example.ximalaya.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import alex.example.ximalaya.R;

public class ConfirmCheckBoxDialong extends Dialog {
    private View mCancel;
    private View mConfirm;
    private onDialogActionClickListener mClickListener = null;
    private CheckBox mCheckBox;

    public ConfirmCheckBoxDialong(@NonNull Context context) {
        this(context,0);
    }

    public ConfirmCheckBoxDialong(@NonNull Context context, int themeResId) {
        this(context, true,null);
    }

    protected ConfirmCheckBoxDialong(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm_check_box);
        initView();
        initListener();
    }

    private void initListener() {
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    ;
                    mClickListener.onCancelClick();
                    dismiss();
                }
            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    boolean isChecked = mCheckBox.isChecked();
                    mClickListener.onConfirmClick(isChecked);
                    dismiss();
                }
            }
        });
    }

    private void initView() {
        mCancel = this.findViewById(R.id.dialog_check_box_cancel);
        mConfirm = this.findViewById(R.id.dialog_check_box_confirm);
        mCheckBox = this.findViewById(R.id.dialog_check_box);
    }

    public void setOnDialogActionClickListener(onDialogActionClickListener listener){
        this.mClickListener =listener;
    }
    public interface onDialogActionClickListener{
        void onCancelClick();
        void onConfirmClick(boolean isChecked);
    }
}
