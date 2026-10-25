package online.mohmedbakr.newsfeed.data.datasources.remote.rss.sources

import online.mohmedbakr.newsfeed.data.datasources.remote.rss.ParseRssFeed
import online.mohmedbakr.newsfeed.data.datasources.remote.rss.RssFeedTags

class ParseMasrawyFeed : ParseRssFeed(
    tags = RssFeedTags(image = "url")
)
