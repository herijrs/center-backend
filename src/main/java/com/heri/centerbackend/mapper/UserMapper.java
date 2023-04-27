package com.heri.centerbackend.mapper;

import com.heri.centerbackend.model.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author heri
* @description 针对表【user(用户)】的数据库操作Mapper
* @createDate 2023-04-20 18:47:03
* @Entity com.heri.centerbackend.model.User
*/
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




