import grails.util.Environment
import org.apache.log4j.Logger
import org.iish.acquisition.domain.*
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.ldap.search.LdapUserSearch

/**
 * Initialization of the application.
 */
class BootStrap {

	static final Logger log = Logger.getLogger(this.class)
	LdapUserSearch ldapUserSearch
	def grailsApplication

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
		 (Depot.COLD_STORAGE_ID)        : 'Cold storage',
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
		 (MiscMaterialType.DVDS_CDS_ID) : 'DVDs / CD-roms'].
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

		[Authority.ROLE_ADMIN, Authority.ROLE_USER,
		 Authority.ROLE_OFFLOADER_1, Authority.ROLE_OFFLOADER_2, Authority.ROLE_OFFLOADER_3].
				each { String role ->
					if (!Authority.findByAuthority(role)) {
						new Authority(authority: role).save()
					}
				}

		[
		 (DigitalMaterialStatusCode.NEW_DIGITAL_MATERIAL_COLLECTION) : [status: '1. No folder created yet', isSetByUser: false],
		 (DigitalMaterialStatusCode.FOLDER_CREATION_RUNNING)         : [status: '2. A folder is being created', isSetByUser: false],
		 (DigitalMaterialStatusCode.FOLDER_CREATED)                  : [status: '3. A folder has been created', isSetByUser: false],
		 (DigitalMaterialStatusCode.MATERIAL_UPLOADED)               : [status: '4. Digital material has been uploaded, request creation of backup', isSetByUser: true],
		 (DigitalMaterialStatusCode.BACKUP_RUNNING)                  : [status: '5. A backup of the digital material is being made', isSetByUser: false],
		 (DigitalMaterialStatusCode.BACKUP_FINISHED)                 : [status: '6. A backup of the digital material has been created', isSetByUser: false],
		 (DigitalMaterialStatusCode.READY_FOR_RESTORE)               : [status: '7. Request restore of the digital material', isSetByUser: true],
		 (DigitalMaterialStatusCode.RESTORE_RUNNING)                 : [status: '8. A restore of the digital material is being performed', isSetByUser: false],
		 (DigitalMaterialStatusCode.RESTORE_FINISHED)                : [status: '9. A restore of the digital material has been done', isSetByUser: false],
		 (DigitalMaterialStatusCode.READY_FOR_PERMANENT_STORAGE)     : [status: '10. Digital material is ready for permanent storage (SOR)', isSetByUser: true],
		 (DigitalMaterialStatusCode.UPLOADING_TO_PERMANENT_STORAGE)  : [status: '11. Digital material is being uploaded to permanent storage (SOR)', isSetByUser: false],
		 (DigitalMaterialStatusCode.MOVED_TO_PERMANENT_STORAGE)      : [status: '12. Digital material has been moved to permanent storage (SOR)', isSetByUser: false]
		].
				each { Long id, Map statusInfo ->
					if (!DigitalMaterialStatusCode.get(id)) {
						DigitalMaterialStatusCode digitalMaterialStatusCode = new DigitalMaterialStatusCode(statusInfo)
						digitalMaterialStatusCode.setId(id)
						digitalMaterialStatusCode.save()
					}
				}
	}

	/**
	 * Creates or updates the users and their granted roles.
	 */
	private void setUsersAndAuthorities() {
		grailsApplication.config.role_admin.split(',').each {
			addRole(it, Authority.ROLE_ADMIN)
		}
		grailsApplication.config.role_user.split(',').each {
			addRole(it, Authority.ROLE_USER)
		}
	}

	private static void addRole(String login, String _authority) {

		User user = User.findByLogin(login)
		def userData = [roles: [_authority], mayReceiveEmail: true]
		if (!user) {
			user = new User(login: login, mayReceiveEmail: userData.mayReceiveEmail)
			user.save(flush: true)
		}

		userData.roles.each { String role ->
			Authority authority = Authority.findByAuthority(role)
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
				DirContextOperations ctx;
				try {
					ctx = ldapUserSearch.searchForUser(user.login)
					if (ctx) user.update(ctx)
				} catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
					log.error(e)
				}
			}
		}
	}
}
