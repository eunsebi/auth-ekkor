package xyz.ekkor

import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.validation.ValidationException
import name.fraser.neil.plaintext.diff_match_patch
import org.hibernate.type.StandardBasicTypes
import org.springframework.http.HttpStatus

//@Secured("ROLE_ADMIN")
class ArticleController {

    ArticleService articleService
    SpringSecurityService springSecurityService
    UserService userService

    static responseFormats = ['html', 'json']

    static allowedMethods = [save   : "POST", update: ["PUT", "POST"], delete: ["DELETE", "POST"], scrap: "POST",
                             addNote: "POST", assent: ["PUT", "POST"], dissent: ["PUT", "POST"]]

    def beforeInterceptor = {
        response.characterEncoding = 'UTF-8' //workaround for https://jira.grails.org/browse/GRAILS-11830
    }

    //TODO 2019. 01. 28 카테고리 index
    def index(String code, Integer max) {

        params.max = Math.min(max ?: 20, 100)
        params.sort = params.sort ?: 'id'
        params.order = params.order ?: 'desc'
        params.query = params.query?.trim()

        def category = Category.get(code)

        if(category == null) {
            notFound()
            return
        }

        if (!SpringSecurityUtils.ifAllGranted(category.categoryLevel)) {
            notAcceptable()
            return
        }

        def notices = articleService.getNotices(category)

        def categories = category.children ?: [category]

        if(category.code == 'community')
            categories = categories.findAll { it.code != 'promote' }

        def articlesQuery = Article.where {
            category in categories
            if (SpringSecurityUtils.ifNotGranted("ROLE_ADMIN"))
                enabled == true
            if (params.query && params.query != '')
                title =~ "%${params.query}%" || content.text =~ "%${params.query}%"

            /*if(recruitFilter) {
                if(recruits)
                    id in recruits*.article*.id
                else
                    id in [Long.MAX_VALUE]
            }*/
        }

        def articles = articlesQuery.list(params)

        respond articles, model:[articlesCount: articlesQuery.count(), category: category, notices: notices]

    } // index

    def seq(Long id) {
        redirect uri: "/article/${id}"
    }

    //TODO 2019. 01. 28 게시판 새글페이지
    def create(String code) {

        def category = Category.get(code)

        //recaptchaService.cleanUp session

        User user = springSecurityService.loadCurrentUser()

        if (category == null) {
            notFound()
            return
        }

        if (user.accountLocked || user.accountExpired) {
            forbidden()
            return
        }

        /*println "user Role: " + user.getAuthorities()
        println "Category Role : " + category.cate_role*/

        /*String[] role = user.getAuthorities()
        int user_size = user.getAuthorities().size()
        String category_role = Integer.toString(category.cate_role)

        for (int num ; num < user_size ; num++) {
            role[num] = role[num].substring(role[num].length() -1)
        }

        boolean result = Arrays.asList(role).contains(category_role)

        //println " 권한 : " + result
        */

        // 권한 확인
        if (!SpringSecurityUtils.ifAllGranted(category.categoryLevel)) {
            notAcceptable()
            return
        }

        params.category = category

        def writableCategories
        def categories = Category.findAllByEnabled(true)
        def goExternalLink = false

        if(SpringSecurityUtils.ifAllGranted("ROLE_ADMIN")) {
            writableCategories = Category.findAllByWritableAndEnabled(true, true)
        } else {
            goExternalLink = category.writeByExternalLink
            writableCategories = Category.findAllByParentAndWritableAndEnabledAndAdminOnly(category?.parent ?: category, true, true, false) ?: [category]
            params.anonymity = category?.anonymity ?: false
        }

        //println "categories : " + categories
        //println "goExternalLink : " + goExternalLink
        //println "writableCategories : " + writableCategories
        //println "category : " + category

        def notices = params.list('notices') ?: []

        if(goExternalLink) {
            redirect(url: category.externalLink)
        } else {
            respond new Article(params), model: [writableCategories: writableCategories, category: category, categories: categories, notices: notices]
        }

    }

