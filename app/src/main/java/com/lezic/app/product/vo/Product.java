package com.lezic.app.product.vo;

import com.lezic.core.orm.annotation.Column;
import com.lezic.core.orm.annotation.Id;
import com.lezic.core.orm.annotation.Table;

/**
 * Created by cielo on 2016/2/19 0019.
 */
@Table(name = "product")
public class Product {

    /**
     * 条码
     */
    @Id
    @Column(type = Column.Type.TEXT)
    private String code;

    /**
     * 名称
     */
    @Column(type = Column.Type.TEXT)
    private String name;

    /**
     * 价格
     */
    @Column(type = Column.Type.REAL)
    private Float price;

    public Product() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Product{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
