package openproject.where42.token;

import lombok.RequiredArgsConstructor;
import openproject.where42.api.ApiService;
import openproject.where42.api.dto.OAuthToken;
import openproject.where42.token.TokenRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class TokenController {
	private final ApiService apiService;
	private final TokenRepository tokenRepository;
	HttpEntity<MultiValueMap<String, String>> req;
	ResponseEntity<String> res;

	@GetMapping("/admin")
	public String TokenAccess(@RequestParam("code") String code) {
		req = apiService.req42AdminHeader(code);
		res = apiService.resPostApi(req, apiService.req42TokenUri());
		OAuthToken oAuthToken = apiService.oAuthTokenMapping(res.getBody());
		tokenRepository.saveAdmin("admin", oAuthToken);
		return "success";
	}
}