    //TODO 2019. 01. 28 새글 저장
    def save(String code) {

        println "article save"

        Article article = new Article(params)

        Category category = Category.get(params.categoryCode)

        User user = springSecurityService.loadCurrentUser()

        if(category?.code == 'recruit') {
            redirect uri: '/recruits/create'
            return
        }

        if(user.accountLocked || user.accountExpired) {
            forbidden()
            return
        }

        try {

            def realIp = userService.getRealIp(request)
            /*def reCaptchaVerified = recaptchaService.verifyAnswer(session, realIp, params)

            if(!reCaptchaVerified) {
                throw new Exception("invalid captcha")
            }

            recaptchaService.cleanUp session*/

            withForm {
                println "2222222222222"
                Avatar author = Avatar.load(springSecurityService.principal.avatarId)

                if(SpringSecurityUtils.ifAllGranted("ROLE_ADMIN")) {
                    article.choice = params.choice?:false
                    article.enabled = !params.disabled
                    article.ignoreBest = params.ignore ?: false
                }

                article.createIp = userService.getRealIp(request)

                articleService.save(article, author, category)
                println "3333333333333333333"

                articleService.saveNotices(article, user, params.list('notices'))

                withFormat {
                    html {
                        flash.message = message(code: 'default.created.message', args: [message(code: 'article.label', default: 'Article'), article.id])
                        redirect article
                    }
                    json { respond article, [status: CREATED] }
                }
            }.invalidToken {
                redirect uri: "/articles/${code}", method:"GET"
            }

        } catch (Exception e) {

            category = Category.get(code)
            def categories = category?.children ?: category?.parent?.children ?: [category]
            def notices = params.list('notices') ?: []
            article.category = category

            respond article.errors, view: 'create', model: [categories: categories, category: category, notices: notices]
        }
    }

    //TODO 2019. 01. 28 글 보기
    def show(Long id) {
        User user = springSecurityService.loadCurrentUser()

        def contentVotes = [], scrapped

        Article article = Article.get(id)

        if (article == null || (!article.enabled && SpringSecurityUtils.ifNotGranted("ROLE_ADMIN"))) {
            notFound()
            return
        }

        if (article.isRecruit) {
            redirect uri: "/recruit/$article.id"
        }

        article.updateViewCount(1)

        if (springSecurityService.loggedIn) {
            Avatar avatar = Avatar.load(springSecurityService.principal.avatarId)
            contentVotes = ContentVote.findAllByArticleAndVoter(article, avatar)
            scrapped = Scrap.findByArticleAndAvatar(article, avatar)
        }

        def category = Category.get(article.categoryId)

        // 권한 확인
        if (!SpringSecurityUtils.ifAllGranted(category.categoryLevel)) {
            notAcceptable()
            return
        }

        def notes = Content.findAllByArticleAndTypeAndEnabled(article, ContentType.NOTE, true)

        def contentBanners = Banner.where {
            type == BannerType.CONTENT && visible == true
        }.list()

        def contentBanner = contentBanners ? randomService.draw(contentBanners) : null

        def changeLogs = ChangeLog.createCriteria().list {
            eq('article', article)
            projections {
                sqlGroupProjection 'article_id as articleId, max(date_created) as dateCreated, content_id as contentId', 'content_id',
                        ['articleId', 'dateCreated', 'contentId'],
                        [StandardBasicTypes.LONG, StandardBasicTypes.TIMESTAMP, StandardBasicTypes.LONG]
            }
        }

        respond article, model: [contentVotes: contentVotes, notes: notes, scrapped: scrapped,
                                 contentBanner: contentBanner,
                                 //changeLogs: changeLogs
        ]
    }

    //TODO 2019. 02. 03 tagged
    def tagged(String tag, Integer max) {
        params.max = Math.min(max ?: 20, 100)
        params.sort = params.sort ?: 'id'
        params.order = params.order ?: 'desc'
        params.query = params.query?.trim()

        if(tag == null) {
            notFound()
            return
        }

        def articlesQuery = Article.where {
            tagString =~ "%${tag}%"
            if(params.query && params.query != '')
                title =~ "%${params.query}%" || content.text =~ "%${params.query}%"

        }

        respond articlesQuery.list(params), model:[articlesCount: articlesQuery.count()]
    }

