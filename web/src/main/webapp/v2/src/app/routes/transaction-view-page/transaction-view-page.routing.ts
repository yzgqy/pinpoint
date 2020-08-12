import { Routes } from '@angular/router';
import { UrlPath, UrlPathId } from 'app/shared/models';
import { TransactionViewTopContentsContainerComponent } from 'app/core/components/transaction-view-top-contents/transaction-view-top-contents-container.component';
import { SystemConfigurationResolverService, ApplicationListResolverService } from 'app/shared/services';
import { TransactionViewPageComponent } from './transaction-view-page.component';

const TO_MAIN = '/' + UrlPath.MAIN;

export const routing: Routes = [
    {
        path: '',
        component: TransactionViewPageComponent,
        resolve: {
            configuration: SystemConfigurationResolverService,
            applicationList: ApplicationListResolverService
        },
        children: [
            {
                path: '',
                redirectTo: TO_MAIN,
                pathMatch: 'full'
            },
            {
                path: ':' + UrlPathId.AGENT_ID,
                redirectTo: TO_MAIN,
                pathMatch: 'full'
            },
            {
                path: ':' + UrlPathId.AGENT_ID + '/:' + UrlPathId.TRACE_ID,
                redirectTo: TO_MAIN,
                pathMatch: 'full'
            },
            {
                path: ':' + UrlPathId.AGENT_ID + '/:' + UrlPathId.TRACE_ID + '/:' + UrlPathId.FOCUS_TIMESTAMP,
                redirectTo: TO_MAIN,
                pathMatch: 'full'
            },
            {
                path: ':' + UrlPathId.AGENT_ID + '/:' + UrlPathId.TRACE_ID + '/:' + UrlPathId.FOCUS_TIMESTAMP + '/:' + UrlPathId.SPAN_ID,
                children: [
                    {
                        path: '',
                        component: TransactionViewTopContentsContainerComponent
                    }
                ]
            }
        ]
    }
];
