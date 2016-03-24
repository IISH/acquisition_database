import grails.util.Environment
import groovy.sql.Sql
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.iish.acquisition.domain.*
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.ldap.search.LdapUserSearch

import javax.sql.DataSource
import java.sql.SQLException

/**
 * Initialization of the application.
 */
class BootStrap {
	static final Logger LOGGER = Logger.getLogger(this.class)

	DataSource dataSource
	LdapUserSearch ldapUserSearch
	GrailsApplication grailsApplication

	def init = { servletContext ->
		populateTables()
		createFulltextIndexes()
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

		[(DigitalMaterialStatusCode.FOLDER)     : [status: '1. Folder creation', isSetByUser: false, confirmRequired: false, dependsOn: null, needsAuthority: null],
		 (DigitalMaterialStatusCode.BACKUP)     : [status: '2. Create backup', isSetByUser: true, confirmRequired: false, dependsOn: DigitalMaterialStatusCode.FOLDER, needsAuthority: Authority.ROLE_OFFLOADER_1],
		 (DigitalMaterialStatusCode.RESTORE)    : [status: '3. Start restore', isSetByUser: true, confirmRequired: true, dependsOn: DigitalMaterialStatusCode.BACKUP, needsAuthority: Authority.ROLE_OFFLOADER_2],
		 (DigitalMaterialStatusCode.STAGINGAREA): [status: '4. Move to stagingarea', isSetByUser: true, confirmRequired: false, dependsOn: DigitalMaterialStatusCode.BACKUP, needsAuthority: Authority.ROLE_OFFLOADER_2],
		 (DigitalMaterialStatusCode.SOR)        : [status: '5. Process in SOR', isSetByUser: false, confirmRequired: false, dependsOn: DigitalMaterialStatusCode.STAGINGAREA, needsAuthority: null],
		 (DigitalMaterialStatusCode.CLEANUP)    : [status: '6. Cleaning up', isSetByUser: false, confirmRequired: false, dependsOn: DigitalMaterialStatusCode.SOR, needsAuthority: null]].
				each { Long id, Map statusInfo ->
					if (!DigitalMaterialStatusCode.get(id)) {
						DigitalMaterialStatusCode digitalMaterialStatusCode = new DigitalMaterialStatusCode(
								status: statusInfo.status,
								isSetByUser: statusInfo.isSetByUser,
								confirmRequired: statusInfo.confirmRequired,
								dependsOn: (statusInfo.dependsOn)
										? DigitalMaterialStatusCode.get(statusInfo.dependsOn)
										: null,
								needsAuthority: (statusInfo.needsAuthority)
										? Authority.findByAuthority(statusInfo.needsAuthority)
										: null
						)
						digitalMaterialStatusCode.setId(id)
						digitalMaterialStatusCode.save()
					}
				}
	}

	/**
	 * Creates the MySQL fulltext indexes.
	 */
	private void createFulltextIndexes() {
		try {
			Sql sql = new Sql(dataSource)
			sql.execute('CREATE FULLTEXT INDEX collections_fulltext ON collections ' +
					'(name, content, lists_available, to_be_done, owner, ' +
					'contact_person, remarks, original_package_transport)')
			sql.execute('CREATE FULLTEXT INDEX locations_fulltext ON locations (cabinet)')
		}
		catch (SQLException sqle) {
			// Already created the index, ignore error
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

	/**
	 * Create a user with the given login name and role.
	 * @param login The login name.
	 * @param role The authority.
	 */
	private static void addRole(String login, String role) {
		User user = User.findByLogin(login)
		Map userData = [roles: [role], mayReceiveEmail: true]
		if (!user) {
			user = new User(login: login, mayReceiveEmail: userData.mayReceiveEmail)
			user.save(flush: true)
		}

		userData.roles.each { String auth ->
			Authority authority = Authority.findByAuthority(auth)
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
		if (Environment.current == Environment.PRODUCTION) {
			User.list().each { User user ->
				DirContextOperations ctx;
				try {
					ctx = ldapUserSearch.searchForUser(user.login)
					if (ctx) {
                        LOGGER.info('Updating user ' + user.login);
						user.update(ctx)
					}
				}
				catch (UsernameNotFoundException e) {
					LOGGER.error(e)
				}
			}
		}
	}
}
