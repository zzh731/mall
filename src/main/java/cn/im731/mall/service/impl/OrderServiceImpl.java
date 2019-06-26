package cn.im731.mall.service.impl;

import cn.im731.mall.common.Const;
import cn.im731.mall.common.ServerResponse;
import cn.im731.mall.dao.CartMapper;
import cn.im731.mall.dao.OrderMapper;
import cn.im731.mall.dao.ProductMapper;
import cn.im731.mall.pojo.Cart;
import cn.im731.mall.pojo.Order;
import cn.im731.mall.pojo.OrderItem;
import cn.im731.mall.pojo.Product;
import cn.im731.mall.service.IOrderService;
import cn.im731.mall.util.BigDecimalUtil;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Service
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    public ServerResponse createOrder(Integer userId, Integer shippingId) {
        //先选出购物车中勾选的商品
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);

        //生成订单快照
        ServerResponse serverResponse = getCartOrderItem(userId, cartList);

        //检查有无错误（如库存不足、商品不在售等）
        if (!serverResponse.isSuccess()) {
            //如果出错
            return serverResponse;
        }

        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();

        //计算订单总价
        BigDecimal orderTotalPrice = getOrderTotalPrice(orderItemList);

        //生成订单
        Order order = assembleOrder(userId, shippingId, orderTotalPrice);

        if (order == null) {
            return ServerResponse.createByErrorMessage("生成订单错误");
        }

        if (orderItemList == null) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }

        //将order生成的订单号赋值到每条orderItem中
        for (OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }

        //myBatis批量插入
        orderMapper.batchInsert(orderItemList);

        //减少库存
        reduceProductStock(orderItemList);

        //清理购物车
        cleanCart(cartList);

        //返回给前端数据

    }

    /**
     * 清理购物车
     * 清空？
     */
    private void cleanCart(List<Cart> cartList) {
        for (Cart cart : cartList) {
            cartMapper.deleteByPrimaryKey(cart.getId());
        }
    }

    /**
     * 减少库存
     */
    private void reduceProductStock(List<OrderItem> orderItemList) {
        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    /**
     *  生成订单
     */
    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal orderTotalPrice) {
        Order order = new Order();
        long orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);//运费
        order.setPaymentType(Const.PaymentTypeEnum.ONLINE_PAY.getCode());
        order.setPayment(orderTotalPrice);

        order.setUserId(userId);
        order.setShippingId(shippingId);
        //缺付款时间、发货时间等

        int effectedRows = orderMapper.insert(order);
        if (effectedRows > 0) {
            return order;
        }
        return null;

    }

    /**
     * 订单号生成器
     * 实际不应该仅使用时间戳、顺序序列号等有规律数字
     * 而且应该考虑以后分库分表的情况
     * 一期工程暂时使用时间戳
     * 可以考虑构建一个订单号缓存池，在前一天生成后一天的
     * 每用一个从缓存池中取一个
     */
    private long generateOrderNo() {
        long currentTime = System.currentTimeMillis();
        return currentTime + new Random().nextInt(100);//降低重复的概率
    }

    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal orderTotalPrice = new BigDecimal("0");
        for (OrderItem item : orderItemList) {
            orderTotalPrice = BigDecimalUtil.add(orderTotalPrice.doubleValue(), item.getTotalPrice().doubleValue());
        }
        return orderTotalPrice;
    }

    private ServerResponse getCartOrderItem(Integer userId, List<Cart> cartList) {
        List<OrderItem> orderItemList = new ArrayList<>();
        if (cartList == null) {
            return ServerResponse.createByErrorMessage("购物车为空");
        }

        //校验购物车的数据，包括产品的状态和数量
        for (Cart cart : cartList) {
            OrderItem orderItem = new OrderItem();
            Product product = productMapper.selectByPrimaryKey(cart.getProductId());
            if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
                //如果商品不在售
                return ServerResponse.createByErrorMessage("商品" + product.getName() + "不是在售状态（" + product.getStatus() + "）");
            }
            //校验库存
            if (cart.getQuantity() > product.getStock()) {
                return ServerResponse.createByErrorMessage("库存不足");
            }
            //校验通过，组装orderItem
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setCurrentUnitPrice(product.getPrice());//下单时购买价格
            orderItem.setQuantity(cart.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cart.getQuantity()));
            //加入到list
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }
}
