package com.kung.dppf.entity.database;

import com.kung.dppf.app.AppApplication;
import com.kung.dppf.entity.ProductBean;
import com.kung.dppf.entity.ProductNutrition;
import com.kung.dppf.entity.ProductType;
import com.kung.dppf.entity.WeighRecord;
import com.kung.dppf.entity.greendao.ProductBeanDao;
import com.kung.dppf.entity.greendao.ProductNutritionDao;
import com.kung.dppf.entity.greendao.ProductTypeDao;
import com.kung.dppf.entity.greendao.WeighRecordDao;

import java.util.ArrayList;
import java.util.List;

import me.goldze.mvvmhabit.utils.StringUtils;

public class SqlManager {

    /**
     * 保存产品
     *
     * @param bean
     * @return
     */
    public static long insertOrReplaceProductBean(ProductBean bean) {
        ProductBeanDao sqlDao = AppApplication.getDaoInstant().getProductBeanDao();
        ProductBean oldBean = queryProductBeanByCode(bean.getProductCode());
        if (oldBean != null) {
            bean.set_id(oldBean.get_id());
        }
        return sqlDao.insertOrReplace(bean);
    }

    /**
     * 根据编号称查询产品
     *
     * @param code
     * @return
     */
    public static ProductBean queryProductBeanByCode(String code) {
        try {
            ProductBeanDao sqlDao = AppApplication.getDaoInstant().getProductBeanDao();
            List<ProductBean> list = sqlDao.queryBuilder().where(ProductBeanDao.Properties.ProductCode.eq(code)).list();
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除型号配方
     *
     * @param bean
     */
    public static void deleteProductBean(ProductBean bean) {
        ProductBeanDao sqlDao = AppApplication.getDaoInstant().getProductBeanDao();
        sqlDao.delete(bean);
    }

    /**
     * 查询所有型号配方
     *
     * @return
     */
    public static List<ProductBean> queryAllProductBean(String key, int pageIndex, int pageSize) {
        try {
            ProductBeanDao sqlDao = AppApplication.getDaoInstant().getProductBeanDao();
            List<ProductBean> list;
            if (StringUtils.isTrimEmpty(key)) {
                list = sqlDao.queryBuilder().orderAsc(ProductBeanDao.Properties.ProductCode).limit(pageSize).offset((pageIndex - 1) * pageSize).list();
            } else {
//                list = sqlDao.queryBuilder().whereOr(ProductBeanDao.Properties.ProductCode.like("%"+key+"%"),
//                                ProductBeanDao.Properties.ProductName.like("%"+key+"%"))
//                        .orderAsc(ProductBeanDao.Properties.ProductCode).limit(pageSize).offset((pageIndex - 1) * pageSize).list();
                list = sqlDao.queryBuilder().whereOr(ProductBeanDao.Properties.ProductCode.like("%"+key+"%"),
                                ProductBeanDao.Properties.ProductName.like("%"+key+"%"))
                        .orderAsc(ProductBeanDao.Properties.ProductCode).limit(pageSize).offset((pageIndex - 1) * pageSize).list();
            }
            if (list != null && list.size() > 0) {
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询配方总数量
     */
    public static long queryProductCount(String key) {
        try {
            ProductBeanDao sqlDao = AppApplication.getDaoInstant().getProductBeanDao();
            if (StringUtils.isTrimEmpty(key)) {
                return sqlDao.queryBuilder().count();
            } else {
                return sqlDao.queryBuilder().whereOr(ProductBeanDao.Properties.ProductCode.like("%"+key+"%"),
                        ProductBeanDao.Properties.ProductName.like("%"+key+"%")).count();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 保存称重记录
     * @param bean
     * @return
     */
    public static long insertOrReplaceWeighRecord(WeighRecord bean) {
        WeighRecordDao sqlDao = AppApplication.getDaoInstant().getWeighRecordDao();
        return sqlDao.insertOrReplace(bean);
    }

    /**
     * 查询称重记录
     * @param beginDate
     * @param endDate
     * @param key
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public static List<WeighRecord> queryWeighRecords(String beginDate, String endDate, String key, int pageIndex, int pageSize) {
        try {
            WeighRecordDao sqlDao = AppApplication.getDaoInstant().getWeighRecordDao();
            List<WeighRecord> list;
            if (StringUtils.isTrimEmpty(beginDate) && StringUtils.isTrimEmpty(endDate) && StringUtils.isTrimEmpty(key)) {
                list = sqlDao.queryBuilder().orderDesc(WeighRecordDao.Properties.CreateTime).limit(pageSize).offset((pageIndex - 1) * pageSize).list();
            } else if (StringUtils.isTrimEmpty(beginDate) && StringUtils.isTrimEmpty(endDate)) {
                list = sqlDao.queryBuilder().whereOr(WeighRecordDao.Properties.ProductCode.like("%"+key+"%"),
                                WeighRecordDao.Properties.ProductName.like("%"+key+"%"),
                                WeighRecordDao.Properties.TypeName.like("%"+key+"%"))
                        .orderDesc(WeighRecordDao.Properties.CreateTime).limit(pageSize).offset((pageIndex - 1) * pageSize).list();
            } else if (StringUtils.isTrimEmpty(key)) {
                list = sqlDao.queryBuilder().where(WeighRecordDao.Properties.CreateTime.between(beginDate, endDate))
                        .orderDesc(WeighRecordDao.Properties.CreateTime).limit(pageSize).offset((pageIndex - 1) * pageSize).list();
            } else {
                list = sqlDao.queryBuilder().whereOr(WeighRecordDao.Properties.ProductCode.like("%"+key+"%"),
                                WeighRecordDao.Properties.ProductName.like("%"+key+"%"),
                                WeighRecordDao.Properties.TypeName.like("%"+key+"%"))
                        .where(WeighRecordDao.Properties.CreateTime.between(beginDate, endDate))
                        .orderDesc(WeighRecordDao.Properties.CreateTime).limit(pageSize).offset((pageIndex - 1) * pageSize).list();
            }
            if (list != null && list.size() > 0) {
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询称重记录数量
     * @param beginDate
     * @param endDate
     * @param key
     * @return
     */
    public static long queryWeighRecordCount(String beginDate, String endDate, String key) {
        try {
            WeighRecordDao sqlDao = AppApplication.getDaoInstant().getWeighRecordDao();
            if (StringUtils.isTrimEmpty(beginDate) && StringUtils.isTrimEmpty(endDate) && StringUtils.isTrimEmpty(key)) {
                return sqlDao.queryBuilder().count();
            } else if (StringUtils.isTrimEmpty(beginDate) && StringUtils.isTrimEmpty(endDate)) {
                return sqlDao.queryBuilder().whereOr(WeighRecordDao.Properties.ProductCode.like("%"+key+"%"),
                        WeighRecordDao.Properties.ProductName.like("%"+key+"%"),
                        WeighRecordDao.Properties.TypeName.like("%"+key+"%")).count();
            } else if (StringUtils.isTrimEmpty(key)) {
                return sqlDao.queryBuilder().where(WeighRecordDao.Properties.CreateTime.between(beginDate, endDate)).count();
            } else {
                return sqlDao.queryBuilder().where(WeighRecordDao.Properties.CreateTime.between(beginDate, endDate))
                        .whereOr(WeighRecordDao.Properties.ProductCode.like("%"+key+"%"),
                                WeighRecordDao.Properties.ProductName.like("%"+key+"%"),
                                WeighRecordDao.Properties.TypeName.like("%"+key+"%")).count();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //获取总重量
    public static List<Double> queryWeighRecordTotalWeight(String beginDate, String endDate, String key) {
        try {
            WeighRecordDao sqlDao = AppApplication.getDaoInstant().getWeighRecordDao();
            List<WeighRecord> list;
            if (StringUtils.isTrimEmpty(key)) {
                list = sqlDao.queryBuilder().where(WeighRecordDao.Properties.CreateTime.between(beginDate, endDate)).list();
            } else {
                list = sqlDao.queryBuilder().whereOr(WeighRecordDao.Properties.ProductCode.like("%"+key+"%"),
                        WeighRecordDao.Properties.ProductName.like("%"+key+"%"),
                                WeighRecordDao.Properties.TypeName.like("%"+key+"%"))
                        .where(WeighRecordDao.Properties.CreateTime.between(beginDate, endDate)).list();
            }
            if (list != null && list.size() > 0) {
                double totalWeight = 0;
                double grossWeight = 0;
                int num = 0;
                for (WeighRecord bean : list) {
                    totalWeight += Double.parseDouble(bean.getNetWeight());
//                    grossWeight += Double.parseDouble(bean.getGrossWeight());
//                    num = num + Integer.parseInt(bean.getQuantity());
                }
                List<Double> total = new ArrayList<>();
                total.add(totalWeight);
//                total.add(grossWeight);
//                total.add((double) num);
                return total;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Double> total = new ArrayList<>();
        total.add(0.0);
//        total.add(0.0);
//        total.add(0.0);
        return total;
    }

    /**
     * 查询称重记录数量
     * @param productCode
     * @return
     */
    public static long queryWeighRecordCountByProductCode(String productCode) {
        try {
            WeighRecordDao sqlDao = AppApplication.getDaoInstant().getWeighRecordDao();
            return sqlDao.queryBuilder().where(WeighRecordDao.Properties.ProductCode.eq(productCode)).count();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 删除称重记录
     */
    public static void deleteWeighRecord(WeighRecord bean) {
        WeighRecordDao sqlDao = AppApplication.getDaoInstant().getWeighRecordDao();
        sqlDao.delete(bean);
    }

    /**
     * 删除某个日期之前的数据
     * @param fdate
     */
    public static void deleteWeighRecords(String fdate) {
        WeighRecordDao sqlDao = AppApplication.getDaoInstant().getWeighRecordDao();
        sqlDao.queryBuilder().where(WeighRecordDao.Properties.CreateTime.lt(fdate)).buildDelete().executeDeleteWithoutDetachingEntities();
    }
//    public static boolean deleteLabelBoxAndGood(String fdate) {
//        try {
//            return AppApplication.getDaoInstant().callInTx(new Callable<Boolean>(){
//
//                @Override
//                public Boolean call() {
//                    LabelGoodBeanDao sqlDaoGood = AppApplication.getDaoInstant().getLabelGoodBeanDao();
//                    sqlDaoGood.queryBuilder().where(LabelGoodBeanDao.Properties.Date.lt(fdate))
//                            .buildDelete().executeDeleteWithoutDetachingEntities();
//
//                    LabelBoxBeanDao sqlDao = AppApplication.getDaoInstant().getLabelBoxBeanDao();
//                    sqlDao.queryBuilder().where(LabelBoxBeanDao.Properties.Date.lt(fdate))
//                            .buildDelete().executeDeleteWithoutDetachingEntities();
//                    return true;
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }


    /**
     * 保存产品类型
     *
     * @param bean
     * @return
     */
    public static long insertOrReplaceProductTypeBean(ProductType bean) {
        ProductTypeDao sqlDao = AppApplication.getDaoInstant().getProductTypeDao();
        return sqlDao.insertOrReplace(bean);
    }

    /**
     * 查询所有产品类型
     *
     * @return
     */
    public static List<ProductType> queryAllProductTypeBean(String key, int pageIndex, int pageSize) {
        try {
            ProductTypeDao sqlDao = AppApplication.getDaoInstant().getProductTypeDao();
            List<ProductType> list;
            if (StringUtils.isTrimEmpty(key)) {
                list = sqlDao.queryBuilder().orderAsc(ProductTypeDao.Properties.TypeName).limit(pageSize).offset((pageIndex - 1) * pageSize).list();
            } else {
                list = sqlDao.queryBuilder().where(ProductTypeDao.Properties.TypeName.like("%"+key+"%"))
                        .orderAsc(ProductBeanDao.Properties.TypeName).limit(pageSize).offset((pageIndex - 1) * pageSize).list();
//                list = sqlDao.queryBuilder().whereOr(WeighRecordDao.Properties.ProductCode.like("%"+key+"%"),
//                                WeighRecordDao.Properties.ProductName.like("%"+key+"%"))
//                        .where(WeighRecordDao.Properties.CreateTime.between(beginDate, endDate)).list();
            }
            if (list != null && list.size() > 0) {
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除产品类型
     *
     * @param bean
     */
    public static void deleteProductTypeBean(ProductType bean) {
        ProductTypeDao sqlDao = AppApplication.getDaoInstant().getProductTypeDao();
        sqlDao.delete(bean);
    }

    /**
     * 查询产品类型
     *
     * @param typeName
     * @return
     */
    public static ProductType queryProductTypeBeanByName(String typeName) {
        try {
            ProductTypeDao sqlDao = AppApplication.getDaoInstant().getProductTypeDao();
            List<ProductType> list = sqlDao.queryBuilder().where(ProductTypeDao.Properties.TypeName.eq(typeName)).list();
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 添加营养成分
     *
     * @param bean
     * @return
     */
    public static long insertOrReplaceNutritionBean(ProductNutrition bean) {
        ProductNutritionDao sqlDao = AppApplication.getDaoInstant().getProductNutritionDao();
        return sqlDao.insertOrReplace(bean);
    }

    /**
     * 查询所有营养成分
     *
     * @return
     */
    public static List<ProductNutrition> queryAllNutritionBean() {
        try {
            ProductNutritionDao sqlDao = AppApplication.getDaoInstant().getProductNutritionDao();
            List<ProductNutrition> list = sqlDao.queryBuilder().orderAsc(ProductNutritionDao.Properties.ProductCode).list();
            if (list != null && list.size() > 0) {
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除营养成分
     *
     * @param bean
     */
    public static void deleteNutritionBean(ProductNutrition bean) {
        ProductNutritionDao sqlDao = AppApplication.getDaoInstant().getProductNutritionDao();
        sqlDao.delete(bean);
    }

    /**
     * 查询营养成分
     *
     * @param ProductCode
     * @return
     */
    public static List<ProductNutrition> queryNutritionBeanByProductCode(String ProductCode) {
        try {
            ProductNutritionDao sqlDao = AppApplication.getDaoInstant().getProductNutritionDao();
            List<ProductNutrition> list = sqlDao.queryBuilder().where(ProductNutritionDao.Properties.ProductCode.eq(ProductCode)).list();
            if (list != null && list.size() > 0) {
                return list;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 删除营养成分
     *
     * @param ProductCode
     * @return
     */
    public static void deleteNutritionBeanByProductCode(String ProductCode) {
        try {
            ProductNutritionDao sqlDao = AppApplication.getDaoInstant().getProductNutritionDao();
            List<ProductNutrition> list = sqlDao.queryBuilder().where(ProductNutritionDao.Properties.ProductCode.eq(ProductCode)).list();
            if (list != null && list.size() > 0) {
                for (ProductNutrition bean : list) {
                    sqlDao.delete(bean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
