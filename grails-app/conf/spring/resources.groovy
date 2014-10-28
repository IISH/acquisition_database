import org.iish.acquisition.converter.BigDecimalValueConverter
import org.iish.acquisition.security.AcquisitionUserDetailsContextMapper
import org.iish.acquisition.security.AcquisitionUserDetailsService

beans = {
	userDetailsService(AcquisitionUserDetailsService) {
		grailsApplication = ref('grailsApplication')
	}

	ldapUserDetailsMapper(AcquisitionUserDetailsContextMapper)

	bigDecimalConverter(BigDecimalValueConverter)
}
