/*
 * (C) Copyright 2019-2021 Lukas Morawietz (https://github.com/F43nd1r)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.faendir.acra.i18n

import com.vaadin.flow.component.UI
import com.vaadin.flow.i18n.I18NProvider
import com.vaadin.flow.server.VaadinService
import java.util.*

open class TranslatableText(val id: String, vararg val params: Any) {

    fun translate(): String {
        return i18NProvider.getTranslation(
            id,
            UI.getCurrent()?.locale ?: VaadinService.getCurrent().instantiator.i18NProvider.providedLocales?.firstOrNull() ?: Locale.getDefault(),
            *params
        )
    }

    private val i18NProvider: I18NProvider
        get() = VaadinService.getCurrent().instantiator.i18NProvider
}