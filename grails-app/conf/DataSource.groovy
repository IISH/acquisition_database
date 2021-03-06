dataSource {
	pooled = true
	jmxExport = true
	driverClassName = "com.mysql.jdbc.Driver"
	dbCreate = "validate"
	configClass = org.iish.acquisition.util.CustomHibernateConfiguration
}
hibernate {
	cache.use_second_level_cache = true
	cache.use_query_cache = true
	cache.region.factory_class = 'org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory'
}

// environment specific settings
environments {
	development {
		dataSource {
			dbCreate = "validate" // first time "create-drop", after that "validate" or "update"
			dialect = org.iish.acquisition.util.CustomMySQL5InnoDBDialect
			logSql = true
			properties {
				dbProperties {
					// tinyint is a boolean
					tinyInt1isBit = false
					// prevent exception '0000-00-00 00:00:00' can not be represented as java.sql.Timestamp
					zeroDateTimeBehavior = 'convertToNull'
				}
			}
		}
	}
	test {
		dataSource {
			driverClassName = "org.h2.Driver"
			dbCreate = "create-drop"
			dialect = org.hibernate.dialect.H2Dialect
			username = "sa"
			password = ""
			url = "jdbc:h2:mem:testDb;MVCC=TRUE;LOCK_TIMEOUT=10000;DB_CLOSE_ON_EXIT=FALSE"
		}
	}
	production {
		dataSource {
			dbCreate = "validate"
			dialect = org.iish.acquisition.util.CustomMySQL5InnoDBDialect
			properties {
				// Documentation for Tomcat JDBC Pool
				// http://tomcat.apache.org/tomcat-7.0-doc/jdbc-pool.html#Common_Attributes
				// https://tomcat.apache.org/tomcat-7.0-doc/api/org/apache/tomcat/jdbc/pool/PoolConfiguration.html
				jmxEnabled = true
				initialSize = 5
				maxActive = 50
				minIdle = 5
				maxIdle = 25
				maxWait = 10000
				maxAge = 10 * 60000
				timeBetweenEvictionRunsMillis = 5000
				minEvictableIdleTimeMillis = 60000
				validationQuery = "SELECT 1"
				validationQueryTimeout = 3
				validationInterval = 15000
				testOnBorrow = true
				testWhileIdle = true
				testOnReturn = false
				ignoreExceptionOnPreLoad = true
				// http://tomcat.apache.org/tomcat-7.0-doc/jdbc-pool.html#JDBC_interceptors
				jdbcInterceptors = "ConnectionState;StatementCache(max=200)"
				defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED // safe default
				// controls for leaked connections
				abandonWhenPercentageFull = 100 // settings are active only when pool is full
				removeAbandonedTimeout = 120000
				removeAbandoned = true
				// use JMX console to change this setting at runtime
				logAbandoned = false // causes stacktrace recording overhead, use only for debugging
				// JDBC driver properties
				dbProperties {
					// Mysql specific driver properties
					// http://dev.mysql.com/doc/connector-j/en/connector-j-reference-configuration-properties.html
					// let Tomcat JDBC Pool handle reconnecting
					autoReconnect = false
					// truncation behaviour
					jdbcCompliantTruncation = false
					// mysql 0-date conversion
					zeroDateTimeBehavior = 'convertToNull'
					// Tomcat JDBC Pool's StatementCache is used instead, so disable mysql driver's cache
					cachePrepStmts = false
					cacheCallableStmts = false
					// Tomcat JDBC Pool's StatementFinalizer keeps track
					dontTrackOpenResources = true
					// performance optimization: reduce number of SQLExceptions thrown in mysql driver code
					holdResultsOpenOverStatementClose = true
					// enable MySQL query cache - using server prep stmts will disable query caching
					useServerPrepStmts = false
					// metadata caching / TODO: UTF-8 problem
					// cacheServerConfiguration = true
					cacheResultSetMetadata = true
					metadataCacheSize = 100
					// timeouts for TCP/IP
					connectTimeout = 15000
					socketTimeout = 120000
					// timer tuning (disable)
					maintainTimeStats = false
					enableQueryTimeouts = false
					// misc tuning
					noDatetimeStringSync = true
					// tinyint is a boolean
					tinyInt1isBit = false
				}
			}
		}
	}
}
