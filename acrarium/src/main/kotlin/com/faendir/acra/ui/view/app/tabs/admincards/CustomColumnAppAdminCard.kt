/*
 * (C) Copyright 2021-2023 Lukas Morawietz (https://github.com/F43nd1r)
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
package com.faendir.acra.ui.view.app.tabs.admincards

import com.faendir.acra.i18n.Messages
import com.faendir.acra.navigation.RouteParams
import com.faendir.acra.navigation.View
import com.faendir.acra.persistence.app.AppRepository
import com.faendir.acra.persistence.app.CustomColumn
import com.faendir.acra.persistence.user.Permission
import com.faendir.acra.security.SecurityUtils
import com.faendir.acra.ui.component.AdminCard
import com.faendir.acra.ui.component.Translatable
import com.faendir.acra.ui.component.Translatable.Companion.createSpan
import com.faendir.acra.ui.component.grid.column
import com.faendir.acra.ui.component.grid.renderer.ButtonRenderer
import com.faendir.acra.ui.ext.*
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.Binder
import java.util.*

@View
class CustomColumnAppAdminCard(
    appRepository: AppRepository,
    routeParams: RouteParams,
) : AdminCard() {

    private val appId = routeParams.appId()

    init {
        content {
            setHeader(createSpan(Messages.CUSTOM_COLUMNS))
            acrariumGrid(appRepository.getCustomColumns(appId)) {
                setMinHeight(280, SizeUnit.PIXEL)
                setHeight(100, SizeUnit.PERCENTAGE)
                val pathColumn = column({ it.path }) {
                    isSortable = true
                    captionId = Messages.PATH
                    setFlexGrow(1)
                }
                val nameColumn = column({ it.name }) {
                    isSortable = true
                    captionId = Messages.NAME
                    setFlexGrow(1)
                }
                if (SecurityUtils.hasPermission(appId, Permission.Level.ADMIN)) {
                    editor.binder = Binder(CustomColumn::class.java)
                    editor.isBuffered = true
                    val regex = Regex("[\\w_-]+(\\.[\\w_-]+)*")
                    val pathField = TextField().apply {
                        allowedCharPattern = "[\\w_.-]"
                        pattern = regex.pattern
                    }
                    val nameField = TextField().apply {
                        isRequired = true
                    }
                    var currentItem: CustomColumn? = null
                    editor.addOpenListener { currentItem = it.item }
                    editor.binder.forField(pathField).withValidator({ it.matches(regex) }, { getTranslation(Messages.ERROR_NOT_JSON_PATH) })
                        .bind({ it.path }, { _, value -> currentItem = currentItem?.copy(path = value) })
                    editor.binder.forField(nameField).bind({ it.name }, { _, value -> currentItem = currentItem?.copy(name = value) })
                    pathColumn.editorComponent = pathField
                    nameColumn.editorComponent = nameField
                    val editButtons = Collections.newSetFromMap<Button>(WeakHashMap())
                    editor.addOpenListener { editButtons.forEach { it.isEnabled = !editor.isOpen } }
                    editor.addCloseListener { editButtons.forEach { it.isEnabled = !editor.isOpen } }
                    val save = Translatable.createButton(Messages.SAVE) {
                        val old = editor.item
                        if (editor.save()) {
                            listDataView.addItemAfter(currentItem, old)
                            listDataView.removeItem(old)
                            appRepository.setCustomColumns(appId, listDataView.items.toList())
                            dataProvider.refreshAll()
                        }
                    }.with { setMarginRight(5.0, SizeUnit.PIXEL) }
                    val cancel = Translatable.createButton(Messages.CANCEL) {
                        editor.cancel()
                        dataProvider.refreshAll()
                    }
                    column(ButtonRenderer(VaadinIcon.EDIT, { editButtons.add(this) }) {
                        editor.editItem(it)
                        pathField.focus()
                        recalculateColumnWidths()
                    }) {
                        key = "edit"
                        editorComponent = Div(save, cancel)
                        setFlexGrow(1)
                    }
                    column(ButtonRenderer(VaadinIcon.TRASH) { customColumn ->
                        listDataView.removeItem(customColumn)
                        appRepository.setCustomColumns(appId, listDataView.items.toList())
                        dataProvider.refreshAll()
                    }) {
                        key = "delete"
                    }
                    appendFooterRow().getCell(columns[0]).component = Translatable.createButton(Messages.ADD_COLUMN) {
                        if (!editor.isOpen) {
                            val customColumn = CustomColumn("", "")
                            listDataView.addItem(customColumn)
                            editor.editItem(customColumn)
                        }
                    }
                }
            }
        }
    }
}