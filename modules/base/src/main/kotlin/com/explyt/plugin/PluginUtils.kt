/*
 * Copyright (c) 2025 Explyt Ltd
 * SPDX-License-Identifier: Apache-2.0
 */

package com.explyt.plugin

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.lang.Language
import com.intellij.openapi.extensions.PluginId

//todo remove
enum class PluginIds(val pluginId: String) {
    HTTP_CLIENT_JB("com.jetbrains.restClient"),
    SPRING_JB("com.intellij.spring"),
    SPRING_DATA_JB("com.intellij.spring.data"),
    SPRING_WEB_JB("com.intellij.spring.mvc"),
    SPRING_DEBUGGER_JB("com.intellij.spring.debugger"),
    CDI_JB("com.intellij.cdi"), //Jakarta EE - DI (QUARKUS SEXPLYT Conflict)
    SPRING_BOOT_JB("com.intellij.spring.boot"),
    SH_JB("com.jetbrains.sh"),
    ;

    fun isEnabled() = !isNotEnabled()

    fun isNotEnabled() = PluginManagerCore.isDisabled(PluginId.getId(pluginId))
}

enum class PluginSqlLanguage(val langId: String) {
    SQL("SQL"),
    JPAQL("JPAQL"),
    HQL("HQL"),
    SPRING_QL("SpringDataQL"),
    ;

    fun isEnabled() = Language.findLanguageByID(langId) != null
}