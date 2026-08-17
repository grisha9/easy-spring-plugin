/*
 * Copyright (c) 2026 Explyt Ltd
 * SPDX-License-Identifier: Apache-2.0
 */

package com.explyt.spring.core.search

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Caches whether the project is a Spring Boot one, so that Search Everywhere can decide on the EDT whether to show
 * the Beans tab.
 *
 * Search Everywhere queries contributor availability on the EDT, while the underlying detection resolves a library
 * class and therefore needs indices and a read action (see #233). The value is thus computed in the background and
 * only ever read from a volatile field on the EDT; it is recomputed when project roots change.
 */
@Service(Service.Level.PROJECT)
class BeanSearchAvailabilityService(private val project: Project) {

    @Volatile
    private var cached: Availability? = null
    private val updateScheduled = AtomicBoolean(false)


    private data class Availability(val isSpringBootProject: Boolean, val modificationCount: Long)

    companion object {
        fun getInstance(project: Project): BeanSearchAvailabilityService = project.service()
    }
}
