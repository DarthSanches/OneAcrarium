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
package com.faendir.acra.common

import com.faendir.acra.jooq.generated.Acrarium
import com.faendir.acra.persistence.jooq.JooqConfigurationCustomizer
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestExecutionListener
import javax.sql.DataSource

@TestConfiguration
@Import(JooqConfigurationCustomizer::class)
class DatabaseTestConfiguration {

    @Bean
    fun dataSource(): DataSource = DataSourceBuilder.create()
        .driverClassName("org.testcontainers.jdbc.ContainerDatabaseDriver")
        .url("jdbc:tc:mysql:8.0:////test?serverTimezone=UTC&TC_MY_CNF=mysql.conf.d")
        .build()
}

class DatabaseCleanupTestExecutionListener : TestExecutionListener {
    override fun afterTestMethod(testContext: org.springframework.test.context.TestContext) {
        try {
            val jooq = testContext.applicationContext.getBean(org.jooq.DSLContext::class.java)
            Acrarium.ACRARIUM.tables.forEach {
                jooq.deleteFrom(it).execute()
            }
        } catch (e: NoSuchBeanDefinitionException) {
            // not a database test
        }
    }
}