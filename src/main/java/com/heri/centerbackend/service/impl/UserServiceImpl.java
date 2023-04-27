package com.heri.centerbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heri.centerbackend.common.ErrorCode;
import com.heri.centerbackend.exception.BusinessException;
import com.heri.centerbackend.model.User;
import com.heri.centerbackend.service.UserService;
import com.heri.centerbackend.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.heri.centerbackend.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author heri
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2023-04-20 18:47:03
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    //盐值，混淆密码
    private static final String SALT = "heri";

    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度小于4");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度小于8");
        }
        // 账户不能包含特殊字符
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\\\[\\\\].·<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if (matcher.matches()) {
            return -1;
        }
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("userAccount", userAccount);
        userQueryWrapper.eq("userPassword", userPassword);
        long count = userMapper.selectCount(userQueryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 加密
        String digestPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        User originUser = new User();
        originUser.setUserAccount(userAccount);
        originUser.setUserPassword(digestPassword);
        int result = userMapper.insert(originUser);
        if (result < 0) {
            return -1;
        }
        return originUser.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1、校验用户密码是否合法
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        // 账户不能包含特殊字符
        String regEx = "[ _`~!@#$%^&*()+=|{}':;',\\\\[\\\\].·<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]|\\n|\\r|\\t]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if (matcher.matches()) {
            return null;
        }
        // 2、加密
        String digestPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> originUserQueryWrapper = new QueryWrapper<>();
        originUserQueryWrapper.eq("userAccount", userAccount);
        originUserQueryWrapper.eq("userPassword", digestPassword);
        User user = userMapper.selectOne(originUserQueryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("originUser login failed, originUserAccount cannot match originUserPassword");
            return null;
        }
        // 3、用户信息脱敏
        User safetyUser = getSafetyUser(user);
        // 4、记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 用户信息脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        return safetyUser;

    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

}




