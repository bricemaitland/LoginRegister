package com.Brice.Authentication.controllers;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.Brice.Authentication.models.LoginUser;
import com.Brice.Authentication.models.User;
import com.Brice.Authentication.services.UserService;

@Controller
public class Users {
    private final UserService userService;
        
    public Users(UserService userService) {
        this.userService = userService;
    }
    
    @RequestMapping("/login")
    public String login(@ModelAttribute("user") User user, @ModelAttribute("loginUser") LoginUser loginUser) {
        return "loginPage.jsp";
    }
    
    @RequestMapping(value="/registration", method=RequestMethod.POST)
    public String register(@Valid @ModelAttribute("user") User newUser, BindingResult result, Model model, HttpSession session) {
        userService.registerUser(newUser, result);
        if(result.hasErrors()) {
            model.addAttribute("user", newUser);
            model.addAttribute("loginUser", new LoginUser());
            return "loginPage.jsp";
        }
        session.setAttribute("userId", newUser.getId());
        return "redirect:/home";
    }
    
    @RequestMapping(value="/login", method=RequestMethod.POST)
    public String login(@Valid @ModelAttribute("loginUser") LoginUser newLogin, BindingResult result, Model model, HttpSession session) {
        User user = userService.login(newLogin, result);
        if(result.hasErrors()) {
            model.addAttribute("loginUser", newLogin);
            model.addAttribute("user", new User());
            return "loginPage.jsp";
        }
        session.setAttribute("userId", user.getId());
        return "redirect:/home";
    }
    
    @RequestMapping("/home")
    public String home(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("userId");
        User u = userService.findUserById(userId);
        model.addAttribute("user", u);
        return "homePage.jsp";
    }
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}