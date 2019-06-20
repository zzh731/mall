package cn.im731.mall.controller.backend;

import cn.im731.mall.common.Const;
import cn.im731.mall.common.ServerResponse;
import cn.im731.mall.pojo.User;
import cn.im731.mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user/")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            User user = response.getData();
            if (user.getRole() == Const.Role.ROLE_ADMIN) {
                //说明登陆的是管理员
                session.setAttribute(Const.CURRENT_USER, user);
                return response;
            } else {
                //不是管理员
                return ServerResponse.createByErrorMessage("没有管理员权限！");
            }
        } else {
            //用户名密码不对
            return response;
        }
    }
}