    //TODO 2019. 02. 03 게시물 수정
    def edit(Long id) {
        Article article = Article.get(id)

        if(article == null) {
            notFound()
            return
        }

        if(SpringSecurityUtils.ifNotGranted("ROLE_ADMIN")) {
            if (article.authorId != springSecurityService.principal.avatarId) {
                notAcceptable()
                return
            }
        }

        if(article.category.code == 'recruit') {
            redirect uri: "/recruit/edit/$article.id"
            return
        }

        def writableCategories
        def categories = Category.findAllByEnabled(true)

        if(SpringSecurityUtils.ifAllGranted("ROLE_ADMIN")) {
            writableCategories = Category.findAllByWritableAndEnabled(true, true)
        } else {
            writableCategories = article.category.children ?: article.category.parent?.children ?: [article.category]
        }

        if(params.categoryCode) {
            article.category = Category.get(params.categoryCode)
        }

        def notices = ArticleNotice.findAllByArticle(article)

        respond article, model: [writableCategories: writableCategories, categories: categories, notices: notices]
    }

    //TODO 2019. 02. 03 게시물 수정 업로드
    def update(Article article) {
        User user = springSecurityService.loadCurrentUser()

        if(SpringSecurityUtils.ifNotGranted("ROLE_ADMIN")) {
            if (article.authorId != springSecurityService.principal.avatarId) {
                notAcceptable()
                return
            }
        }

        if(article.category.code == 'recruit') {
            redirect uri: '/recruits/create'
            return
        }

        if(user.accountLocked || user.accountExpired) {
            forbidden()
            return
        }

        try {

            withForm {

                Avatar editor = Avatar.get(springSecurityService.principal.avatarId)

                Category category = Category.get(params.categoryCode)

                if(SpringSecurityUtils.ifAllGranted("ROLE_ADMIN")) {
                    article.choice = params.choice?:false
                    article.enabled = !params.disabled
                    article.ignoreBest = params.ignore ?: false
                }

                articleService.update(article, editor, category)

                articleService.removeNotices(article)

                articleService.saveNotices(article, user, params.list('notices'))

                withFormat {
                    html {
                        flash.message = message(code: 'default.updated.message', args: [message(code: 'Article.label', default: 'Article'), article.id])
                        redirect article
                    }
                    json { respond article, [status: OK] }
                }

            }.invalidToken {
                redirect article
            }

        } catch (ValidationException e) {
            respond article.errors, view: 'edit'
        }
    }

    //TODO 2019. 02. 03 게시물 삭제
    def delete(Long id) {
        Article article = Article.get(id)

        User user = springSecurityService.loadCurrentUser()

        def categoryCode = article.category.code

        if (article == null) {
            notFound()
            return
        }

        if(user.accountLocked || user.accountExpired) {
            forbidden()
            return
        }

        if(SpringSecurityUtils.ifNotGranted("ROLE_ADMIN")) {
            if (article.authorId != springSecurityService.principal.avatarId) {
                notAcceptable()
                return
            }
        }

        articleService.delete(article)

        withFormat {
            html {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Article.label', default: 'Article'), article.id])
                flash.status = "success"
                redirect uri: "/articles/${categoryCode}", method:"GET"
            }
            json { render status: NO_CONTENT }
        }
    }

    //TODO 2019. 02. 03 스크랩
    def scrap(Long id) {
        Article article = Article.get(id)

        if (article == null) {
            notFound()
            return
        }

        try {

            Avatar avatar = Avatar.get(springSecurityService.principal.avatarId)

            if(Scrap.countByArticleAndAvatar(article, avatar) < 1) {
                articleService.saveScrap(article, avatar)
            } else {
                articleService.deleteScrap(article, avatar)
            }

            withFormat {
                html { redirect article }
                json {
                    article.refresh()
                    def result = [scrapCount: article.scrapCount]
                    respond result
                }
            }

        } catch (ValidationException e) {
            flash.error = e.message
            redirect article
        }
    }

