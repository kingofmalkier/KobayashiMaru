class UrlMappings {

    static mappings = {
        "/instances"(resources:'Ec3')
        "/refresh"(controller: "Ec3", action: "refresh")
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
