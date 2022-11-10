package openproject.where42.check;

import org.springframework.stereotype.Controller;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;

@Controller
public class CheckCookie {
	private static AES aes = new AES();

	@GetMapping("/checkCookie")
	public String checkCookie(@CookieValue(value = "access_token", required = false) String token,
							  @CookieValue(value = "refresh_token", required = false) String refreshToken) {
		if (token != null)
		{
//			if (refreshToken == null)
//				invalidToken(token); ==> 다 파기하고 다시 받는게 나을지도
			return "/main_test";
		}
		if (refreshToken == null) {}
			// uninvalid(token, refreshToken);
		return "/invalidRefreshToken";
//		return "redirect:https://api.intra.42.fr/oauth/authorize?client_id=u-s4t2ud-6d1e73793782a2c15be3c0d2d507e679adeed16e50deafcdb85af92e91c30bd0&redirect_uri=http%3A%2F%2Flocalhost%3A8080%2Fauth%2Flogin%2Fcallback&response_type=code";
	}

	@GetMapping("/registerCookie")
	public void register(HttpServletResponse response, CheckApi checkApi) {

		// cookie 입력
		javax.servlet.http.Cookie cookie2 = new javax.servlet.http.Cookie("access_token", aes.encoding(checkApi.getAccess_token()));
		cookie2.setMaxAge(7600);
		cookie2.setPath("/");
		response.addCookie(cookie2);

		javax.servlet.http.Cookie cookie3 = new javax.servlet.http.Cookie("refresh_token", aes.encoding(checkApi.getRefresh_token()));
		cookie3.setMaxAge(1209600);
		cookie3.setPath("/");
		response.addCookie(cookie3);

		javax.servlet.http.Cookie cookie4 = new javax.servlet.http.Cookie("max-age", "2-weeks");
		cookie4.setMaxAge(1209600);
		cookie4.setPath("/");
		// 쿠키 등록
		response.addCookie(cookie4);
	}
}
