import grails.util.Environment
import org.iish.acquisition.domain.*
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.ldap.search.LdapUserSearch

/**
 * Initialization of the application.
 */
class BootStrap {
	LdapUserSearch ldapUserSearch

	def init = { servletContext ->
		populateTables()
		setUsersAndAuthorities()
		updateUserData(ldapUserSearch)
	}

	def destroy = {}

	/**
	 * Populates the database with necessary records, if the records do not exist yet.
	 */
	private static void populateTables() {
		[(Appraisal.YES_ID): 'yes',
		 (Appraisal.NO_ID) : 'no',
		 (Appraisal.NA_ID) : 'n.a'].
				each { Long id, String name ->
					if (!Appraisal.get(id)) {
						new Appraisal(id: id, name: name).save()
					}
				}

		[(Contract.YES_ID)          : 'yes',
		 (Contract.NA_ID)           : 'n.a (not applicable)',
		 (Contract.NOT_YET_THERE_ID): 'not yet there',
		 (Contract.UNKNOWN_ID)      : 'unknown'].
				each { Long id, String name ->
					if (!Contract.get(id)) {
						new Contract(id: id, name: name).save()
					}
				}

		[(Depot.FIFTH_FLOOR_ID)         : '5th floor',
		 (Depot.FOURTH_FLOOR_ID)        : '4th floor',
		 (Depot.THIRD_FLOOR_ID)         : '3rd floor',
		 (Depot.ZERO_FLOOR_ID)          : '0th floor',
		 (Depot.SORTEERRUIMTE_ID)       : 'Sorteerruimte',
		 (Depot.RANGEERTERREIN_ID)      : 'Rangeerterrein',
		 (Depot.BG_DEPOT_ID)            : 'B&G depot',
		 (Depot.DIGITAL_INGEST_DEPOT_ID): 'Digital ingest depot',
		 (Depot.REGIONAL_DESK_ID)       : 'Regional Desk',
		 (Depot.ELSEWHERE_ID)           : 'Elsewhere'].
				each { Long id, String name ->
					if (!Depot.get(id)) {
						new Depot(id: id, name: name).save()
					}
				}

		[(MaterialType.ARCHIVE_ID)      : [name: 'Archive', inMeters: true, inNumbers: false],
		 (MaterialType.BOOKS_ID)        : [name: 'Books', inMeters: true, inNumbers: true],
		 (MaterialType.PERIODICALS_ID)  : [name: 'Periodicals', inMeters: true, inNumbers: false],
		 (MaterialType.MOVING_IMAGES_ID): [name: 'Moving images', inMeters: false, inNumbers: true],
		 (MaterialType.EPHEMERA_ID)     : [name: 'Ephemera', inMeters: true, inNumbers: false],
		 (MaterialType.SOUND_ID)        : [name: 'Sound', inMeters: false, inNumbers: true],
		 (MaterialType.POSTERS_ID)      : [name: 'Posters', inMeters: false, inNumbers: true],
		 (MaterialType.DRAWINGS_ID)     : [name: 'Drawings', inMeters: false, inNumbers: true],
		 (MaterialType.PHOTOS_ID)       : [name: 'Photos', inMeters: false, inNumbers: true],
		 (MaterialType.OTHER_UNKNOWN_ID): [name: 'Other/Unknown', inMeters: false, inNumbers: true]].
				each { Long id, Map materialType ->
					if (!MaterialType.get(id)) {
						new MaterialType([id: id] + materialType).save()
					}
				}

		[(MiscMaterialType.DISKETTES_ID): 'Diskettes',
		 (MiscMaterialType.DVDS_CDS_ID) : 'DVD\'s / CD-rom\'s'].
				each { Long id, String name ->
					if (!MiscMaterialType.get(id)) {
						new MiscMaterialType(id: id, name: name).save()
					}
				}

		[(Status.NOT_PROCESSED_ID)    : 'Not processed',
		 (Status.IN_PROCESS_ID)       : 'In process',
		 (Status.PROCESSED_ID)        : 'Processed',
		 (Status.WONT_BE_PROCESSED_ID): 'Won\'t be processed'].
				each { Long id, String status ->
					if (!Status.get(id)) {
						new Status(id: id, status: status).save()
					}
				}

		[Authority.ROLE_SUPER_ADMIN, Authority.ROLE_ADMIN, Authority.ROLE_USER].
				each { String role ->
					if (!Authority.findByAuthority(role)) {
						new Authority(authority: role).save()
					}
				}
	}

	/**
	 * Creates or updates the users and their granted roles.
	 */
	private static void setUsersAndAuthorities() {
		['kerim.meijer'        : [role: Authority.ROLE_SUPER_ADMIN, mayReceiveEmail: false],
		 'marja.musson'        : [role: Authority.ROLE_SUPER_ADMIN, mayReceiveEmail: true],
		 'joke.zwaan'          : [role: Authority.ROLE_ADMIN, mayReceiveEmail: true],
		 'bouwe.hijma'         : [role: Authority.ROLE_USER, mayReceiveEmail: true],
		 'huub.sanders'        : [role: Authority.ROLE_USER, mayReceiveEmail: true],
		 'marien.vanderheijden': [role: Authority.ROLE_USER, mayReceiveEmail: true],
		 'frank.dejong'        : [role: Authority.ROLE_USER, mayReceiveEmail: true],
		 'jacques.vangerwen'   : [role: Authority.ROLE_USER, mayReceiveEmail: true],
		 'stefano.bellucci'    : [role: Authority.ROLE_USER, mayReceiveEmail: true],
		 'rossana.barragan'    : [role: Authority.ROLE_USER, mayReceiveEmail: true],
		 'eef.vermeij'         : [role: Authority.ROLE_USER, mayReceiveEmail: true],
		 'niels.beugeling'     : [role: Authority.ROLE_USER, mayReceiveEmail: true],
		 'job.schouten'        : [role: Authority.ROLE_USER, mayReceiveEmail: true],
		 'irina.novichenko'    : [role: Authority.ROLE_USER, mayReceiveEmail: true],
		 'zulfikar.ozdogan'    : [role: Authority.ROLE_USER, mayReceiveEmail: true],
		 'ed.kool'             : [role: Authority.ROLE_USER, mayReceiveEmail: true]].
				each { String login, Map userData ->
					User user = User.findByLogin(login)
					if (!user) {
						user = new User(login: login, mayReceiveEmail: userData.mayReceiveEmail)
						user.save(flush: true)
					}

					Authority authority = Authority.findByAuthority(userData.role.toString())
					if (!UserAuthority.exists(user.id, authority.id)) {
						UserAuthority.create(user, authority)
					}
				}
	}

	/**
	 * Updates the user data of all users with data from Active Directory.
	 * @param ldapUserSearch Allows us to search for users in Active Directory.
	 */
	private static void updateUserData(LdapUserSearch ldapUserSearch) {
		if (Environment.current != Environment.TEST) {
			User.list().each { User user ->
				DirContextOperations ctx = ldapUserSearch.searchForUser(user.login)
				user.update(ctx)
			}
		}
	}
}
