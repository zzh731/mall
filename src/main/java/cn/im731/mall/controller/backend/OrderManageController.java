package cn.im731.mall.controller.backend;

import cn.im731.mall.common.Const;
import cn.im731.mall.common.ResponseCode;
import cn.im731.mall.common.ServerResponse;
import cn.im731.mall.pojo.User;
import cn.im731.mall.service.IOrderService;
import cn.im731.mall.service.IUserService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order/")
public class OrderManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;

    /**
     * 管理员查看所有订单
     */
    @RequestMapping(value = "list.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<PageInfo> getOrderList(HttpSession session,
                                                 @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                 @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登陆!");
        }
        //校验是否是管理员
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            //不是管理员
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        return iOrderService.manageList(pageNum, pageSize);
    }

    /**
     * 管理员查看订单详情
     */
    @RequestMapping(value = "detail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getOrderDetail(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登陆!");
        }
        //校验是否是管理员
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            //不是管理员
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        return iOrderService.manageDetail(orderNo);
    }

    /**
     * 按订单号搜索订单
     * 需要管理员权限
     */
    @RequestMapping(value = "search.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getOrderByOrderNo(HttpSession session, Long orderNo,
                                            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登陆!");
        }
        //校验是否是管理员
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            //不是管理员
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        return iOrderService.manageSearch(orderNo, pageNum, pageSize);
    }

    /***
     * 发货
     */
    @RequestMapping(value = "send_goods.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse orderSendGoods(HttpSession session, Long orderNo) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登陆!");
        }
        //校验是否是管理员
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            //不是管理员
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        return iOrderService.manageSendGoods(orderNo);
    }


}
