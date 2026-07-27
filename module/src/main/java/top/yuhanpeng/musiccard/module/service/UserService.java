package top.yuhanpeng.musiccard.module.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import top.yuhanpeng.musiccard.module.domain.UserVO;
import top.yuhanpeng.musiccard.module.entity.User;
import top.yuhanpeng.musiccard.module.mapper.UserMapper;
import top.yuhanpeng.musiccard.module.utils.JwtUtil;
import top.yuhanpeng.musiccard.module.utils.SaltUtil;

import java.util.Random;

/**
 * 用户表
 *
 * @author YHP
 */
@Service
public class UserService {
    @Resource
    private UserMapper userMapper;

    public User getById(Long id) {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        User user = userMapper.getById(id);
        if (user == null) {
            throw new RuntimeException("user is null!");
        }
        return user;
    }

    public User extractById(Long id) {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        User user = userMapper.extractById(id);

        if (user == null) {
            throw new RuntimeException("user is null!");
        }
        return user;
    }

    public Long create(String phone, String password, String salt, String name, String avatar, Integer createTime, Integer updateTime, Byte isDeleted) {
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        User user = new User()
                .setPhone(phone)
                .setPassword(password)
                .setSalt(salt)
                .setName(name)
                .setAvatar(avatar)
                .setCreateTime(timeStamp)
                .setUpdateTime(timeStamp)
                .setIsDeleted(0);
        if (phone == null) {
            throw new RuntimeException("phone cannot be null!");
        }
        if (password == null) {
            throw new RuntimeException("password cannot be null!");
        }
        if (salt == null) {
            throw new RuntimeException("salt cannot be null!");
        }
        if (name == null) {
            throw new RuntimeException("name cannot be null!");
        }
        if (avatar == null) {
            throw new RuntimeException("avatar cannot be null!");
        }
        userMapper.insert(user);
        return user.getId();
    }

    public Long update(Long id, String phone, String password, String salt, String name, String avatar, Integer createTime, Integer updateTime, Byte isDeleted) {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        User user = new User()
                .setId(id)
                .setPhone(phone)
                .setPassword(password)
                .setSalt(salt)
                .setName(name)
                .setAvatar(avatar)
                .setCreateTime(timeStamp)
                .setUpdateTime(timeStamp)
                .setIsDeleted(0);
        if (userMapper.extractById(id) == null) {
            throw new RuntimeException("cannot find the id");
        }
        return (long) userMapper.update(user);
    }

    public Long edit(Long id, String phone, String password, String salt, String name, String avatar, Integer createTime, Integer updateTime, Byte isDeleted) {
        Long res;
        if (id != null) {
            res = update(id, phone, password, salt, name, avatar, createTime, updateTime, isDeleted);
            if (res == 0) {
                throw new RuntimeException("update fail!");
            }
        } else {
            res = create(phone, password, salt, name, avatar, createTime, updateTime, isDeleted);
            if (res == null) {
                throw new RuntimeException("create fail!");
            }
        }
        return res;
    }

    public Integer delete(Long id) {
        if (id == null) {
            throw new RuntimeException("id cannot be null!");
        }
        int timeStamp = (int) (System.currentTimeMillis() / 1000);
        return userMapper.delete(timeStamp, id);
    }

    public String login(String phone, String password) throws JsonProcessingException {
        User user = userMapper.extractByPhone(phone);
        if (user == null) {
            throw new RuntimeException("手机号未注册");
        }
        String encodePassword = DigestUtil.md5Hex(password + user.getSalt());
        if (!encodePassword.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        UserVO userVO = new UserVO().setUserId(user.getId());
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(userVO);
        String token = JwtUtil.createToken(json);
        return token;
    }

    public String adminLogin(String phone, String password, HttpSession session, HttpServletResponse response) throws JsonProcessingException {
        User user = userMapper.extractByPhone(phone);
        if (user == null) {
            throw new RuntimeException("手机号未注册");
        }
        String encodePassword = DigestUtil.md5Hex(password + user.getSalt());
        if (!encodePassword.equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        UserVO userVO = new UserVO().setUserId(user.getId());
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(userVO);
        String token = JwtUtil.createToken(json);
        Cookie cookie = new Cookie("sign", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(cookie);
        session.setAttribute("user", json);
        return token;
    }

    public String register(String phone, String password, String name, String avatar) throws Exception {
        User exist = userMapper.extractByPhone(phone);
        if (exist != null) {
            throw new RuntimeException("手机号已经注册");
        }
        String salt = SaltUtil.generateSalt(new Random().nextInt(10) + 6);
        String encodePassword = DigestUtil.md5Hex(password + salt);
        if (name == null) {
            name = "用户" + phone;
        }
        User user = new User()
                .setPhone(phone)
                .setPassword(encodePassword)
                .setSalt(salt)
                .setName(name)
                .setAvatar(avatar)
                .setCreateTime((int) (System.currentTimeMillis() / 1000))
                .setUpdateTime((int) (System.currentTimeMillis() / 1000))
                .setIsDeleted(0);
        Long id;
        try {
            id = userMapper.insert(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        UserVO userVO = new UserVO().setUserId(id);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(userVO);
        String token = JwtUtil.createToken(json);
        return token;
    }
}