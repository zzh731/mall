package cn.im731.mall.controller.portal;

import cn.im731.mall.common.ServerResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/order/")
public class OrderController {

    /**
     * 创建订单
     */
    @RequestMapping(value = "create.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse create(HttpSession session, Integer shippingId) {

    }
}
