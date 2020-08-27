package tech.muso.rekoil.ktx

import tech.muso.rekoil.core.RekoilContext
import kotlin.reflect.KProperty

/**
 * Create extension function for setValue for automatic kotlin delegate properties.
 *
 * Allows for `var backingProperty: T by
 */
public operator fun <T> RekoilContext.ValueNode<T>.setValue(line: Any?, property: KProperty<*>, t: T) {
    this.value = t
}

public operator fun <T> RekoilContext.ValueNode<T>.getValue(line: Any?, property: KProperty<*>): T {
    return this.value
}
