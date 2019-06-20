package cn.im731.mall.service.impl;

import cn.im731.mall.common.Const;
import cn.im731.mall.common.ServerResponse;
import cn.im731.mall.common.TokenCache;
import cn.im731.mall.dao.UserMapper;
import cn.im731.mall.pojo.User;
import cn.im731.mall.service.IUserService;
import cn.im731.mall.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.rmi.CORBA.Util;
import java.util.UUID;

@Service("iUserService")//名字是接口名首字母小写，即可在controller中注入
//@Service//TODO 不写名字能否注入？
public class UserSerivceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public ServerResponse<User> login(String username, String password) {
        //检查用户名是否存在
        int resultCount = userMapper.checkUsername(username);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        //密码经MD5加密
        String md5Password = MD5Util.MD5EncodeUtf8(password);

        User user = userMapper.selectLogin(username, md5Password);
        if (user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(StringUtils.EMPTY);//把密码置空，防止json返回
        return ServerResponse.createBySuccess("登陆成功", user);
    }

    @Override
    public ServerResponse<String> register(User user) {

        ServerResponse validResponse = null;

        //1. 检查用户名是否存在
        validResponse = this.checkValid(user.getUsername(), Const.USERNAME);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }

        //2.检查email是否存在
        validResponse = this.checkValid(user.getEmail(), Const.EMAIL);
        if (!validResponse.isSuccess()) {
            return validResponse;
        }

        //设置角色为普通用户
        user.setRole(Const.Role.ROLE_CUSTOMER);

        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));

        int resultCount = userMapper.insert(user);
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccessMessage("注册成功");


    }

    @Override
    public ServerResponse<String> checkValid(String str, String type) {
        if (StringUtils.isNotBlank(type)) { //isNotBlank会trim掉空格再判断是否为空,isNotEmpty不会
            //type不为空，执行检查
            if (Const.USERNAME.equals(type)) {
                //检查用户名
                int resultCount = userMapper.checkUsername(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("用户名已存在");
                }
            } else if (Const.EMAIL.equals(type)) {
                //检查email
                int resultCount = userMapper.checkEmail(str);
                if (resultCount > 0) {
                    return ServerResponse.createByErrorMessage("email已存在");
                }
            }
        } else {
            //type为空
            return ServerResponse.createByErrorMessage("参数错误");
        }
        return ServerResponse.createBySuccessMessage("校验成功");


    }

    @Override
    public ServerResponse<String> selectQuestion(String username) {
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            //用户名不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String question = userMapper.selectQuestion(username);
        if (StringUtils.isNotBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }
        //密码找回问题为空
        return ServerResponse.createByErrorMessage("密码找回问题为空");
    }

    @Override
    public ServerResponse<String> checkAnswer(String username, String question, String answer) {
        int resultCount = userMapper.checkAnswer(username, question, answer);
        if (resultCount > 0) {
            //问题匹配，答案正确
            String forgetToken = UUID.randomUUID().toString();
            //加入到缓存中
            TokenCache.setKey(TokenCache.TOKEN_PREFIX + username, forgetToken);
            return ServerResponse.createBySuccess(forgetToken);
        } else {
            return ServerResponse.createByErrorMessage("问题答案错误");
        }
    }

    @Override
    public ServerResponse<String> resetPasswordAsForget(String username, String passwordNew, String token) {
        //判空
        if (StringUtils.isBlank(token)) {
            return ServerResponse.createByErrorMessage("参数错误，token为空");
        }
        //检查用户名是否存在
        ServerResponse validResponse = this.checkValid(username, Const.USERNAME);
        if (validResponse.isSuccess()) {
            //用户名不存在
            return ServerResponse.createByErrorMessage("用户名不存在");
        }
        String tokenGet = TokenCache.getKey(TokenCache.TOKEN_PREFIX + username);
        if (StringUtils.isBlank(tokenGet)) {
            return ServerResponse.createByErrorMessage("token无效或过期，请重试");
        }
        if (StringUtils.equals(token, tokenGet)) {
            //token验证通过，正式重置密码
            String md5Password = MD5Util.MD5EncodeUtf8(passwordNew);
            int rowCount = userMapper.updatePasswordByUsername(username, md5Password);
            if (rowCount > 0) {
                return ServerResponse.createBySuccessMessage("修改密码成功");
            } else {
                return ServerResponse.createByErrorMessage("修改密码失败");
            }
        } else {
            //token 验证不通过
            return ServerResponse.createByErrorMessage("token无效或过期，请重试");
        }
    }

    /**
     *使用旧密码重置新密码
     */
    @Override
    public ServerResponse<String> resetPasswordUseOldPassword(String passwordOld, String passwordNew, User user) {
        //防止横向越权，需要校验此密码是否属于这个用户
        int resultCount = userMapper.checkPassword(user.getId(), MD5Util.MD5EncodeUtf8(passwordOld));
        if (resultCount == 0) {
            return ServerResponse.createByErrorMessage("旧密码错误");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(passwordNew));
        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if (resultCount > 0) {
            return ServerResponse.createBySuccessMessage("密码更新成功");
        }
        return ServerResponse.createByErrorMessage("密码更新失败");

    }

    /**
     * 更新用户个人信息
     * username不能被更新
     * email不能和别人的重复
     */
    @Override
    public ServerResponse<User> updateUserInfomation(User user) {
        int resultCount = userMapper.checkEmailValidByUserId(user.getEmail(), user.getId());
        if (resultCount > 0) {
            //查到了其他用户已经使用了这个email
            return ServerResponse.createByErrorMessage("email已被使用");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        resultCount = userMapper.updateByPrimaryKeySelective(user);
        if (resultCount > 0) {
            return ServerResponse.createBySuccess("更新个人信息成功", updateUser);
        }
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }
}
