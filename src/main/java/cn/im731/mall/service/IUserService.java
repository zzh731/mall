package cn.im731.mall.service;

import cn.im731.mall.common.ServerResponse;
import cn.im731.mall.pojo.User;
import cn.im731.mall.vo.ProductDetailVo;

public interface IUserService {

    //后台部分
    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse<String> selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> resetPasswordAsForget(String username, String passwordNew, String token);

    ServerResponse<String> resetPasswordUseOldPassword(String passwordOld, String passwordNew, User user);

    ServerResponse<User> updateUserInformation(User user);

    ServerResponse<User> getUserInformation(Integer userId);

    ServerResponse<String> checkAdminRole(User user);

    //前台部分

}
