import org.iish.acquisition.security.AcquisitionUserDetailsContextMapper
import org.iish.acquisition.security.AcquisitionUserDetailsService

beans = {
	userDetailsService(AcquisitionUserDetailsService) {
		grailsApplication = ref('grailsApplication')
	}

	ldapUserDetailsMapper(AcquisitionUserDetailsContextMapper)
}
