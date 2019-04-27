package xyz.ekkor

import grails.util.Environment

class BootStrap {

    UserService userService

    def init = { servletContext ->

        switch (Environment.current) {
            case Environment.DEVELOPMENT:
                populateUsers()
                populateCategory()

                break

            case Environment.PRODUCTION :
                break
        }
    }

    //TODO 2019. 02. 03 Boot 계정생성
    def populateUsers() {
        if(User.count() == 0) {
            def adminRole = new Role(authority: 'ROLE_ADMIN').save()
            def operatorRole = new Role(authority: 'ROLE_OPERATOR').save()
            def customerRole = new Role(authority: 'ROLE_CUSTOMER').save()
            def userRole = new Role(authority: 'ROLE_USER').save(flush: true)

            def adminUser = new User(
                    username: 'admin',
                    password: 'admin',
                    person: new Person(fullName: '관리자', email: 'admin@ekkor.xyz'),
                    avatar: new Avatar(nickname: '관리자')
                    //).save()
            )
            //def operatorUser = new User(username: 'operator', password: 'Password123!').save()
            //def customerUser = new User(username: 'customer', password: 'Password123!').save()

            adminUser.enabled = true
            adminUser.createIp = '0.0.0.0'
            userService.saveUser adminUser
            UserRole.create(adminUser, adminRole)
            //UserRole.create adminUser, adminRole
            //UserRole.create operatorUser, operatorRole
            //UserRole.create customerUser, customerRole
            //userService.saveUser adminUser

            UserRole.withSession {
                it.flush()
                it.clear()
            }
        }

        //assert Role.count() == 4
        //assert User.count() == 1
        //assert UserRole.count() == 1
    }

    //TODO 2019. 02. 03 게시판 카테고리 생성
    def populateCategory() {
        // 1 Level Category
        def questionsCategory = Category.get('questions') ?: new Category(code: 'questions', labelCode: 'questions.label', defaultLabel: 'Q&A', iconCssNames: 'fa fa-database', sortOrder: 0, writable: true, useNote: true, useOpinion: true, useEvaluate: true, useTag: true, requireTag: true).save(flush: true)
        def techCategory = Category.get('tech') ?: new Category(code: 'tech', labelCode: 'tech.label', defaultLabel: 'Tech', iconCssNames: 'fa fa-code', sortOrder: 1, writable: false, useNote: true, useOpinion: false, useEvaluate: false, useTag: true).save(flush: true)
        def communityCategory = Category.get('community') ?: new Category(code: 'community', labelCode: 'community.label', defaultLabel: '커뮤니티', iconCssNames: 'fa fa-comments', sortOrder: 2, writable: false, useNote: true, useOpinion: false, useEvaluate: false, useTag: false).save(flush: true)
        def columnsCategory = Category.get('columns') ?: new Category(code: 'columns', labelCode: 'columns.label', defaultLabel: '칼럼', iconCssNames: 'fa fa-quote-left', sortOrder: 3, writable: true, useNote: true, useOpinion: false, useEvaluate: false, useTag: true).save(flush: true)
        //def jobsCategory = Category.get('jobs') ?: new Category(code: 'jobs', labelCode: 'jobs.label', defaultLabel: 'Jobs', iconCssNames: 'fa fa-group', sortOrder: 4, writable: false, useNote: true, useOpinion: false, useEvaluate: false, useTag: true).save(flush: true)

        // 2 Level Category

        // Tech
        def newsCategory = Category.get('news') ?: new Category(code: 'news', parent: techCategory, labelCode: 'news.label', defaultLabel: 'IT News & 정보', iconCssNames: 'fa fa-code', sortOrder: 0, useNote: true, useOpinion: false, useEvaluate: false, useTag: true).save(flush: true)
        def tipsCategory = Category.get('tips') ?: new Category(code: 'tips', parent: techCategory, labelCode: 'tips.label', defaultLabel: 'Tips & Tricks', iconCssNames: 'fa fa-code', sortOrder: 1, useNote: true, useOpinion: false, useEvaluate: false, useTag: true).save(flush: true)

        // Community
        def noticeCategory = Category.get('notice') ?: new Category(code: 'notice', parent: communityCategory, labelCode: 'notice.label', defaultLabel: '공지사항', iconCssNames: 'fa fa-comments', sortOrder: 0, useNote: true, useOpinion: false, useEvaluate: false, useTag: true, adminOnly: true).save(flush: true)
        def lifeCategory = Category.get('life') ?: new Category(code: 'life', parent: communityCategory, labelCode: 'life.label', defaultLabel: '사는얘기', iconCssNames: 'fa fa-comments', sortOrder: 1, useNote: true, useOpinion: false, useEvaluate: false, useTag: false).save(flush: true)
        def forumCategory = Category.get('forum') ?: new Category(code: '포럼', parent: communityCategory, labelCode: 'forum.label', defaultLabel: 'Forum', iconCssNames: 'fa fa-code', sortOrder: 1, useNote: true, useOpinion: false, useEvaluate: false, useTag: true).save(flush: true)
        def gatheringCategory = Category.get('gathering') ?: new Category(code: 'gathering', parent: communityCategory, labelCode: 'gathering.label', defaultLabel: '정기모임/스터디', iconCssNames: 'fa fa-comments', sortOrder: 2, useNote: true, useOpinion: false, useEvaluate: false, useTag: true).save(flush: true)
        def promoteCategory = Category.get('promote') ?: new Category(code: 'promote', parent: communityCategory, labelCode: 'gathering.label', defaultLabel: '학원', iconCssNames: 'fa fa-comments', sortOrder: 3, useNote: true, useOpinion: false, useEvaluate: false).save(flush: true)

    }
    def destroy = {
    }
}
