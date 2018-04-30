/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
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
package domainapp.fixture.scenarios;

import org.incode.module.docrendering.freemarker.fixture.RenderingStrategyFSForFreemarker;
import org.incode.module.docrendering.stringinterpolator.fixture.RenderingStrategyFSForStringInterpolator;
import org.incode.module.docrendering.xdocreport.fixture.RenderingStrategyFSForXDocReportToPdf;
import org.incode.module.document.fixture.DocumentTemplateFSAbstract;

public class RenderingStrategiesFixture extends DocumentTemplateFSAbstract {

    public static final String REF_SI = RenderingStrategyFSForStringInterpolator.REF;
    public static final String REF_FMK = RenderingStrategyFSForFreemarker.REF;
    public static final String REF_XDP = RenderingStrategyFSForXDocReportToPdf.REF;


    @Override
    protected void execute(final ExecutionContext executionContext) {

        // prereqs
        executionContext.executeChild(this, new RenderingStrategyFSForStringInterpolator());
        executionContext.executeChild(this, new RenderingStrategyFSForFreemarker());
        executionContext.executeChild(this, new RenderingStrategyFSForXDocReportToPdf());

    }


}
