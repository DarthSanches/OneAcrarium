/*
 * (C) Copyright 2018-2021 Lukas Morawietz (https://github.com/F43nd1r)
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
package com.faendir.acra.ui.component

import com.faendir.acra.util.tryOrNull
import com.vaadin.flow.component.customfield.CustomField
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import org.springframework.util.StreamUtils
import java.nio.charset.Charset

class UploadField : CustomField<String?>() {
    private val buffer: MemoryBuffer = MemoryBuffer()

    init {
        val upload = Upload(buffer)
        upload.addSucceededListener { value = generateModelValue() }
        add(upload)
    }

    override fun generateModelValue(): String? = tryOrNull { StreamUtils.copyToString(buffer.inputStream, Charset.defaultCharset()) }

    override fun setPresentationValue(newPresentationValue: String?) {}
}