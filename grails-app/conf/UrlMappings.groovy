
class UrlMappings {
	static mappings = {
		"/$controller/$action?/$id?(.$format)?" {
			constraints {
				// apply constraints here
			}
		}

		"/" {
			controller = 'collection'
			action = 'list'
		}

		"/service/statusList/$code/$subCode" {
			controller = 'service'
			action = 'statusList'
		}

		"500"(view: '/error')
	}
}
