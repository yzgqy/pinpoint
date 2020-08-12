import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared';
import { AngularSplitModule } from 'app/core/components/angular-split/angular-split';
import { NoticeModule } from 'app/core/components/notice';
import { ApplicationListModule } from 'app/core/components/application-list';
import { DataLoadIndicatorModule } from 'app/core/components/data-load-indicator';
import { StateButtonModule } from 'app/core/components/state-button';
import { CommandGroupModule } from 'app/core/components/command-group';
import { TransactionTableGridModule } from 'app/core/components/transaction-table-grid';
import { TransactionListBottomContentsModule } from 'app/core/components/transaction-list-bottom-contents';

import { TransactionListEmptyComponent } from './transaction-list-empty.component';
import { TransactionListPageComponent } from './transaction-list-page.component';
import { routing } from './transaction-list-page.routing';

@NgModule({
    declarations: [
        TransactionListEmptyComponent,
        TransactionListPageComponent
    ],
    imports: [
        AngularSplitModule,
        SharedModule,
        NoticeModule,
        ApplicationListModule,
        DataLoadIndicatorModule,
        StateButtonModule,
        CommandGroupModule,
        TransactionTableGridModule,
        TransactionListBottomContentsModule,
        RouterModule.forChild(routing)
    ],
    exports: [],
    providers: []
})
export class TransactionListPageModule {}
