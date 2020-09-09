package io.github.kn65op.domag.ui.common


fun constructItemFullName(category: String, item: String?): String {
    if (item.isNullOrEmpty()) {
        return category
    }
    return "$item - $category"
}