    def addNote(Long id) {

        Article article = Article.get(id)

        User user = springSecurityService.loadCurrentUser()

        if(user.accountLocked || user.accountExpired) {
            forbidden()
            return
        }

        try {

            Avatar avatar = Avatar.get(springSecurityService.principal.avatarId)

            Content content = new Content()
            bindData(content, params, 'note')

            content.createIp = userService.getRealIp(request)

            articleService.addNote(article, content, avatar)

            withFormat {
                html {
                    flash.message = message(code: 'default.created.message', args: [message(code: 'Note.label', default: 'Note'), article.id])
                    redirect article
                }
                json {
                    respond article, [status: OK]
                }
            }

        } catch (ValidationException e) {
            flash.error = e.message
            redirect article
        }

    }

    def assent(Long id, Long contentId) {

        Article article = Article.get(id)

        Avatar avatar = Avatar.get(springSecurityService.principal.avatarId)
        Content content = Content.get(contentId)

        articleService.addVote(article, content, avatar, 1)

        withFormat {
            html { redirect article }
            json {
                content.refresh()
                def result = [voteCount: content.voteCount]
                respond result
            }
        }
    }

    def dissent(Long id, Long contentId) {

        Article article = Article.get(id)

        Avatar avatar = Avatar.get(springSecurityService.principal.avatarId)
        Content content = Content.get(contentId)

        articleService.addVote(article, content, avatar, -1)

        withFormat {
            html { redirect article }
            json {
                content.refresh()
                def result = [voteCount: content.voteCount]
                respond result
            }
        }
    }

    def unvote(Long id, Long contentId) {

        Article article = Article.get(id)

        Content content = Content.get(contentId)
        Avatar avatar = Avatar.get(springSecurityService.principal.avatarId)

        articleService.cancelVote(article, content, avatar)

        withFormat {
            html { redirect article }
            json {
                content.refresh()
                def result = [voteCount: content.voteCount]
                respond result
            }
        }

    }

    def selectNote(Long id, Long contentId) {

        Article article = Article.get(id)

        if(article.authorId != springSecurityService.principal.avatarId) {
            notAcceptable()
            return
        }

        Content content = Content.get(contentId)

        if(article.selectedNote == null) {

            content.selected = true
            content.save()

            article.selectedNote = content
            article.save(flush: true)
        }

        withFormat {
            html { redirect article }
            json { respond article, [status: OK] }
        }
    }

    def deselectNote(Long id) {

        Article article = Article.get(id)

        if(article.authorId != springSecurityService.principal.avatarId) {
            notAcceptable()
            return
        }

        if(article.selectedNote != null) {
            article.selectedNote.selected = false
            article.selectedNote.save()

            article.selectedNote = null
            article.save(flush: true)
        }

        withFormat {
            html { redirect article }
            json { respond article, [status: OK] }
        }
    }

    def changes(Long id) {

        Content content = Content.get(id)

        Article article = content.article

        def changeLogs = ChangeLog.where{
            eq('article', article)
            eq('content', content)
        }.list(sort: 'id', order: 'desc')


        def lastTexts = [:]

        changeLogs.each { ChangeLog log ->

            if(!lastTexts[log.type]) {
                if(log.type == ChangeLogType.TITLE) {
                    lastTexts[log.type] = article.title
                } else if(log.type == ChangeLogType.CONTENT) {
                    lastTexts[log.type] = content.text
                } else if(log.type == ChangeLogType.TAGS) {
                    lastTexts[log.type] = article.tagString
                }
            }

            def dmp = new diff_match_patch()

            LinkedList<diff_match_patch.Patch> patches = dmp.patch_fromText(log.patch)

            log.text = dmp.patch_apply(patches, lastTexts[log.type] as String)[0]

            lastTexts[log.type] = log.text

        }

        respond article, model: [content: content, changeLogs: changeLogs]
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'job.label', default: 'Job'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: HttpStatus.NOT_FOUND }
        }
    }

    protected void notAcceptable() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.Acceptable.message', args: [message(code: 'job.label', default: 'Job'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: HttpStatus.NOT_ACCEPTABLE }
        }
    }

    protected void forbidden() {

        withFormat {
            html {
                flash.message = message(code: 'default.forbidden.message', args: [message(code: 'article.label', default: 'Article'), params.id])
                redirect uri: '/'
            }
            json { render status: FORBIDDEN }
        }
    }

}
