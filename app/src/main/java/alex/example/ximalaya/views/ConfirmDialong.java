package alex.example.ximalaya.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import alex.example.ximalaya.R;

public class ConfirmDialong extends Dialog {
    private View mCancelSub;
    private View mGiveUp;
    private onDialogActionClickListener mClickListener = null;

    public ConfirmDialong(@NonNull Context context) {
        this(context,0);
    }

    public ConfirmDialong(@NonNull Context context, int themeResId) {
        this(context, true,null);
    }

    protected ConfirmDialong(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);
        initView();
        initListener();
    }

    private void initListener() {
        mCancelSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onCancelSubClick();
                    dismiss();
                }
            }
        });
        mGiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onGiveUpClick();
                    dismiss();
                }
            }
        });
    }

    private void initView() {
        mCancelSub = this.findViewById(R.id.dialog_cancel_sub_tv);
        mGiveUp = this.findViewById(R.id.dialog_give_up_tv);
    }

    public void setOnDialogActionClickListener(onDialogActionClickListener listener){
        this.mClickListener =listener;
    }
    public interface onDialogActionClickListener{
        void onCancelSubClick();
        void onGiveUpClick();
    }
}
