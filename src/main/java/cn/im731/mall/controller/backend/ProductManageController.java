package cn.im731.mall.controller.backend;

import cn.im731.mall.common.Const;
import cn.im731.mall.common.ResponseCode;
import cn.im731.mall.common.ServerResponse;
import cn.im731.mall.pojo.Product;
import cn.im731.mall.pojo.User;
import cn.im731.mall.service.IFileService;
import cn.im731.mall.service.IProductService;
import cn.im731.mall.service.IUserService;
import cn.im731.mall.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/manage/product/")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private IProductService iProductService;

    @Autowired
    private IFileService iFileService;

    /**
     * 新增或修改
     * id为空是新增
     * id不为空是修改
     */
    @RequestMapping(value = "save.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登陆!");
        }
        //校验是否是管理员
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            //不是管理员
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        return iProductService.saveProduct(product);
    }

    /**
     * 设置商品状态信息
     * status
     */
    @RequestMapping(value = "set_sale_status.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登陆!");
        }
        //校验是否是管理员
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            //不是管理员
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        return iProductService.setSaleStatus(productId, status);
    }

    /**
     * 获取商品详情
     */
    @RequestMapping(value = "detail.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getDetail(HttpSession session, Integer productId) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登陆!");
        }
        //校验是否是管理员
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            //不是管理员
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        return iProductService.manageProductDetail(productId);
    }


    /**
     * 获取商品列表 ，带分页
     */
    @RequestMapping(value = "list.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登陆!");
        }
        //校验是否是管理员
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            //不是管理员
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        return iProductService.getProductList(pageNum, pageSize);
    }


    /**
     * 产品搜索
     * productName、productId均可以为空
     * 如果都为空，则列出所有
     */
    @RequestMapping(value = "search.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse productSearch(HttpSession session, String productName, Integer productId, @RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登陆!");
        }
        //校验是否是管理员
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            //不是管理员
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        return iProductService.searchProduct(productName, productId, pageNum, pageSize);
    }

    /**
     * 文件上传
     * 返回文件名和可访问的文件URL
     */
    @RequestMapping(value = "upload.do", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse upload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request) {
        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "未登陆!");
        }
        //校验是否是管理员
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            //不是管理员
            return ServerResponse.createByErrorMessage("需要管理员权限");
        }

        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file, path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

        Map fileMap = new HashMap();
        fileMap.put("uri", targetFileName);
        fileMap.put("url", url);

        return ServerResponse.createBySuccess(fileMap);
    }


    /**
     * 富文本上传
     * 使用simditor, 返回值规范：
     * {
     * "file_path": "",
     * "msg": "",
     * "success": true
     * }
     */
    @RequestMapping(value = "richtext_img_upload.do", method = RequestMethod.POST)
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file", required = false) MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map resultMap = new HashMap();

        User user = (User) session.getAttribute(Const.CURRENT_USER);
        if (user == null) {
            resultMap.put("success", false);
            resultMap.put("msg", "未登陆!");
            return resultMap;
        }
        //校验是否是管理员
        if (!iUserService.checkAdminRole(user).isSuccess()) {
            //不是管理员
            resultMap.put("success", false);
            resultMap.put("msg", "需要管理员权限!");
            return resultMap;
        }


        String path = request.getSession().getServletContext().getRealPath("upload");
        String targetFileName = iFileService.upload(file, path);
        String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + targetFileName;

        if (StringUtils.isBlank(targetFileName)) {
            //文件名为空
            resultMap.put("success", false);
            resultMap.put("msg", "上传失败");
            return resultMap;
        }

        resultMap.put("success", true);
        resultMap.put("msg", "上传成功");
        resultMap.put("file_path", url);

        //和前端的约定
        response.addHeader("Access-Control-Allow-Headers", "X-File-Name");

        return resultMap;
    }


}
