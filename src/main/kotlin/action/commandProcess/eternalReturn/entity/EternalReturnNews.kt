package action.commandProcess.eternalReturn.entity

import com.alibaba.fastjson2.annotation.JSONField

/**
 * @author LoMu
 * Date 2024.09.01 13:27
 */

data class EternalReturnNews(
    @JSONField(name = "per_page")
    val perPage: Int,

    @JSONField(name = "current_page")
    val currentPage: Int,

    @JSONField(name = "total_page")
    val totalPage: Int,

    @JSONField(name = "article_count")
    val articleCount: Int,

    @JSONField(name = "articles")
    val articles: List<EternalReturnArticle>,
)

data class EternalReturnArticle(
    @JSONField(name = "id")
    val id: Int,

    @JSONField(name = "board_id")
    val boardId: Int,

    @JSONField(name = "category_id")
    val categoryId: Int,

    @JSONField(name = "thumbnail_url")
    val thumbnailUrl: String?,

    @JSONField(name = "view_count")
    val viewCount: Int,

    @JSONField(name = "is_hidden")
    val isHidden: Boolean,

    @JSONField(name = "is_pinned")
    val isPinned: Boolean,

    @JSONField(name = "created_at")
    val createdAt: String,

    @JSONField(name = "updated_at")
    val updatedAt: String,

    @JSONField(name = "url")
    val url: String,

    @JSONField(name = "i18ns")
    val i18ns: EternalReturnI18ns,
)

data class EternalReturnI18ns(
    @JSONField(name = "zh_CN")
    val zhCN: EternalReturnI18nsZHCN,
)

data class EternalReturnI18nsZHCN(
    @JSONField(name = "locale")
    val locale: String,

    @JSONField(name = "title")
    val title: String,

    @JSONField(name = "summary")
    val summary: String,

    @JSONField(name = "created_at_for_humans")
    val createdAtForHumans: String,

    @JSONField(name = "is_hidden")
    val isHidden: Boolean,

    @JSONField(name = "content_type")
    val contentType: Int,

    @JSONField(name = "content_link")
    val contentLink: String,

    @JSONField(name = "content_link_target")
    val contentLinkTarget: String,
)