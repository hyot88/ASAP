package com.fourtwod.web;

import com.fourtwod.config.auth.LoginUser;
import com.fourtwod.config.auth.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RequiredArgsConstructor
@Controller
public class IndexController {

    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user) {
        if (user != null ) {
            model.addAttribute("nickname", user.getNickname());

            if (user.getNickname() != null) {
                return "index";
            } else {
                return "redirect:/nickname";
            }
        } else {
            return "login";
        }
    }

    @GetMapping("/nickname")
    public String nickname(Model model, @LoginUser SessionUser user) {
        if (user != null) {
            if (user.getNickname() != null) {
                return "redirect:/";
            }

            return "nickname";
        } else {
            return "redirect:/";
        }
    }
}
