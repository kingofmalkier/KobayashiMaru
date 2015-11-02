class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/" {
            controller = "Ec3"
            action = "index"
        }
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
