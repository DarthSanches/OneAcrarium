/*
 * (C) Copyright 2018-2023 Lukas Morawietz (https://github.com/F43nd1r)
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
package com.faendir.acra.ui.view.bug.tabs.admincards

import com.faendir.acra.i18n.Messages
import com.faendir.acra.navigation.RouteParams
import com.faendir.acra.navigation.View
import com.faendir.acra.persistence.bug.BugRepository
import com.faendir.acra.persistence.user.Permission
import com.faendir.acra.security.RequiresPermission
import com.faendir.acra.ui.component.AdminCard
import com.faendir.acra.ui.component.Translatable.Companion.createSpan
import com.faendir.acra.ui.component.dialog.confirmButtons
import com.faendir.acra.ui.component.dialog.showFluentDialog
import com.faendir.acra.ui.ext.box
import com.faendir.acra.ui.ext.content
import com.faendir.acra.ui.ext.translatableText
import com.faendir.acra.ui.view.Overview
import com.vaadin.flow.component.UI

@View
@RequiresPermission(Permission.Level.ADMIN)
class DangerBugAdminCard(
    private val bugRepository: BugRepository,
    routeParams: RouteParams,
) : AdminCard() {
    private val appId = routeParams.appId()
    private val bugId = routeParams.bugId()

    init {
        content {
            setHeader(createSpan(Messages.DANGER_ZONE))
            setHeaderColor("var(--lumo-error-contrast-color)", "var(--lumo-error-color)")
            dividerEnabled = true
            box(Messages.DELETE_BUG, Messages.DELETE_BUG_DETAILS, Messages.DELETE) {
                showFluentDialog {
                    translatableText(Messages.DELETE_BUG_CONFIRM)
                    confirmButtons {
                        bugRepository.delete(appId, bugId)
                        UI.getCurrent().navigate(Overview::class.java)
                    }
                }
            }
        }
    }
}