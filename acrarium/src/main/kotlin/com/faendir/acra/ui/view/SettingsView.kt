/*
 * (C) Copyright 2018-2022 Lukas Morawietz (https://github.com/F43nd1r)
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
package com.faendir.acra.ui.view

import com.faendir.acra.i18n.Messages
import com.faendir.acra.i18n.TranslatableText
import com.faendir.acra.navigation.View
import com.faendir.acra.settings.LocalSettings
import com.faendir.acra.ui.component.HasAcrariumTitle
import com.faendir.acra.ui.ext.*
import com.faendir.acra.ui.view.main.MainView
import com.vaadin.flow.component.Composite
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode
import com.vaadin.flow.component.orderedlayout.FlexLayout
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.VaadinService
import com.vaadin.flow.server.VaadinSession
import com.vaadin.flow.theme.lumo.Lumo

@View
@Route(value = "settings", layout = MainView::class)
class SettingsView(localSettings: LocalSettings) : Composite<FlexLayout>(), HasAcrariumTitle {

    init {
        content {
            setSizeFull()
            justifyContentMode = JustifyContentMode.CENTER
            alignItems = FlexComponent.Alignment.CENTER
            formLayout {
                setResponsiveSteps(ResponsiveStep("0px", 1))
                setAlignSelf(Align.AUTO)
                translatableCheckbox(Messages.DARK_THEME) {
                    value = localSettings.darkTheme
                    addValueChangeListener {
                        localSettings.darkTheme = it.value
                        VaadinSession.getCurrent().uIs.forEach { ui -> ui.element.setAttribute("theme", if (it.value) Lumo.DARK else Lumo.LIGHT) }
                    }
                }
                translatableSelect(VaadinService.getCurrent().instantiator.i18NProvider.providedLocales, { it.getDisplayName(localSettings.locale) }, Messages.LOCALE) {
                    value = localSettings.locale
                    addValueChangeListener {
                        localSettings.locale = it.value
                        VaadinSession.getCurrent().locale = it.value
                    }
                }
            }
        }
    }

    override val title = TranslatableText(Messages.SETTINGS)
}