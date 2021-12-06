package com.android.designpattern.创建型.简单工厂;

public class ProductSimpleFactory {

    static AbstractProduct createProduct(Class<? extends AbstractProduct> product) {
        if (product.equals(ProductA.class)) {
            return new ProductA();
        } else if (product.equals(ProductB.class)) {
            return new ProductB();
        } else {
            throw new ClassCastException("class " + product.getSimpleName() + " is not extends " + AbstractProduct.class.getSimpleName());
        }
    }

}
