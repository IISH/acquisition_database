import grails.plugin.springsecurity.SpringSecurityUtils
import grails.util.Environment
import org.iish.acquisition.domain.Authority

// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

// change this to alter the default package name and Maven publishing destination
grails.project.groupId = 'org.iish.acquisition'

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings
// (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [ // the first one is the default format
		all          : '*/*', // 'all' maps to '*' or the first available format in withFormat
		atom         : 'application/atom+xml',
		css          : 'text/css',
		csv          : 'text/csv',
		form         : 'application/x-www-form-urlencoded',
		html         : ['text/html', 'application/xhtml+xml'],
		js           : 'text/javascript',
		json         : ['application/json', 'text/json'],
		multipartForm: 'multipart/form-data',
		rss          : 'application/rss+xml',
		text         : 'text/plain',
		hal          : ['application/hal+json', 'application/hal+xml'],
		xml          : ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
	views {
		gsp {
			encoding = 'UTF-8'
			htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
			codecs {
				expression = 'html' // escapes values inside ${}
				scriptlet = 'html' // escapes output from scriptlets in GSPs
				taglib = 'none' // escapes output from taglibs
				staticparts = 'none' // escapes output from static template parts
			}
		}
		// escapes all not-encoded output at final stage of outputting
		// filteringCodecForContentType.'text/html' = 'html'
	}
}

grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart = false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false

// Default mapping configuration
grails.gorm.default.mapping = {
	version false
}

grails.databinding.dateFormats = ['dd/MM/yyyy']

grails.assets.plugin."twitter-bootstrap".excludes = ["**/*.less"]
grails.assets.plugin."twitter-bootstrap".includes = ["bootstrap.less"]

grails.plugins.twitterbootstrap.fixtaglib = true

// Make sure grails.config.locations is initialized
if (!grails.config.locations || !(grails.config.locations instanceof Collection)) {
	grails.config.locations = []
}

if (Environment.current != Environment.TEST) {
	// Load properties, like passwords, from another location
	if (System.properties.containsKey('acquisition.properties')) {
		println('Loading properties from ' + System.properties['acquisition.properties'])
		grails.config.locations << 'file:' + System.properties['acquisition.properties']
	}
	else if (System.getenv()?.containsKey('ACQUISITION')) {
		println('Loading properties from ' + System.getenv().get('ACQUISITION'))
		grails.config.locations << 'file:' + System.getenv().get('ACQUISITION')
	}
	else {
		println('FATAL: no acquisition.properties file set in VM or Environment. \n \
Add a -Dacquisition.properties=/path/to/acquisition.properties argument when starting this application. \n \
Or set a ACQUISITION=/path/to/acquisition.properties as environment variable.')
		System.exit(-1)
	}
}

environments {
	development {
		grails.logging.jul.usebridge = true
	}
	test {
		grails.plugin.springsecurity.active = false
	}
	production {
		grails.logging.jul.usebridge = false
	}
}

// log4j configuration
// We assume the production environment is running in an tomcat container. If not we use the application path's
// target folder.
final String catalinaBase = System.properties.getProperty('catalina.base', './target') + '/logs'
File logFile = new File(catalinaBase)
logFile.mkdirs()
println('log directory: ' + logFile.absolutePath)

String loglevel = System.properties.getProperty('loglevel', 'warn')
log4j = {
	appenders {
		console name: 'StackTrace'
		rollingFile name: 'stacktrace', maxFileSize: 1024, file: logFile.absolutePath + '/stacktrace.log'
	}

	root {
		"$loglevel"()
	}

	"$loglevel" 'org.codehaus.groovy.grails.web.servlet',        // controllers
			'org.codehaus.groovy.grails.web.pages',          // GSP
			'org.codehaus.groovy.grails.web.sitemesh',       // layouts
			'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
			'org.codehaus.groovy.grails.web.mapping',        // URL mapping
			'org.codehaus.groovy.grails.commons',            // core / classloading
			'org.codehaus.groovy.grails.plugins',            // plugins
			'org.codehaus.groovy.grails.orm.hibernate',      // hibernate integration
			'org.springframework',
			'org.hibernate',
			'net.sf.ehcache.hibernate'
}

// Spring security configuration
grails {
	plugin {
		springsecurity {
			// active = false

			userLookup {
				usernamePropertyName = 'login'
				userDomainClassName = 'org.iish.acquisition.domain.User'
				authorityJoinClassName = 'org.iish.acquisition.domain.UserAuthority'
			}

			authority {
				className = 'org.iish.acquisition.domain.Authority'
			}

			controllerAnnotations {
				staticRules = [
						'/'              : ['permitAll'],
						'/index'         : ['permitAll'],
						'/index.gsp'     : ['permitAll'],
						'/assets/**'     : ['permitAll'],
						'/**/js/**'      : ['permitAll'],
						'/**/css/**'     : ['permitAll'],
						'/**/images/**'  : ['permitAll'],
						'/**/favicon.ico': ['permitAll'],
						'/**'            : [Authority.ROLE_USER]
				]
			}

			logout {
				postOnly = false
			}

			ldap {
				search {
					searchSubtree = true
					base = 'dc=iisg,dc=net'
					filter = 'sAMAccountName={0}'
				}

				auth {
					hideUserNotFoundExceptions = false
				}

				authorities {
					retrieveGroupRoles = false
					retrieveDatabaseRoles = true
					ignorePartialResultException = true
					defaultRole = SpringSecurityUtils.NO_ROLE
				}

				mapper {
					userDetailsClass = 'AcquisitionUser'
				}

				useRememberMe = false
			}

			providerNames = ['ldapAuthProvider', 'anonymousAuthenticationProvider']
			roleHierarchy = """
			   $Authority.ROLE_SUPER_ADMIN > $Authority.ROLE_ADMIN
			   $Authority.ROLE_ADMIN > $Authority.ROLE_USER
			"""
		}
	}
}