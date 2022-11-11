package openproject.where42.check;

import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
public class CheckCookie {
	private static AES aes = new AES();
	static final private MakeCookie oven = new MakeCookie();
	
	@GetMapping("/checkCookie")
	public String checkCookie(@CookieValue(value = "access_token", required = false) String token,
							  @CookieValue(value = "refresh_token", required = false) String refreshToken) {
		if (token != null)
		{
			/*** 쿠키 삭제하는 부분도 어디 넣을지 생각하기***/
//			if (refreshToken == null)
//				return ""
			return "success main home";
		}
		if (refreshToken == null)
			return "redirect:https://api.intra.42.fr/oauth/authorize?client_id=u-s4t2ud-6d1e73793782a2c15be3c0d2d507e679adeed16e50deafcdb85af92e91c30bd0&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fauth%2Flogin%2Fcallback&response_type=code";
		return "redirect:/invalidRefreshToken";
	}

	@GetMapping("/registerCookie")
	public void register(HttpServletResponse response, CheckApi checkApi) {
		// 쿠키 만들고 등록
		response.addCookie(oven.bakingCookie("access_token", aes.encoding(checkApi.getAccess_token()), 7200));
		response.addCookie(oven.bakingCookie("access_token", aes.encoding(checkApi.getRefresh_token()), 1209600));
		response.addCookie(oven.bakingMaxAge("1209600", 1209600));
	}
}
