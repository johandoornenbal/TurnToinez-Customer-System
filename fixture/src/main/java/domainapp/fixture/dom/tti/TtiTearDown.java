/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package domainapp.fixture.dom.tti;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.jdosupport.IsisJdoSupport;

public class TtiTearDown extends FixtureScript {

    @Override
    protected void execute(ExecutionContext executionContext) {

        isisJdoSupport.executeUpdate("delete from \"mailchimp\".\"MailChimpListMemberLink\"");
        isisJdoSupport.executeUpdate("delete from \"mailchimp\".\"MailChimpMember\"");
        isisJdoSupport.executeUpdate("delete from \"mailchimp\".\"MailChimpList\"");

        isisJdoSupport.executeUpdate("delete from \"tti\".\"Communicatie\"");

        isisJdoSupport.executeUpdate("delete from \"tti\".\"NotitieLink\"");
        isisJdoSupport.executeUpdate("delete from \"tti\".\"Notitie\"");

        isisJdoSupport.executeUpdate("delete from \"financieleadministratie\".\"Boeking\"");
        isisJdoSupport.executeUpdate("delete from \"financieleadministratie\".\"KostenPost\"");
        isisJdoSupport.executeUpdate("delete from \"tti\".\"PaperclipForFactuur\"");

        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"Paperclip\"");
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"Applicability\"");
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"Document\"");
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"DocumentTemplate\"");
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"DocumentAbstract\"");
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"DocumentType\"");
        isisJdoSupport.executeUpdate("delete from \"incodeDocuments\".\"RenderingStrategy\"");

        isisJdoSupport.executeUpdate("delete from \"tti\".\"Numerator\"");

        isisJdoSupport.executeUpdate("delete from \"tti\".\"FormulierVanSite\"");
        isisJdoSupport.executeUpdate("delete from \"tti\".\"FactuurRegel\"");
        isisJdoSupport.executeUpdate("delete from \"tti\".\"Factuur\"");
        isisJdoSupport.executeUpdate("delete from \"tti\".\"Regel\"");
        isisJdoSupport.executeUpdate("delete from \"tti\".\"Bestelling\"");
        isisJdoSupport.executeUpdate("delete from \"tti\".\"Klant\"");
    }


    @javax.inject.Inject
    private IsisJdoSupport isisJdoSupport;

}
