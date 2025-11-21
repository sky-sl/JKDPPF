package com.kung.dppf.entity.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import com.kung.dppf.entity.greendao.DaoMaster;
import com.kung.dppf.entity.greendao.ProductBeanDao;
import com.kung.dppf.entity.greendao.ProductNutritionDao;
import com.kung.dppf.entity.greendao.ProductTypeDao;
import com.kung.dppf.entity.greendao.WeighRecordDao;

import org.greenrobot.greendao.database.Database;

import me.goldze.mvvmhabit.utils.KLog;

public class MyGreenDaoDbHelper extends DaoMaster.DevOpenHelper {
    public MyGreenDaoDbHelper(Context context, String name) {
        super(context, name);
    }

    public MyGreenDaoDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }


    @Override
    @SuppressWarnings("all")
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        KLog.e("MyGreenDaoDbHelper", "----" + oldVersion + "---先前和更新之后的版本---" + newVersion + "----");
        if (oldVersion < newVersion) {
            KLog.e("MyGreenDaoDbHelper", "进行数据库升级");
            new GreenDaoCompatibleUpdateHelper()
                    .setCallBack(
                            new GreenDaoCompatibleUpdateHelper.GreenDaoCompatibleUpdateCallBack() {

                                @Override
                                public void onFinalSuccess() {
                                    KLog.e("MyGreenDaoDbHelper", "进行数据库升级 ===> 成功");
                                }

                                @Override
                                public void onFailedLog(String errorMsg) {
                                    KLog.e("MyGreenDaoDbHelper", "升级失败日志 ===> " + errorMsg);
                                }
                            }
                    )
                    .compatibleUpdate(
                            db,
                            WeighRecordDao.class, ProductBeanDao.class, ProductTypeDao.class, ProductNutritionDao.class);
            KLog.e("MyGreenDaoDbHelper", "进行数据库升级--完成");
        }
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        // 不要调用父类的，它默认是先删除全部表再创建
        // super.onUpgrade(db, oldVersion, newVersion);
    }
}
