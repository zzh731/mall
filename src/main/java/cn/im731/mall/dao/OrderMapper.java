package cn.im731.mall.dao;

import cn.im731.mall.pojo.Order;
import cn.im731.mall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    Order selectByOrderNo(Long OrderNo);

    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    int batchInsert(@Param("orderItemList")List<OrderItem> orderItemList);

    List<Order> selectByUserId(Integer userId);

    List<Order> selectAll();

}