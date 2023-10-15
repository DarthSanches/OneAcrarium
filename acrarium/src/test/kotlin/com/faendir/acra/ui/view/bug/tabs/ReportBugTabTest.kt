/*
 * (C) Copyright 2023 Lukas Morawietz (https://github.com/F43nd1r)
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
package com.faendir.acra.ui.view.bug.tabs

import com.faendir.acra.common.UiParams
import com.faendir.acra.common.UiTest
import com.faendir.acra.common.captionId
import com.faendir.acra.i18n.Messages
import com.faendir.acra.persistence.TestDataBuilder
import com.faendir.acra.persistence.app.AppId
import com.faendir.acra.persistence.bug.BugId
import com.faendir.acra.persistence.report.ReportRow
import com.faendir.acra.persistence.user.Permission
import com.faendir.acra.persistence.user.Role
import com.faendir.acra.ui.component.grid.LocalizedColumn
import com.faendir.acra.ui.view.bug.BugView
import com.github.mvysny.kaributesting.v10._expectNone
import com.github.mvysny.kaributesting.v10._expectOne
import com.github.mvysny.kaributesting.v10._get
import com.vaadin.flow.component.grid.Grid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class ReportBugTabTest(
    @Autowired private val testDataBuilder: TestDataBuilder,
) : UiTest() {
    private val appId: AppId = testDataBuilder.createApp()
    private val bugId: BugId = testDataBuilder.createBug(appId)
    private val reportId: String = testDataBuilder.createReport(appId, bugId)

    override fun setup() = UiParams(
        route = ReportBugTab::class,
        routeParameters = BugView.getNavigationParams(appId, bugId),
        requiredAuthorities = setOf(Role.USER, Permission(appId, Permission.Level.VIEW))
    )

    @Test
    fun `should show report list`() {
        val grid = _get<Grid<ReportRow>>()

        expectThat(grid._get(0).id).isEqualTo(reportId)
    }

    @Test
    fun `should show not show delete column with VIEW permission`() {
        val grid = _get<Grid<ReportRow>>()

        grid._expectNone<LocalizedColumn<*>> { captionId = Messages.DELETE }
    }

    @Test
    fun `should show delete column with EDIT permission`() {
        withAuth(Permission(appId, Permission.Level.EDIT)) {
            val grid = _get<Grid<ReportRow>>()

            grid._expectOne<LocalizedColumn<*>> { captionId = Messages.DELETE }
        }
    }
}