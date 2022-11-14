package openproject.where42.check;

import openproject.where42.Oauth.TokenDao;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;

public class CheckCookie {
	private static AES aes = new AES();
	static final private MakeCookie oven = new MakeCookie();
}
