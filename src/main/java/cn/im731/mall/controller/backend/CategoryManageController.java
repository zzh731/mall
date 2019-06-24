package cn.im731.mall.controller.backend;

import cn.im731.mall.common.Const;
import cn.im731.mall.common.ResponseCode;
import cn.im731.mall.common.ServerResponse;
import cn.im731.mall.pojo.Category;
import cn.im731.mall.pojo.User;
import cn.im731.mall.service.ICategoryService;
import cn.im731.mall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/manage/category/")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    /**
     * 添加类目
     */
    @RequestMapping(value = "add_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> addCategory(HttpSession session, String categoryName, @RequestParam(value = "parentId", defaultValue = "0") int parentId ) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登陆！");
        }
        //校验是否是管理员
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            //不是管理员
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        return iCategoryService.addCategory(categoryName, parentId);
    }

    /**
     * 设置/修改分类名称
     */
    @RequestMapping(value = "set_category_name.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> setCategoryName(HttpSession session, Integer categoryId, String categoryName) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登陆！");
        }
        //校验是否是管理员
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            //不是管理员
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        return iCategoryService.updateCategoryName(categoryId, categoryName);
    }

    /**
     * 获取平级的子类目
     */
    @RequestMapping(value = "get_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Category>> getChildrenParallelCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登陆！");
        }
        //校验是否是管理员
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            //不是管理员
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }
        return iCategoryService.getChildrenParallelCategory(categoryId);
    }

    /**
     * 递归查询子节点
     */
    @RequestMapping(value = "get_deep_category.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<List<Integer>> getChildrenDeepCategory(HttpSession session, @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"未登陆！");
        }
        //校验是否是管理员
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            //不是管理员
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }
        return iCategoryService.selectCategoryAndChildrenById(categoryId);
    }


}
