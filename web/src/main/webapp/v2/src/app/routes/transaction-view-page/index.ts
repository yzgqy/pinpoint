import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared';
import { ApplicationListModule } from 'app/core/components/application-list';
import { NoticeModule } from 'app/core/components/notice';
import { CommandGroupModule } from 'app/core/components/command-group';
import { TransactionShortInfoModule } from 'app/core/components/transaction-short-info';
import { TransactionViewTopContentsModule } from 'app/core/components/transaction-view-top-contents';
import { TransactionViewBottomContentsModule } from 'app/core/components/transaction-view-bottom-contents';

import { AngularSplitModule } from 'app/core/components/angular-split/angular-split';
import { TransactionViewPageComponent } from './transaction-view-page.component';
import { routing } from './transaction-view-page.routing';

@NgModule({
    declarations: [
        TransactionViewPageComponent
    ],
    imports: [
        AngularSplitModule,
        SharedModule,
        ApplicationListModule,
        NoticeModule,
        CommandGroupModule,
        TransactionShortInfoModule,
        TransactionViewTopContentsModule,
        TransactionViewBottomContentsModule,
        RouterModule.forChild(routing)
    ],
    exports: [
    ],
    providers: []
})
export class TransactionViewPageModule {}
