package net.octopvp.aetheriacoremaster.core;

import java.security.Principal;

import net.octopvp.aetheriacoremaster.repositories.UserRepository;
import net.octopvp.aetheriacoremaster.services.impl.UserDetailsImpl;
import net.octopvp.aetheriacoremaster.stereotypes.CurrentUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class CurrentUserHandlerMethodArgumentResolver {

	@Component
	public static class UserDetailsImplArgResolver implements HandlerMethodArgumentResolver {
		public boolean supportsParameter(MethodParameter methodParameter) {
			return methodParameter.getParameterAnnotation(CurrentUser.class) != null
					&& methodParameter.getParameterType().equals(UserDetailsImpl.class);
		}

		public Object resolveArgument(MethodParameter methodParameter,
									  ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
									  WebDataBinderFactory binderFactory) {
			if (this.supportsParameter(methodParameter)) {
				Principal principal = webRequest.getUserPrincipal();
				return ((Authentication) principal).getPrincipal();
			} else {
				return WebArgumentResolver.UNRESOLVED;
			}
		}
	}
	@Component
	public static class UserArgResolver implements HandlerMethodArgumentResolver {

		@Autowired
		private UserRepository userRepository;
		@Override
		public boolean supportsParameter(MethodParameter parameter) {
			return parameter.getParameterAnnotation(CurrentUser.class) != null
					&& parameter.getParameterType().equals(User.class);
		}

		@Override
		public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
			if (this.supportsParameter(parameter)) {
				Principal principal = webRequest.getUserPrincipal();
				return userRepository.findById(((UserDetailsImpl) ((Authentication) principal).getPrincipal()).getId()).orElse(null);
			} else {
				return WebArgumentResolver.UNRESOLVED;
			}
		}
	}
}
