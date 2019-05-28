package com.ManageServices.service_interface;

import org.apache.ibatis.annotations.Param;

import java.util.Map;

public interface UserService {
    /**
     * 登陆
     * @param userName
     * @param pwd
     * @return Map{userId，expertId}
     *      若userId对应的value为null则说明登陆失败
     *      若expertId对应的value为null则说明不是专家
     */
    Map login(String userName, String pwd);

    /**
     * 返回用户基本信息，是用户不是专家调用
     * @param uid
     * @return 返回Map
     */
    Map selectUserByUid(int uid);

    /**
     * 创建新用户
     * @param userName
     * @param pwd
     * @param nickname
     * @param mail
     * @return 成功1，失败-1
     */
    int insertUser(String userName,String pwd,String nickname,String mail);

    /**
     * 重置密码
     * @param userId
     * @param pwd
     */
    int resetPwd(int userId, String pwd);

    /**
     * 更换头像
     * @param userId
     * @param iconPath
     *
     */
    int changeIcon(int userId, String iconPath);

    /**
     * 更换昵称
     * @param userId
     * @param nickname
     *
     */
    int changeNickname(int userId, String nickname);


    /**
     * 更改账户余额，
     * @param userId
     * @param increment 整数充值，负数消费
     * @return 1成功，-1失败
     */
    int updateBalance(int userId, int increment);


    /**
     * todo
     * 申请成为专家
     * @param userId
     * @param file
     * @param organization
     * @param name
     * @param tel
     * @param mail
     * @return
     */
    int beExpert(int userId, String file, String organization, String name, String tel, String mail);

    /**
     * todo
     * 认证已存在的专家
     * @param userId
     * @param field
     * @param organization
     * @param name
     * @param tel
     * @param mail
     * @return
     */
    int authorizeExpert(int userId, String field, String organization, String name, String tel, String mail);

}