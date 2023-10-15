/*
 * (C) Copyright 2017-2023 Lukas Morawietz (https://github.com/F43nd1r)
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
package com.faendir.acra.ui.view.installation

import com.faendir.acra.i18n.Messages
import com.faendir.acra.navigation.LogicalParent
import com.faendir.acra.navigation.PARAM_APP
import com.faendir.acra.navigation.PARAM_INSTALLATION
import com.faendir.acra.navigation.RouteParams
import com.faendir.acra.persistence.app.AppId
import com.faendir.acra.persistence.user.Permission
import com.faendir.acra.security.RequiresPermission
import com.faendir.acra.ui.component.tabs.TabView
import com.faendir.acra.ui.view.app.tabs.InstallationAppTab
import com.faendir.acra.ui.view.installation.tabs.ReportInstallationTab
import com.faendir.acra.ui.view.installation.tabs.StatisticsInstallationTab
import com.faendir.acra.ui.view.main.MainView
import com.vaadin.flow.router.ParentLayout
import com.vaadin.flow.router.RoutePrefix
import com.vaadin.flow.spring.annotation.SpringComponent
import com.vaadin.flow.spring.annotation.UIScope

@UIScope
@SpringComponent
@RoutePrefix("app/:$PARAM_APP/installation/:$PARAM_INSTALLATION")
@ParentLayout(MainView::class)
@LogicalParent(InstallationAppTab::class)
@RequiresPermission(Permission.Level.VIEW)
class InstallationView(
    routeParams: RouteParams,
) : TabView(
    routeParams.installationId(),
    TabInfo(ReportInstallationTab::class, Messages.REPORTS),
    TabInfo(StatisticsInstallationTab::class, Messages.STATISTICS),
) {

    companion object {
        fun getNavigationParams(app: AppId, installationId: String) = mapOf(PARAM_APP to app.toString(), PARAM_INSTALLATION to installationId)
    }
}