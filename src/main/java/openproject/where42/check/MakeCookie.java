package openproject.where42.check;


import lombok.NoArgsConstructor;

import javax.servlet.http.Cookie;

@NoArgsConstructor
public class MakeCookie {

	public Cookie bakingCookie(String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setMaxAge(maxAge);
		cookie.setPath("/");
		return cookie;
	}

	public Cookie bakingMaxAge(String value, int maxAge) {
		return bakingCookie("max-age", value, maxAge);
	}

	public Cookie burnCookie(String name) {
		return bakingCookie(name, null, 0);
	}
}
