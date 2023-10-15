/*
 * (C) Copyright 2022-2023 Lukas Morawietz (https://github.com/F43nd1r)
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
package com.faendir.acra.navigation

import com.faendir.acra.annotation.AcrariumTest
import com.faendir.acra.ui.view.bug.tabs.ReportBugTab
import com.vaadin.flow.router.RouteConfiguration
import com.vaadin.flow.router.RouteParameters
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@AcrariumTest
class SpringRouteConfigurationInitializerTest {

    @Autowired
    lateinit var routeConfiguration: RouteConfiguration

    @Test
    fun `should resolve urls`() {
        expectThat(
            routeConfiguration.getUrl(
                ReportBugTab::class.java, RouteParameters(
                    mapOf(
                        PARAM_APP to "1",
                        PARAM_BUG to "2"
                    )
                )
            )
        ).isEqualTo("app/1/bug/2/report")
    }
}