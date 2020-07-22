package alex.example.ximalaya;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;

import alex.example.ximalaya.adapters.IndicatorAdapter;
import alex.example.ximalaya.adapters.MainContentAdapter;
import alex.example.ximalaya.utils.LogUtil;

public class MainActivity extends FragmentActivity {

    private static final String TAG="MainActivity";
    private MagicIndicator mMagicIndicator;
    private ViewPager mContentPager;
    private IndicatorAdapter mIndicatorAdapter;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        /*Map<String, String> map = new HashMap<>();
        CommonRequest.getCategories(map, new IDataCallBack<CategoryList>() {
            @Override
            public void onSuccess(CategoryList categoryList) {
               List<Category> categories=categoryList.getCategories();
                if (categories!=null) {
                  int size=categories.size();
                     Log.d(TAG, "categories size ---<"+size);
                    for (Category category : categories) {
                        //Log.d(TAG,"category -->"+category.getCategoryName());
                        LogUtil.d(TAG,"category -->"+category.getCategoryName());
                    }

                }

            }

            @Override
            public void onError(int i, String s) {
                //Log.d(TAG,"error code --"+ i +"error message ==>" + s);
                  LogUtil.d(TAG,"error code --"+ i +"error message ==>" + s);
            }
        });*/
    }

    private void initEvent() {
        mIndicatorAdapter.setOnIndicatorTapClickListener(new IndicatorAdapter.OnIndicatorTapClickListener() {
            @Override
            public void onTapClick(int index) {
                LogUtil.d(TAG,"click index is ==> " + index);
                if (mContentPager != null) {
                    mContentPager.setCurrentItem(index);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initView() {
        mMagicIndicator = this.findViewById(R.id.main_indicator);
        mMagicIndicator.setBackgroundColor(this.getColor(R.color.mainColor));
        //创建适配器
        mIndicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(mIndicatorAdapter);
        //设置要显示的内容


        //ViewPager
        mContentPager = this.findViewById(R.id.content_pager);
        //创建内容适配器
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager);
        mContentPager.setAdapter(mainContentAdapter);
        //把ViewPager和indicator绑定
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator,mContentPager);
    }
}
