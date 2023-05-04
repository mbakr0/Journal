package online.mohmedbakr.newsfeed.core

/*
 ArticleDTO for the Article Class
 */
class ArticleDTO {
    var imageLink = ""
    var link = ""
    var title = ""
    var description = ""
    var publicationDate = ""

    fun clone(article: ArticleDTO): ArticleDTO {
        val clone = ArticleDTO()
        clone.imageLink = article.imageLink
        clone.link = article.link
        clone.title = article.title
        clone.description = article.description
        clone.publicationDate = article.publicationDate
        article.imageLink = ""
        article.link = ""
        article.title = ""
        article.description = ""
        article.publicationDate = ""
        return clone
    }

}