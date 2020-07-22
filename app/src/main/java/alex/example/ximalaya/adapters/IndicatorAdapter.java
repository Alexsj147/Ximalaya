package alex.example.ximalaya.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import alex.example.ximalaya.MainActivity;
import alex.example.ximalaya.R;

public class IndicatorAdapter extends CommonNavigatorAdapter {


    private final String[] mTitles;
    private OnIndicatorTapClickListener mOnTapClickListener ;

    public IndicatorAdapter(Context context) {
        mTitles = context.getResources().getStringArray(R.array.indicator_title);
    }

    @Override
    public int getCount() {
        if (mTitles != null) {
            return mTitles.length;
        }
        return 0;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        //创建View
        ColorTransitionPagerTitleView colorTransitionPagerTitleView =new ColorTransitionPagerTitleView(context);
        //设置一般情况下的颜色为灰色
        colorTransitionPagerTitleView.setNormalColor(Color.parseColor("#aaffffff"));
        //设置选中情况下的颜色为黑色
        colorTransitionPagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
        //单位sp
        colorTransitionPagerTitleView.setTextSize(18);
        //设置要显示的内容
        colorTransitionPagerTitleView.setText(mTitles[index]);
        //设置title的点击事件，这里的话，如果点击了title,那么就选中下面的ViewPager对应的index里面去
        //当我们点击了title的时候,下面的ViewPager会对应着index进行切换
        colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //index不一样的话，切换ViewPager的内容
                if (mOnTapClickListener != null) {
                    mOnTapClickListener.onTapClick(index);
                }
            }
        });
        //把这个创建好的view返回去
        return colorTransitionPagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
        linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        linePagerIndicator.setColors(Color.WHITE);
        return linePagerIndicator;
    }

    public void setOnIndicatorTapClickListener(OnIndicatorTapClickListener listener){
        this.mOnTapClickListener = listener;
    }
    public interface OnIndicatorTapClickListener{
        void onTapClick(int index);
    }
}
