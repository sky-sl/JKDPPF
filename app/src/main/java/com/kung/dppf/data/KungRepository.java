package com.kung.dppf.data;

import com.kung.dppf.data.source.HttpDataSource;
import com.kung.dppf.data.source.LocalDataSource;
import com.kung.dppf.entity.DemoEntity;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import io.reactivex.Observable;
import me.goldze.mvvmhabit.base.BaseModel;
import me.goldze.mvvmhabit.http.BaseResponse;

/**
 * MVVM的Model层，统一模块的数据仓库，包含网络数据和本地数据（一个应用可以有多个Repositor）
 * Created by goldze on 2019/3/26.
 */
public class KungRepository extends BaseModel implements HttpDataSource, LocalDataSource {
    private volatile static KungRepository INSTANCE = null;
    private final HttpDataSource mHttpDataSource;

    private final LocalDataSource mLocalDataSource;

    private KungRepository(@NonNull HttpDataSource httpDataSource,
                           @NonNull LocalDataSource localDataSource) {
        this.mHttpDataSource = httpDataSource;
        this.mLocalDataSource = localDataSource;
    }

    public static KungRepository getInstance(HttpDataSource httpDataSource,
                                             LocalDataSource localDataSource) {
        if (INSTANCE == null) {
            synchronized (KungRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new KungRepository(httpDataSource, localDataSource);
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }


    @Override
    public Observable<Object> login() {
        return mHttpDataSource.login();
    }

    @Override
    public Observable<DemoEntity> loadMore() {
        return mHttpDataSource.loadMore();
    }

    @Override
    public Observable<BaseResponse<DemoEntity>> demoGet() {
        return mHttpDataSource.demoGet();
    }

    @Override
    public Observable<BaseResponse<DemoEntity>> demoPost(String catalog) {
        return mHttpDataSource.demoPost(catalog);
    }

    @Override
    public void saveUserName(String userName) {
        mLocalDataSource.saveUserName(userName);
    }

    @Override
    public void savePassword(String password) {
        mLocalDataSource.savePassword(password);
    }

    @Override
    public String getUserName() {
        return mLocalDataSource.getUserName();
    }

    @Override
    public String getPassword() {
        return mLocalDataSource.getPassword();
    }

    @Override
    public void savePluCode(String pluCode) {
        mLocalDataSource.savePluCode(pluCode);
    }

    @Override
    public String getPluCode() {
        return mLocalDataSource.getPluCode();
    }

    @Override
    public void saveLabelTitle(String labelTitle) {
        mLocalDataSource.saveLabelTitle(labelTitle);
    }

    @Override
    public String getLabelTitle() {
        return mLocalDataSource.getLabelTitle();
    }

    @Override
    public void saveTare(String tare) {
        mLocalDataSource.saveTare(tare);
    }

    @Override
    public String getTare() {
        return mLocalDataSource.getTare();
    }

    @Override
    public void saveBatchNumber(String batchNumber) {
        mLocalDataSource.saveBatchNumber(batchNumber);
    }

    @Override
    public String getBatchNumber() {
        return mLocalDataSource.getBatchNumber();
    }

    @Override
    public void savePrintCount(int printCount) {
        mLocalDataSource.savePrintCount(printCount);
    }

    @Override
    public int getPrintCount() {
        return mLocalDataSource.getPrintCount();
    }
}
