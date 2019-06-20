package cn.im731.mall.service;

import cn.im731.mall.common.ServerResponse;
import cn.im731.mall.pojo.User;

public interface IUserService {

    ServerResponse<User> login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<String> checkValid(String str, String type);

    ServerResponse<String> selectQuestion(String username);

    ServerResponse<String> checkAnswer(String username, String question, String answer);

    ServerResponse<String> resetPasswordAsForget(String username, String passwordNew, String token);

    ServerResponse<String> resetPasswordUseOldPassword(String passwordOld, String passwordNew, User user);

    ServerResponse<User> updateUserInfomation(User user);
}
