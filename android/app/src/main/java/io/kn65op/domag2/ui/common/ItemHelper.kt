package io.kn65op.domag2.ui.common


fun constructItemFullName(category: String, item: String?): String {
    if (item == null || item.isEmpty()) {
        return category
    }
    return "$item - $category"
}
