package openproject.where42.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("v1/auth/login")
    public String authLogin() {
        return "redirect:https://api.intra.42.fr/oauth/authorize?client_id=u-s4t2ud-6d1e73793782a2c15be3c0d2d507e679adeed16e50deafcdb85af92e91c30bd0&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fauth%2Flogin%2Fcallback&response_type=code";
    }
}
