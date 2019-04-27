package xyz.ekkor

import grails.core.GrailsApplication
import org.grails.web.util.WebUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationListener
import org.springframework.security.authentication.event.AuthenticationSuccessEvent

class CustomSecurityEventListener implements ApplicationListener<AuthenticationSuccessEvent>{

    @Autowired
    GrailsApplication grailsApplication

    @Autowired
    UserService userService

    @Override
    void onApplicationEvent(AuthenticationSuccessEvent event) {
        CustomUserDetail userDetail = event.authentication.principal

        User userInstance = User.load(userDetail.id)

        def remoteAddress = userService.getRealIp(WebUtils.retrieveGrailsWebRequest().request)

        //Login Log 저장
        new LoggedIn(user: userInstance, remoteAddr: remoteAddress).save(flush: true)

       /*
       def rememberMeConfig = SpringSecurityUtils.securityConfig.rememberMe
       def params = WebUtils.retrieveGrailsWebRequest().params

       // Remember me를 활성화 안하고, Remember me 쿠키가 기존에 없으면 Login 유지를 위한 임시 Cookie 생성
       // Cookie 데이터는 Remember me 와 동일 단 MaxAge -1 설정으로 브라우저 종료시 삭제
       if(!params[rememberMeConfig.parameter]) {

           String cookieName = rememberMeConfig.cookieName + "_"
           String rememberMe = cookieService.getCookie(cookieName)

           if(!rememberMe) {

               Integer expirationTime = -1
               String key = rememberMeConfig.key

               String username = "${userInstance.username}:${expirationTime}"
               String password = "${userInstance.username}:${expirationTime}:${userInstance.password}:${key}".encodeAsSHA256()

               rememberMe = "${username}:${password}".encodeAsBase64()

               cookieService.setCookie(cookieName, rememberMe, expirationTime)
           }
       }*/
    }
}
