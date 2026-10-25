package online.mohmedbakr.newsfeed.data.datasources.remote.rss.sources

import online.mohmedbakr.newsfeed.data.datasources.remote.rss.ParseRssFeed
import online.mohmedbakr.newsfeed.data.datasources.remote.rss.RssFeedTags

class ParseAljazeeraFeed : ParseRssFeed(
    tags = RssFeedTags(link = "guid", image = "noImage")
)
