package cn.im731.mall.common;

import com.google.common.collect.Sets;

import java.util.Set;

public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_asc", "price_desc");
    }

    public interface Cart {
        //购物车选中
        int CHECKED = 1;
        int UN_CHECKED = 0;

        //限制库存
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public interface Role {
        int ROLE_CUSTOMER = 0;  //普通用户
        int ROLE_ADMIN = 1; //管理员
    }

    public enum ProductStatusEnum {
        ON_SALE("在线", 1);


        private String value;
        private int code;

        ProductStatusEnum(String value, int code) {
            this.value = value;
            this.code = code;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum OrderStatusEnum {
        CANCELED("已取消", 0),
        NO_PAY("未付款", 10),
        PAID("已付款", 20),
        SHIPPED("已发货", 40),
        ORDER_SUCCESS("订单完成", 50),
        ORDER_CLOSE("订单关闭", 60)
        ;

        private String value;
        private int code;

        OrderStatusEnum(String value, int code) {
            this.value = value;
            this.code = code;
        }
        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum orderStatusEnum : values()) {
                if (orderStatusEnum.code == code) {
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的支付状态码："+code);
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }

    public enum PaymentTypeEnum {

        ONLINE_PAY("在线支付", 1);

        private String value;
        private int code;

        PaymentTypeEnum(String value, int code) {
            this.value = value;
            this.code = code;
        }

        public static PaymentTypeEnum codeOf(int code) {
            for (PaymentTypeEnum paymentTypeEnum : values()) {
                if (paymentTypeEnum.code == code) {
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的支付状态码："+code);
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }


}
