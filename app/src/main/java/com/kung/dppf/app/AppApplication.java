package com.kung.dppf.app;

import com.kung.dppf.BuildConfig;
import com.kung.dppf.R;
import com.kung.dppf.entity.database.MyGreenDaoDbHelper;
import com.kung.dppf.ui.login.LoginActivity;
import com.kung.dppf.ui.main.HomeActivity;
import com.kung.dppf.entity.greendao.DaoMaster;
import com.kung.dppf.entity.greendao.DaoSession;

import org.greenrobot.greendao.identityscope.IdentityScopeType;

import java.lang.ref.WeakReference;

import me.goldze.mvvmhabit.base.BaseApplication;
import me.goldze.mvvmhabit.crash.CaocConfig;
import me.goldze.mvvmhabit.utils.KLog;

/**
 * Created by goldze on 2017/7/16.
 */

public class AppApplication extends BaseApplication {

    private static DaoSession daoSession;
    public static WeakReference<HomeActivity> homeActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        //是否开启打印日志
        KLog.init(BuildConfig.DEBUG);
        //初始化全局异常崩溃
        initCrash();
        //内存泄漏检测
//        if (!LeakCanary.isInAnalyzerProcess(this)) {
//            LeakCanary.install(this);
//        }

        setupDatabase();
    }

    private void initCrash() {
        CaocConfig.Builder.create()
                .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
                .enabled(true) //是否启动全局异常捕获
                .showErrorDetails(true) //是否显示错误详细信息
                .showRestartButton(true) //是否显示重启按钮
                .trackActivities(true) //是否跟踪Activity
                .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
                .errorDrawable(R.mipmap.ic_launcher) //错误图标
                .restartActivity(LoginActivity.class) //重新启动后的activity
//                .errorActivity(YourCustomErrorActivity.class) //崩溃后的错误activity
//                .eventListener(new YourCustomEventListener()) //崩溃后的错误监听
                .apply();
    }

    /**
     * 配置数据库
     */
    private void setupDatabase() {
        //创建数据库shop.db
        MyGreenDaoDbHelper helper = new MyGreenDaoDbHelper(this, "kungnbyj.db", null);
//        //获取可写数据库
//        SQLiteDatabase db = helper.getWritableDatabase();
        //获取数据库对象
        DaoMaster daoMaster = new DaoMaster(helper.getWritableDatabase());
        //获取dao对象管理者
        daoSession = daoMaster.newSession(IdentityScopeType.None);
    }

    public static DaoSession getDaoInstant() {
        return daoSession;
    }
}
