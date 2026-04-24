/*
 * Copyright (C) 2022 Atlas of Living Australia
 * All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 */

package au.org.ala.userdetails

import grails.converters.JSON
import grails.plugin.cache.Cacheable
import grails.gorm.transactions.NotTransactional
import org.grails.web.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.core.io.Resource

class LocationService {

    @Autowired
    MessageSource messageSource

    @Value('${attributes.states.path}')
    Resource states

    @NotTransactional
    @Cacheable("states")
    JSONObject getStatesAndCountries() {
        return states.inputStream.withReader('UTF-8') { reader ->
            (JSONObject) JSON.parse(reader)
        }
    }

    Map<String,String> affiliationSurvey(Locale locale) {
        // Use ala.affiliations.$key for i18N
        //NBN version:
        [
                'Academia and research': 'Academia and research',
                'Agriculture, forestry and fishing': 'Agriculture, forestry and fishing',
                'Artist, journalist, photographer':'Artist, journalist, photographer',
                'College and university student':'College and university student',
                'Construction, utilities and technology': 'Construction, utilities and technology',
                'Ecologist': 'Ecologist',
                'Environmental consultant': 'Environmental consultant',
                'LERC staff': 'LERC staff',
                'Local and national government': 'Local and national government',
                'Museums, botanical gardens and aquaria': 'Museums, botanical gardens and aquaria',
                'NGO staff': 'NGO staff',
                'Personal interest': 'Personal interest',
                'Recorder and recording scheme': 'Recorder and recording scheme',
                'Teaching and education': 'Teaching and education'
        ]
    }
}
