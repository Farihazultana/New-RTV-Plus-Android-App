package org.imaginativeworld.whynotimagecarousel.model

import androidx.annotation.DrawableRes

data class CarouselItem constructor(
    val contentId: String? = null,
    val imageUrl: String? = null,
    @DrawableRes val imageDrawable: Int? = null,
    val caption: String? = null,
    val headers: Map<String, String>?
) {
    constructor(contentId: String? = null, imageUrl: String? = null) : this(
        contentId,
        imageUrl,
        null,
        null,
        null
    )

    constructor(
        contentId: String? = null,
        imageUrl: String? = null,
        headers: Map<String, String>? = null
    ) : this(
        contentId,
        imageUrl,
        null,
        null,
        headers
    )

    constructor(contentId: String? = null, @DrawableRes imageDrawable: Int? = null) : this(
        contentId,
        null,
        imageDrawable,
        null,
        null
    )

    constructor(contentId: String? = null, imageUrl: String? = null, caption: String? = null) : this(
        contentId,
        imageUrl,
        null,
        caption,
        null
    )

    constructor(
        contentId: String? = null,
        imageUrl: String? = null,
        caption: String? = null,
        headers: Map<String, String>? = null
    ) : this(
        contentId,
        imageUrl,
        null,
        caption,
        headers
    )

    constructor(
        contentId: String? = null,
        @DrawableRes imageDrawable: Int? = null,
        caption: String? = null
    ) : this(
        contentId,
        null,
        imageDrawable,
        caption,
        null
    )
}

