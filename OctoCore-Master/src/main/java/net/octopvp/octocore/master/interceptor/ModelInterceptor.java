package net.octopvp.aetheriacoremaster.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.octopvp.aetheriacoremaster.models.UserModel;
import net.octopvp.aetheriacoremaster.services.impl.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.SmartView;
import org.springframework.web.servlet.View;

public class ModelInterceptor implements HandlerInterceptor {

    private static Logger log = LoggerFactory.getLogger(ModelInterceptor.class);

    /**
     * Executed before actual handler is executed
     **/
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        if (isUserLogged()) {
            addToModelUserDetails(request.getSession());
        }
        return true;
    }

    /**
     * Executed before after handler is executed. If view is a redirect view, we don't need to execute postHandle
     **/
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object object, ModelAndView model) throws Exception {
        if (model != null && !isRedirectView(model)) {
            if (isUserLogged()) {
                addToModelUserDetails(model);
            }
        }
    }

    /**
     * Used before model is generated, based on session
     */
    private void addToModelUserDetails(HttpSession session) {
        //UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Used when model is available
     */
    private void addToModelUserDetails(ModelAndView model) {
        //System.out.println("addToModelUserDetails");
        UserDetailsImpl user = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        model.addObject("username", user.getUsername());
        String profile = user.getProfilePicture();
        if (profile == null || profile.isEmpty()) profile = UserModel.DEFAULT_PROFILE_PICTURE;
        model.addObject("profilepicture", profile);
        boolean admin = user.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase("role_admin") || authority.getAuthority().equalsIgnoreCase("role_dev"));
        model.addObject("admin", admin);
        log.info("Admin: " + admin);

        boolean dev = System.getProperty("dev", "false").equalsIgnoreCase("true");
        model.addObject("isDevMode", dev);
    }

    public static boolean isRedirectView(ModelAndView mv) {

        String viewName = mv.getViewName();
        if (viewName.startsWith("redirect:/")) {
            return true;
        }

        View view = mv.getView();
        return (view != null && view instanceof SmartView && ((SmartView) view).isRedirectView());
    }

    public static boolean isUserLogged() {
        try {
            return !SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getName()
                    .equals("anonymousUser");
        } catch (Exception e) {
            return false;
        }
    }
}
