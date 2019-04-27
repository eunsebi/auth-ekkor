import xyz.ekkor.CustomSecurityEventListener
import xyz.ekkor.CustomUserDetailService
import xyz.ekkor.OldPasswordEncoder
import xyz.ekkor.UserPasswordEncoderListener
// Place your Spring DSL code here
beans = {
    userPasswordEncoderListener(UserPasswordEncoderListener)
    userDetailsService(CustomUserDetailService)
    securityEventListener(CustomSecurityEventListener)
    passwordEncoder(OldPasswordEncoder)
}
