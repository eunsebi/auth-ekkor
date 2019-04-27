package xyz.ekkor

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/" (controller: "main", action: 'index')
        "/flush"(controller: "main", action: 'flush')

        "/user/$action"(controller: "user")
        "/user/info/$id"(controller: "user", action: "index")
        "/user/info/$id/$category?"(controller: "user", action: "index")
        "/user/privacy"(view: '/user/privacy')
        "/user/agreement"(view: '/user/agreement')

        "/articles/$code/$action?(.$format)?"(controller: "article")
        "/articles/tagged/$tag(.$format)?"(controller: "article", action: "tagged")

        "/article/$id(.$format)?"(controller: "article", action: "show")
        "/article/$action/$id(.$format)?"(controller: "article")
        "/article/$id/$action/$contentId(.$format)?"(controller: "article")
        "/seq/$id"(controller: "article", action: "seq")

        "/banner/stats/$id(.$format)?"(controller: "banner", action: "stats")
        "/file/uploadFile"(controller: "file", action: "uploadFile")
        "/file/uploadImg"(controller: "file", action: "uploadImg")
        "/file/deleteFile"(controller: "file", action: "deleteFile")

        "/find/user?/$action"(controller: "findUser")

        /* Admin */

        "/_admin/banner/$action?/$id?(.$format)?"(controller: "banner")
        //"/_admin/spamWord/$action?/$id?(.$format)?"(controller: "spamWord")
        "/_admin/user/$action?/$id?(.$format)?"(controller: "adminUser")
        //"/_admin/company/$action?/$id?(.$format)?"(controller: "adminCompany")
        //"/_admin/job/group/$action?/$id?(.$format)?"(controller: "jobPositionGroup")
        //"/_admin/job/duty/$action?/$id?(.$format)?"(controller: "jobPositionDuty")
        //"/_admin/dm/export"(controller: "dm", action: 'export')
        //"/_admin/dm/reject"(controller: "dm", action: 'reject')
        //"/_admin/dm/updateReject"(controller: "dm", action: 'updateReject')
        "/_admin"(controller: "statistic")

        "/"(view:"/main/index")
        "/gr" (view: "index")
        "405"(view:'/error')
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
