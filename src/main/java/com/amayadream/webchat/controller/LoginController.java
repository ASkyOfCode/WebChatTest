package com.amayadream.webchat.controller;

import com.amayadream.webchat.pojo.Log;
import com.amayadream.webchat.pojo.User;
import com.amayadream.webchat.service.ILogService;
import com.amayadream.webchat.service.IUserService;
import com.amayadream.webchat.utils.CommonDate;
import com.amayadream.webchat.utils.LogUtil;
import com.amayadream.webchat.utils.NetUtil;
import com.amayadream.webchat.utils.WordDefined;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * NAME   :  WebChat/com.amayadream.webchat.controller
 * Author :  Amayadream
 * Date   :  2016.01.08 14:57
 * TODO   :  用户登录与注销
 */
@Controller
@RequestMapping(value = "user")
public class LoginController {
    @Resource private IUserService userService;
    //@Resource private Log log;
    @Resource private ILogService logService;

    @RequestMapping(value="register",method=RequestMethod.POST)
    public String register(HttpServletRequest request,RedirectAttributes attributes,WordDefined defined,
                           CommonDate date, LogUtil logUtil, NetUtil netUtil){
        System.out.println("Register is start");
        String uname = request.getParameter("uname");
        String upassword = request.getParameter("upassword");
        String uemail = request.getParameter("uemail");
        int sex =(Integer.parseInt(request.getParameter("sex").toString()));
        int age =(Integer.parseInt(request.getParameter("age").toString()));
        User user = userService.SelectUserByUserName(uname);
        if(user != null){
            attributes.addFlashAttribute("error",defined.REGISTER_EXIT);
            return "redirect:/register";
        }else{
            user = new User();
            user.setUemail(uemail);
            user.setNickname(uname);
            user.setPassword(upassword);
            user.setSex(sex);
            user.setAge(age);
            userService.insert(user);
            logService.insert(logUtil.setLog(user.getUserid(), date.getTime24(), defined.LOG_TYPE_LOGIN, defined.LOG_DETAIL_USER_LOGIN, netUtil.getIpAddress(request)));
        }
        return "redirect:/login";
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(String userid, String password, HttpSession session, RedirectAttributes attributes,
                        WordDefined defined, CommonDate date, LogUtil logUtil, NetUtil netUtil, HttpServletRequest request){
        User user = userService.selectUserByUserid(userid);
        if(user == null){
            attributes.addFlashAttribute("error", defined.LOGIN_USERID_ERROR);
            return "redirect:/login";
        }else{
            if(!user.getPassword().equals(password)){
                attributes.addFlashAttribute("error", defined.LOGIN_PASSWORD_ERROR);
                return "redirect:/login";
            }else{
                if(user.getStatus() != 1){
                    attributes.addFlashAttribute("error", defined.LOGIN_USERID_DISABLED);
                    return "redirect:/login";
                }else{
                    logService.insert(logUtil.setLog(userid, date.getTime24(), defined.LOG_TYPE_LOGIN, defined.LOG_DETAIL_USER_LOGIN, netUtil.getIpAddress(request)));
                    session.setAttribute("userid", userid);

                    if(user.getOnline() == 1){
                        attributes.addFlashAttribute("error",defined.LOGIN_AGAIN);
                        return "redirect:/login";
                    }
                    if(session.getAttribute("login_status") != null){
                         attributes.addFlashAttribute("error",defined.LOGIN_BROWER_AGAIN);
                         return "redirect:/login";
                    }
                    session.setAttribute("login_status", true);
                    user.setLasttime(date.getTime24());
                    user.setOnline(1);
                    userService.update(user);
                    attributes.addFlashAttribute("message", defined.LOGIN_SUCCESS);
                    return "redirect:/chat";
                }
            }
        }
    }

    @RequestMapping(value = "logout")
    public String logout(HttpSession session, RedirectAttributes attributes, WordDefined defined){
        //如果两个页面登录着同一个用户，一个注销后，另外一个也注销，的处理情况
        if(session.getAttribute("userid") != null) {
            User user = userService.selectUserByUserid(session.getAttribute("userid").toString());
            user.setOnline(0);
            userService.update(user);
            session.removeAttribute("userid");
            session.removeAttribute("login_status");
            attributes.addFlashAttribute("message", defined.LOGOUT_SUCCESS);
        }else{
            attributes.addFlashAttribute("message", defined.LOGOUT_AGAIN);
        }
        return "redirect:/login";
    }
}
