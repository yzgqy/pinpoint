import { NgModule } from '@angular/core';
import { MatTooltipModule } from '@angular/material';
import { RouterModule } from '@angular/router';

import { routing } from './inspector-page.routing';
import { SharedModule } from 'app/shared';
import { NoticeModule } from 'app/core/components/notice';
import { ApplicationListModule } from 'app/core/components/application-list';
import { PeriodSelectorModule } from 'app/core/components/period-selector';
import { CommandGroupModule } from 'app/core/components/command-group';
import { ApplicationInspectorTitleModule } from 'app/core/components/application-inspector-title';
import { ServerAndAgentListModule } from 'app/core/components/server-and-agent-list';
import { AgentSearchInputModule } from 'app/core/components/agent-search-input';
import { ApplicationInspectorContentsModule } from 'app/core/components/application-inspector-contents';
import { AgentInspectorContentsModule } from 'app/core/components/agent-inspector-contents';
import { EmptyInspectorContentsModule } from 'app/core/components/empty-inspector-contents';
import { InspectorPageComponent } from './inspector-page.component';
import { HelpViewerPopupModule } from 'app/core/components/help-viewer-popup';

@NgModule({
    declarations: [
        InspectorPageComponent
    ],
    imports: [
        MatTooltipModule,
        SharedModule,
        NoticeModule,
        ApplicationListModule,
        PeriodSelectorModule,
        CommandGroupModule,
        ApplicationInspectorTitleModule,
        ServerAndAgentListModule,
        ApplicationInspectorContentsModule,
        AgentInspectorContentsModule,
        AgentSearchInputModule,
        EmptyInspectorContentsModule,
        HelpViewerPopupModule,
        RouterModule.forChild(routing)
    ],
    exports: [],
    providers: []
})
export class InspectorPageModule {}
