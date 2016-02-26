package com.lezic.app.product.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lezic.app.product.service.ProductService;
import com.lezic.app.product.vo.Product;
import com.lezic.core.util.UtilDate;
import com.lezic.core.util.UtilExcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by cielo on 2016/2/19 0019.
 */
public class ExportThread extends Thread {

    private Context context;

    /**
     * 进度条
     */
    private ProgressDialog progressDialogInitData;

    /**
     * 商品服务类
     */
    private ProductService ProductService;

    private Handler handler;

    public static String filePath = "";

    public  static String fileName = "";

    public ExportThread(ProgressDialog progressDialogInitData, ProductService ProductService, Handler handler) {
        this.progressDialogInitData = progressDialogInitData;
        this.ProductService = ProductService;
        this.context = this.progressDialogInitData.getContext();
        this.handler = handler;
        this.fileName = UtilDate.formatDate(UtilDate.P_YYYYMMDD_2) + "-商品.xls";
        this.filePath = Environment.getExternalStorageDirectory().getPath() + "/百汇便利店";
    }

    /**
     * 导出文件
     *
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @author Lin Chenglin
     * @date 2013-9-15
     */
    public void exportFile() {
        try {
            List<Product> result = ProductService.getAll();
            if (result == null || result.size() == 0) {
                Message msg = new Message();
                msg.what = 2;
                handler.sendMessage(msg);
                return;
            }
            progressDialogInitData.setMax(result.size() + 1);
            Iterator<Product> it = result.iterator();
            List<List<Object>> rows = new LinkedList<List<Object>>();
            while (it.hasNext()) {
                Product item = it.next();
                List<Object> row = new ArrayList<Object>();
                row.add(item.getCode());
                row.add(item.getPrice());
                row.add(item.getName());
                rows.add(row);
                progressDialogInitData.setProgress(progressDialogInitData.getProgress() + 1);
            }
            Map<String, Object> params = new HashMap<String, Object>();
            String[] headers = new String[]{"条码","价格","名称"};
            params.put("headers", headers);
            UtilExcel.createExcelFile(filePath, fileName, params, rows);
            progressDialogInitData.setProgress(progressDialogInitData.getProgress() + 1);
            Message msg = new Message();
            msg.what = 0;
            handler.sendMessage(msg);
        } catch (Exception e) {
            Log.e("导出失败", e.toString());
            Message msg = new Message();
            msg.what = 1;
            handler.sendMessage(msg);
        }
    }

    @Override
    public void run() {
        exportFile();
    }
}
