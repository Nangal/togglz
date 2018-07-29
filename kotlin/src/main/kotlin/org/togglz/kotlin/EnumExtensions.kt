package org.togglz.kotlin

import org.togglz.core.Feature
import org.togglz.core.context.FeatureContext

inline fun Enum<*>.toFeature(): Feature? = FeatureContext.getFeatureManager().featureFor(this)
inline val Enum<*>.isActive: Boolean get() = FeatureContext.getFeatureManager().isActive(this.toFeature())
