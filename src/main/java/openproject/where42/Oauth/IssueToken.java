package openproject.where42.Oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import openproject.where42.check.AES;
import openproject.where42.check.MakeCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;


@Controller
@RequiredArgsConstructor
public class IssueToken {

	static private AES aes = new AES();
	static private MakeCookie oven = new MakeCookie();
	static private TokenHttp tokenHttp = new TokenHttp();

	@GetMapping("/invalidRefreshToken")
	public String invalidRefreshToken(@CookieValue("refresh_token") String refreshToken, HttpServletResponse response) {

		System.out.println("======= invalidRefreshToken call =======");

		/*** refresh call ***/
		ResponseEntity<String> response2 = tokenHttp.resPostApi(
				tokenHttp.callRefreshHttp(aes.decoding(refreshToken)),
				tokenHttp.createUrl("https://api.intra.42.fr/oauth/token")
				);

		/*** parsing ***/
		ObjectMapper objectMapper = new ObjectMapper();
		OAuthToken oauthToken = null;
		try {
			oauthToken = objectMapper.readValue(
					response2.getBody(),
					OAuthToken.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

		/*** save cookie ***/
		response.addCookie(oven.bakingCookie("access_token", aes.encoding(oauthToken.getAccess_token()), 7200));
		response.addCookie(oven.bakingCookie("refresh_token", aes.encoding(oauthToken.getRefresh_token()), 1209600));
		response.addCookie(oven.bakingMaxAge("1209600", 1209600));
		
		return "redirect:/auth/login/member";
	}
}
