/*
 * (C) Copyright 2022-2024 Lukas Morawietz (https://github.com/F43nd1r)
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
package com.faendir.jooq

import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.DirectoryResourceAccessor
import org.jooq.DSLContext
import org.jooq.exception.DataAccessException
import org.jooq.impl.DSL
import org.jooq.meta.postgres.PostgresDatabase
import org.jooq.tools.jdbc.JDBCUtils
import org.slf4j.LoggerFactory
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import java.io.File
import java.sql.Connection
import java.util.*

@Suppress("unused")
class PostgresLiquibaseDatabase : PostgresDatabase() {
    private lateinit var postgresContainer: PostgreSQLContainer<*>
    private var conn: Connection? = null
    private var ctx: DSLContext? = null
    override fun create0(): DSLContext {
        if (conn == null) {
            val scripts = Objects.requireNonNull(properties.getProperty("scripts"))
            val postgresqlVersion = properties.getProperty("postgresqlVersion", "15")
            val dbName = properties.getProperty("databaseName", "test")
            println(dbName)
            try {
                postgresContainer = PostgreSQLContainer(DockerImageName.parse("postgres:$postgresqlVersion"))
                    .withDatabaseName(dbName)
                postgresContainer.start()
                conn = postgresContainer.createConnection("")
                ctx = DSL.using(conn)
                val scriptFile = File(scripts)
                val database = DatabaseFactory
                    .getInstance()
                    .findCorrectDatabaseImplementation(JdbcConnection(conn))
                val liquibase = Liquibase(
                    scriptFile.name,
                    DirectoryResourceAccessor(scriptFile.parentFile),
                    database
                )
                liquibase.update(Contexts(), LabelExpression())
            } catch (e: Exception) {
                log.error("Error while preparing schema for code generation", e)
                throw DataAccessException("Error while preparing schema for code generation", e)
            }
        }
        return ctx!!
    }

    override fun close() {
        JDBCUtils.safeClose(conn)
        conn = null
        if (::postgresContainer.isInitialized) {
            postgresContainer.close()
        }
        ctx = null
        super.close()
    }

    companion object {
        private val log = LoggerFactory.getLogger(PostgresLiquibaseDatabase::class.java)
    }
}