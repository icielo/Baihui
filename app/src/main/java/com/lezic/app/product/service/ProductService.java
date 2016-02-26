package com.lezic.app.product.service;

import android.app.ProgressDialog;
import android.content.Context;

import com.lezic.app.product.vo.Product;
import com.lezic.core.orm.service.BaseService;

/**
 * Created by cielo on 2016/2/19 0019.
 */
public class ProductService extends BaseService<Product> {

    public ProductService(Context context) {
        super(context);
    }

    /**
     * 导出数据
     * @param progressDialog 进度条
     */
    public void doExport(ProgressDialog progressDialog) {
        new Thread() {
            @Override
            public void run() {
                super.run();
            }
        };
    }
}
