package io.github.kn65op.domag.ui.common


fun constructItemFullName(category: String, item: String?): String {
    if (item == null || item.isEmpty()) {
        return category
    }
    return "$item - $category"
}
