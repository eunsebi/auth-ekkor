package xyz.ekkor

import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic

@CompileStatic
@Transactional
class ArticleSrvice {

    ArticleDataService articleDataService

    @Transactional
    Article save(Article articleInstance, Avatar author, Category category) {
        Article article = articleDataService.save(articleInstance, author, category)

    }


}
