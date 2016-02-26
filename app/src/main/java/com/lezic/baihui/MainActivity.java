package com.lezic.baihui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lezic.app.product.service.ProductService;
import com.lezic.app.product.task.ExportThread;
import com.lezic.app.product.vo.Product;
import com.lezic.core.util.UtilData;
import com.lezic.core.util.view.UtilConfirmDialog;

public class MainActivity extends AppCompatActivity {

    /**
     * 条码
     */
    private EditText editTextCode;

    /**
     * 价格
     */
    private EditText editTextPrice;

    /**
     * 名称
     */
    private EditText editTextName;

    /**
     * 商品服务类
     */
    private ProductService productService;

    /**
     * 进度条
     */
    private ProgressDialog progressDialogInitData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //输入框
        editTextCode = (EditText) findViewById(R.id.editTextCode);
        editTextPrice = (EditText) findViewById(R.id.editTextPrice);
        editTextName = (EditText) findViewById(R.id.editTextName);

        //单击事件
        findViewById(R.id.btnScan).setOnClickListener(doScan);
        findViewById(R.id.btnSave).setOnClickListener(doSave);
        findViewById(R.id.btnReset).setOnClickListener(doReset);
        findViewById(R.id.btnImport).setOnClickListener(doImport);
        findViewById(R.id.btnExport).setOnClickListener(doExport);
        editTextCode.setOnFocusChangeListener(onEditTextCodeChange);

        productService = new ProductService(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private final View.OnFocusChangeListener onEditTextCodeChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View arg0, boolean arg1) {
            if (arg0.getId() == R.id.editTextCode && arg1 == false) {//离开条码输入框
                initProduct();
            }
        }
    };

    /**
     * 扫描
     */
    private final Button.OnClickListener doScan = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

        }

    };

    /**
     * 扫描回调方法
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    }

    /**
     * 保存
     */
    private final Button.OnClickListener doSave = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (UtilData.isNull(editTextCode.getText().toString())) {
                editTextCode.requestFocus();
                return;
            }
            if (UtilData.isNull(editTextPrice.getText().toString())) {
                editTextPrice.requestFocus();
                return;
            }

            try {
                Product entity = new Product();
                entity.setCode(editTextCode.getText().toString());
                entity.setPrice(UtilData.floatOfString(editTextPrice.getText().toString()));
                entity.setName(editTextName.getText().toString());
                productService.save(entity);
                UtilConfirmDialog.showDialog(MainActivity.this, R.string.dialog_title, "已保存，是否清空？",
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cleanProduct();
                            }
                        });
            } catch (Exception e) {
                Toast toast = Toast.makeText(getApplicationContext(), "对不起，保存失败！", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

        }

    };

    /**
     * 清空
     */
    private final Button.OnClickListener doReset = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            cleanProduct();
        }

    };

    /**
     * 导入
     */
    private final Button.OnClickListener doImport = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {

        }

    };

    /**
     * 导出
     */
    private final Button.OnClickListener doExport = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            //创建ProgressDialog对象
            progressDialogInitData = new ProgressDialog(MainActivity.this);
            // 设置进度条风格
            progressDialogInitData.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // 设置ProgressDialog 标题
            progressDialogInitData.setTitle("导出数据");
            // 设置ProgressDialog提示信息
            progressDialogInitData.setMessage("处理中...");
            // 设置ProgressDialog标题图标
//            progressDialogInitData.setIcon(R.drawable.img2);
            // 设置ProgressDialog 的进度条是否不明确 false 就是不设置为不明确
            progressDialogInitData.setIndeterminate(false);
            // 设置ProgressDialog 是否可以按退回键取消
            progressDialogInitData.setCancelable(true);
            // 让ProgressDialog显示
            progressDialogInitData.setMax(9999);
            progressDialogInitData.show();
            try {
                new ExportThread(progressDialogInitData, productService, new Handler() {
                    public void handleMessage(android.os.Message msg) {
                        String s = "";
                        switch (msg.what) {
                            case 0:
                                s = "导出成功！保存在：" + ExportThread.filePath + "/" + ExportThread.fileName;
                                break;
                            case 1:
                                s = "对不起，导出到" + ExportThread.filePath + "/" + ExportThread.fileName + "失败！";
                                break;
                            case 2:
                                s = "导出失败！没有记录可保存保存！";
                                break;
                        }
                        UtilConfirmDialog.showDialog(MainActivity.this, R.string.dialog_title, s,
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                        progressDialogInitData.cancel();
                                    }
                                });
                    }
                }).start();
            } catch (Exception e) {
                Toast toast = Toast.makeText(getApplicationContext(), "对不起，系统错误！", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }

    };

    /**
     * 获取商品信息
     */
    private void initProduct() {
        try {
            Product entity = productService.get(editTextCode.getText().toString());
            if (entity != null) {
                editTextName.setText(entity.getName());
                editTextCode.setText(entity.getCode());
                editTextPrice.setText(entity.getPrice() + "");
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "对不起，找不到记录！", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), "对不起，系统错误！", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    /**
     * 清空表单
     */
    private void cleanProduct() {
        editTextCode.setText("");
        editTextPrice.setText("");
        editTextName.setText("");
        editTextCode.requestFocus();
    }
}
